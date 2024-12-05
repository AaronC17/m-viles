package com.example.moviles_proyecto

data class ResearchWork(
    var id: String = "", // Campo para almacenar el ID del documento
    val title: String = "",
    val area: String = "",
    val description: String = "",
    val conclusions: String = "",
    val recommendations: String = "",
    val imageUrls: List<String> = listOf(),
    val pdfUrl: String = "",
    val authorName: String = "",
    val subject: String = "" // Nuevo campo para la materia
)
