package com.example.syncshot

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.example.syncshot.ocr.TesseractHelper

class CameraApplication : Application(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        // Initialize Tesseract as soon as the app starts
        TesseractHelper.init(this)
        Log.i("CameraApp", "Tesseract initialized with eng.traineddata")
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder
            .fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR)
            .build()
    }
}
