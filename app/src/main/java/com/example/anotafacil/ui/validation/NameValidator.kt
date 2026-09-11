package com.example.anotafacil.ui.validation

object NameValidator {
    fun isNameValid(name: String): String? {
        if (name.isBlank()) {
            return "O campo nome não pode estar em branco"
        }

        if (name.length < 4) {
            return "O nome deve ter pelo menos 4 caracteres"
        }

        return null
    }
}