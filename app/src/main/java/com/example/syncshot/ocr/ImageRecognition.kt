package com.example.syncshot.ocr

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.util.regex.Pattern
import kotlin.math.abs

/**
 * Data class representing one player's round of golf.
 * - name: Player's name (or default)
 * - playerNumber: Order number (e.g., Player 1, Player 2, etc.)
 * - scores: Array of 18 hole scores (pad with -1 for missing)
 * - par: Par values for each hole (optional)
 */
data class PlayerRound(
    val name: String = "Player",
    val playerNumber: Int,
    val scores: IntArray,
    val par: IntArray?
)

/**
 * Represents a single OCR-detected word along with its bounding box and confidence score.
 */
data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val confidence: Int = 100
)

// Custom exception classes for more specific error reporting
class TesseractInitializationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ImageDecodingException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DataExtractionException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Core OCR processor class that uses Tesseract to extract structured scorecard data.
 */
class ImageRecognition(private val context: Context) {

    /**
     * Public function to process an image and extract players' scores.
     * Returns list of PlayerRound objects or an error via callback.
     */
    fun processScorecardImage(
        imageUri: Uri,
        onResult: (List<PlayerRound>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val tessBaseAPI = initializeTesseract()                     // Load and init Tesseract
            val blocks = extractTextBlocks(tessBaseAPI, imageUri)      // OCR scan into word blocks
            tessBaseAPI.end()                                          // Clean up resources

            val players = parseTextBlocks(blocks)                      // Parse rows into PlayerRound objects
            onResult(players)
        } catch (e: Exception) {
            Log.e("TESSERACT_OCR", "OCR error: ${e.message}", e)
            onError(e)
        }
    }

    /**
     * Sets up the Tesseract engine and loads trained language data.
     */
    private fun initializeTesseract(): TessBaseAPI {
        return try {
            val tessBaseAPI = TessBaseAPI()
            val tessDataPath = File(context.filesDir, "tessdata")

            if (!tessDataPath.exists()) tessDataPath.mkdirs()

            // Copy eng.traineddata from assets to internal storage if missing
            val trainedDataFile = File(tessDataPath, "eng.traineddata")
            if (!trainedDataFile.exists()) {
                context.assets.open("tessdata/eng.traineddata").use { input ->
                    trainedDataFile.outputStream().use { output -> input.copyTo(output) }
                }
            }

            tessBaseAPI.init(context.filesDir.absolutePath, "eng")
            tessBaseAPI
        } catch (e: Exception) {
            throw TesseractInitializationException("Failed to initialize Tesseract", e)
        }
    }

    /**
     * Converts the image to grayscale and performs OCR.
     * Returns a list of words with bounding boxes and confidence.
     */
    private fun extractTextBlocks(tessBaseAPI: TessBaseAPI, imageUri: Uri): List<TextBlock> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw ImageDecodingException("Unable to decode image")
            inputStream?.close()

            val grayscale = toGrayscale(bitmap)
            tessBaseAPI.setImage(grayscale)

            val iterator = tessBaseAPI.resultIterator
            val blocks = mutableListOf<TextBlock>()

            do {
                val word = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                val rect = iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                val confidence = iterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_WORD)

                if (!word.isNullOrBlank() && confidence > 60) {
                    val cleaned = cleanOcrText(word)
                    blocks.add(TextBlock(cleaned, rect, confidence.toInt()))
                }
            } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD))

            blocks
        } catch (e: Exception) {
            throw DataExtractionException("Error extracting OCR text", e)
        }
    }

    /**
     * Parses the OCR blocks into rows, finds par values, and extracts player rows.
     */
    private fun parseTextBlocks(blocks: List<TextBlock>): List<PlayerRound> {
        if (blocks.isEmpty()) return emptyList()

        val rows = groupBlocksIntoRows(blocks)
        val parRow = rows.firstOrNull { row -> row.any { it.equals("par", ignoreCase = true) } }

        val par = parRow
            ?.drop(1)
            ?.mapNotNull { it.toIntOrNull() }
            ?.padTo18()
            ?.toIntArray()

        return rows
            .filterIndexed { index, row -> isPlayerRow(row, index, parRow?.let { rows.indexOf(it) } ?: -1) }
            .mapIndexed { index, row -> processPlayerRow(row, index, par) }
    }

    /**
     * Groups words that are vertically aligned into row-like structures.
     */
    private fun groupBlocksIntoRows(blocks: List<TextBlock>, tolerance: Int = 15): List<List<String>> {
        val sorted = blocks.sortedBy { it.boundingBox.top }
        val rows = mutableListOf<MutableList<TextBlock>>()

        for (block in sorted) {
            val row = rows.find { abs(it.first().boundingBox.top - block.boundingBox.top) < tolerance }
            if (row != null) row.add(block) else rows.add(mutableListOf(block))
        }

        return rows.map { it.sortedBy { b -> b.boundingBox.left }.map { b -> b.text } }
    }

    /**
     * Converts one row of OCR text into a PlayerRound object (name, scores, par).
     */
    private fun processPlayerRow(row: List<String>, index: Int, par: IntArray?): PlayerRound {
        val name = extractPlayerName(row) ?: "Player ${index + 1}"

        val possibleScores = row.filter { isScoreLike(it) }.mapNotNull { it.toIntOrNull() }

        val scores = when {
            possibleScores.size >= 18 -> possibleScores.take(18)
            possibleScores.size >= 9  -> (possibleScores + List(9) { -1 }).take(18)
            else                      -> List(18) { -1 }
        }.toIntArray()

        return PlayerRound(name, index + 1, scores, par)
    }

    /**
     * Determines if a row contains player scores by filtering out headers, tee rows, and distance rows.
     */
    private fun isPlayerRow(row: List<String>, index: Int, parRowIndex: Int): Boolean {
        val first = row.firstOrNull()?.lowercase() ?: ""

        val isParRow = index == parRowIndex
        val isHeader = first == "player" && row.drop(1).count { it.toIntOrNull() != null } > 5
        val isTeeRow = listOf("black", "blue", "white", "red", "gold", "silver").any { first.startsWith(it) }
        val isHoleRow = first.contains("hole")

        val numbers = row.drop(1).mapNotNull { it.toIntOrNull() }
        val isDistanceRow = numbers.size > 3 && numbers.count { it > 50 } >= numbers.size - 1

        return !isParRow && !isHeader && !isTeeRow && !isHoleRow && !isDistanceRow && numbers.count { it > -1 } >= 2
    }

    /**
     * Extracts the first valid name string from a row.
     */
    private fun extractPlayerName(row: List<String>): String? {
        val pattern = Pattern.compile("^[a-zA-Z\\s]+\$")
        return row.firstOrNull { pattern.matcher(it).matches() }
    }

    /**
     * Determines if a string looks like a valid score (digits, optional minus).
     */
    private fun isScoreLike(text: String): Boolean {
        return text.matches("-?\\d+".toRegex())
    }

    /**
     * Fixes common OCR misreads (e.g., "S" -> "5", "O" -> "0").
     */
    private fun cleanOcrText(text: String): String {
        return text
            .replace("S", "5", ignoreCase = true)
            .replace("O", "0")
            .replace("l", "1")
            .trim()
    }

    /**
     * Converts any image to grayscale to improve Tesseract's accuracy.
     */
    private fun toGrayscale(src: Bitmap): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bmpGrayscale
    }

    /**
     * Pads a list to exactly 18 elements (if fewer), using a fallback value.
     */
    private fun List<Int>.padTo18(pad: Int = 0): List<Int> {
        return this.toMutableList().apply {
            while (size < 18) add(pad)
        }
    }
}

