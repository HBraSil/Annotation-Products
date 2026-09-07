package com.example.anotafacil.data.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.anotafacil.domain.model.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.hilquias.anotafacil.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class GoogleSignInUtils @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    suspend fun doGoogleSingIn(): Result<AuthCredential> {
        val credentialManager = CredentialManager.create(context)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getCredentialOptions(context))
            .build()

        return try {
            val credentialResponse = credentialManager.getCredential(context, request)

            when (val credential = credentialResponse.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                        return Result.success(authCredential)
                    }
                }
            }
            Result.failure(Exception("Erro ao fazer login com Google"))
        } catch (e: NoCredentialException) {
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        }
    }


    fun getIntent(): Intent {
        return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
    }

    private fun getCredentialOptions(context: Context): CredentialOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(context.getString(R.string.android_client_id))
            .build()
    }
}