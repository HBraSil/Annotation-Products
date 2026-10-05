package com.hilquias.anotafacil

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.anotafacil.data.util.AppForegroundManager
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appForegroundManager: AppForegroundManager

    override fun onCreate() {
        super.onCreate()
        Log.d(
            "APP_LIFECYCLE",
            "Application manager = ${
                System.identityHashCode(appForegroundManager)
            }"
        )

        ProcessLifecycleOwner
            .get()
            .lifecycle
                .addObserver(appForegroundManager)
    }
}