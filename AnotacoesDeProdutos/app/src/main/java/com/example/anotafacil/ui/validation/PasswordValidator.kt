package com.example.anotafacil.ui.validation



object PasswordValidator {
    fun isValidPassword(password: String): String? {
        if (password.isBlank()) return "Senha não pode estar vazia"

        if (password.length < 8) return "Senha deve ter no mínimo 8 caracteres"

        return null
    }
}