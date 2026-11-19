package com.example.anotafacil.data.firestore.model

data class FirestoreUser(
    val name: String = "",
    val email: String = "",
    val ownerId: String? = null
)