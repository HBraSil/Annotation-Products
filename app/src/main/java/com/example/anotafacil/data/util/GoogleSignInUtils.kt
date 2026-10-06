package com.example.anotafacil.data.util

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.hilquias.anotafacil.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class GoogleSignInUtils @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    suspend fun doGoogleSingIn(): Result<AuthCredential> {
        val credentialManager = CredentialManager.create(context)
        Log.d("GoogleSignInUtils", "credentialManager: $credentialManager")
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(getCredentialOptions(context))
            .build()
        Log.d("GoogleSignInUtils", "request: $request")

        return try {
            val credentialResponse = credentialManager.getCredential(context, request)
            Log.d("GoogleSignInUtils", "credentialResponse: $credentialResponse")
            val credential = credentialResponse.credential
            Log.d("GoogleSignInUtils", "credential: $credential")

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                return Result.success(authCredential)
            }

            Result.failure(Exception("Credencial inválida"))
        } catch (e: NoCredentialException) {
            Log.d("GoogleSignInUtils", "NoCredentialException: ${e.stackTrace}")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.d("GoogleSignInUtils", "GetCredentialException: ${e.stackTrace}")
            Result.failure(e)
        }
    }


    /*fun getIntent(): Intent {
        return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
    }*/

    private fun getCredentialOptions(context: Context): CredentialOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId(context.getString(R.string.android_client_id))
            .build()
    }
}