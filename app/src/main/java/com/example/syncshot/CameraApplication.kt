package com.example.syncshot

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

//default camera Application for the app to use (as defined in the AndroidManifest.xml file)
class CameraApplication : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR) // Optional: Limit logging
            .build()
    }
}