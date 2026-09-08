package com.example.anotafacil.ui.validation

import android.util.Patterns

object EmailValidator {
    fun validate(email: String): String? {
        if (email.isBlank()) {
            return "O campo de e-mail não pode estar vazio."
        }

        if (!isEmailValid(email)) {
            return "O e-mail fornecido é inválido."
        }

        return null
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}