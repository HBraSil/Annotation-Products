package com.example.anotafacil.ui.validation



object PasswordValidator {
    fun isValidPassword(password: String): String? {
        if (password.isBlank()) return "Senha não pode estar vazia"

        if (password.length < 8) return "Senha deve ter no mínimo 8 caracteres"

        return null
    }

    fun isValidConfirmPassword(password: String, confirmPassword: String): String? {
        if (password.isBlank()) return "Senha não pode estar vazia"

        if (password.length < 8) return "Senha deve ter no mínimo 8 caracteres"

        if (confirmPassword.isNotBlank() && confirmPassword != password) return "As senhas não coincidem"


        return null
    }
}