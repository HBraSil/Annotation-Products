package com.example.anotafacil.data.sync

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor(
    private val syncCoordinator: SyncCoordinator
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        syncCoordinator.sync()
    }
}