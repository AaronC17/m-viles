package com.example.moviles_proyecto

data class ResearchWork(
    val title: String = "",
    val area: String = "",
    val description: String = "",
    val conclusions: String = "",
    val recommendations: String = "",
    val authorId: String = "", // UID del autor
    val authorName: String = "Anónimo" // Nombre del autor
)
