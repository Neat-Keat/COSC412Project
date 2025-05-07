// app/src/main/java/com/example/syncshot/ocr/TesseractHelper.kt
package com.example.syncshot.ocr

import android.app.Application
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File

object TesseractHelper {
    private const val LANG = "eng"
    private lateinit var api: TessBaseAPI

    /** Copy traineddata from assets → filesDir/tessdata, then init API */
    fun init(app: Application) {
        val dataPath = app.filesDir.absolutePath
        val tessFolder = File(dataPath, "tessdata")
        if (!tessFolder.exists()) {
            tessFolder.mkdirs()
            // copy the .traineddata file from assets
            app.assets.open("tessdata/$LANG.traineddata").use { input ->
                File(tessFolder, "$LANG.traineddata")
                    .outputStream()
                    .use { it.write(input.readBytes()) }
            }
        }
        api = TessBaseAPI().apply { init(dataPath, LANG) }
    }

    /** Run OCR on a Bitmap and return the trimmed result */
    fun extractText(bitmap: Bitmap): String {
        api.setImage(bitmap)
        return api.utF8Text?.trim() ?: ""
    }
}
