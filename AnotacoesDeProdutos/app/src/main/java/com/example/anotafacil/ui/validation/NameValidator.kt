package com.example.anotafacil.ui.validation

object NameValidator {
    fun isNameValid(name: String): String? {
        if (name.isBlank()) {
            return "O nome não pode estar em branco"
        }

        if (name.length < 3) {
            return "O nome deve ter pelo menos 3 caracteres"
        }

        return null
    }
}