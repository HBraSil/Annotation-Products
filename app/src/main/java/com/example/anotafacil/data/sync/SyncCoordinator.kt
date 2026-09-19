package com.example.anotafacil.data.sync

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncCoordinator @Inject constructor(
    private val syncManager: SyncManager,
    private val auth: FirebaseAuth
) {

    private var isSyncing = false


    fun sync() {
        if (auth.currentUser == null || isSyncing) return


        isSyncing = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                syncManager.sync()
            } finally {
                isSyncing = false
            }
        }
    }
}