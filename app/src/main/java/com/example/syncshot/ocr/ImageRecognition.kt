package com.example.syncshot.ocr

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern
import kotlin.math.abs

// Data classes

data class PlayerRound(
    val name: String = "Player",
    val playerNumber: Int,
    val scores: IntArray,
    val par: IntArray?
)

data class TextBlock(
    val text: String,
    val boundingBox: Rect,
    val confidence: Int = 100
)

// Custom Exceptions

class TesseractInitializationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ImageDecodingException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DataExtractionException(message: String, cause: Throwable? = null) : Exception(message, cause)

// OCR Image Processor

class ImageRecognition(private val context: Context) {

    fun processScorecardImage(
        imageUri: Uri,
        onResult: (List<PlayerRound>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val tessBaseAPI = initializeTesseract()
            val blocks = extractTextBlocks(tessBaseAPI, imageUri)
            tessBaseAPI.end()

            val players = parseTextBlocks(blocks)
            onResult(players)
        } catch (e: Exception) {
            Log.e("TESSERACT_OCR", "OCR error: ${e.message}", e)
            onError(e)
        }
    }

    private fun initializeTesseract(): TessBaseAPI {
        return try {
            val tessBaseAPI = TessBaseAPI()
            val tessDataPath = File(context.filesDir, "tessdata")
            if (!tessDataPath.exists()) tessDataPath.mkdirs()

            val trainedDataFile = File(tessDataPath, "eng.traineddata")
            if (!trainedDataFile.exists()) {
                context.assets.open("tessdata/eng.traineddata").use { input ->
                    trainedDataFile.outputStream().use { output -> input.copyTo(output) }
                }
            }

            if (!tessBaseAPI.init(context.filesDir.absolutePath, "eng")) {
                throw TesseractInitializationException("Tesseract init failed")
            }

            tessBaseAPI
        } catch (e: Exception) {
            throw TesseractInitializationException("Failed to initialize Tesseract", e)
        }
    }

    private fun extractTextBlocks(tessBaseAPI: TessBaseAPI, imageUri: Uri): List<TextBlock> {
        return try {
            Log.d("OCR", "Opening input stream from URI: $imageUri")
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw ImageDecodingException("Input stream was null – possible content URI issue")

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw ImageDecodingException("Bitmap decode failed")
            inputStream.close()

            val resizedBitmap = resizeBitmapIfNeeded(originalBitmap, maxWidth = 1000)
            Log.d("OCR", "Bitmap dimensions: ${resizedBitmap.width}x${resizedBitmap.height}")

            val grayscale = toGrayscale(resizedBitmap)
            Log.d("OCR", "Grayscale dimensions: ${grayscale.width}x${grayscale.height}")

            // Optional: Save grayscale image for debugging
            val debugFile = File(context.cacheDir, "grayscale_debug.png")
            grayscale.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(debugFile))
            Log.d("OCR", "Saved grayscale image at: ${debugFile.absolutePath}")

            tessBaseAPI.setImage(grayscale)
            Log.d("OCR", "Tesseract image set. Attempting to extract...")

            val iterator = tessBaseAPI.resultIterator
                ?: throw DataExtractionException("No OCR results found")

            val blocks = mutableListOf<TextBlock>()

            do {
                val word = iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                val rect = try {
                    iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD)
                } catch (e: Exception) {
                    Log.e("OCR", "Bounding box retrieval failed", e)
                    continue
                }
                val confidence = iterator.confidence(TessBaseAPI.PageIteratorLevel.RIL_WORD)

                if (!word.isNullOrBlank() && confidence > 30) {  // Lowered threshold to catch more text
                    val cleaned = cleanOcrText(word)
                    blocks.add(TextBlock(cleaned, rect, confidence.toInt()))
                }
            } while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD))

            Log.d("OCR", "Total text blocks extracted: ${blocks.size}")
            blocks
        } catch (e: Exception) {
            Log.e("OCR", "Exception during extractTextBlocks: ${e.message}", e)
            throw DataExtractionException("Error extracting OCR text", e)
        }
    }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxWidth: Int): Bitmap {
        return if (bitmap.width > maxWidth) {
            val ratio = bitmap.height.toFloat() / bitmap.width
            val targetHeight = (maxWidth * ratio).toInt()
            Bitmap.createScaledBitmap(bitmap, maxWidth, targetHeight, true)
        } else {
            bitmap
        }
    }

    private fun parseTextBlocks(blocks: List<TextBlock>): List<PlayerRound> {
        if (blocks.isEmpty()) return emptyList()

        val rows = groupBlocksIntoRows(blocks)
        val parRow = rows.firstOrNull { row -> row.any { it.equals("par", ignoreCase = true) } }

        val par = parRow?.drop(1)?.mapNotNull { it.toIntOrNull() }?.padTo18()?.toIntArray()

        return rows
            .filterIndexed { index, row -> isPlayerRow(row, index, parRow?.let { rows.indexOf(it) } ?: -1) }
            .mapIndexed { index, row -> processPlayerRow(row, index, par) }
    }

    private fun groupBlocksIntoRows(blocks: List<TextBlock>, tolerance: Int = 15): List<List<String>> {
        val sorted = blocks.sortedBy { it.boundingBox.top }
        val rows = mutableListOf<MutableList<TextBlock>>()

        for (block in sorted) {
            val row = rows.find { abs(it.first().boundingBox.top - block.boundingBox.top) < tolerance }
            if (row != null) row.add(block) else rows.add(mutableListOf(block))
        }

        return rows.map { it.sortedBy { b -> b.boundingBox.left }.map { b -> b.text } }
    }

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

    private fun extractPlayerName(row: List<String>): String? {
        val pattern = Pattern.compile("^[a-zA-Z\\s]+")
        return row.firstOrNull { pattern.matcher(it).matches() }
    }

    private fun isScoreLike(text: String): Boolean {
        return text.matches("-?\\d+".toRegex())
    }

    private fun cleanOcrText(text: String): String {
        return text.replace("S", "5", ignoreCase = true)
            .replace("O", "0")
            .replace("l", "1")
            .trim()
    }

    private fun toGrayscale(src: Bitmap): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bmpGrayscale
    }

    private fun List<Int>.padTo18(pad: Int = 0): List<Int> {
        return this.toMutableList().apply {
            while (size < 18) add(pad)
        }
    }
}

