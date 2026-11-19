package com.hilquias.anotafacil

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.anotafacil.data.util.AppForegroundManager
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
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

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        ProcessLifecycleOwner
            .get()
            .lifecycle
                .addObserver(appForegroundManager)
    }
}