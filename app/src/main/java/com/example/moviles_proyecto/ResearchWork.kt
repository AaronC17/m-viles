package com.example.moviles_proyecto

data class ResearchWork(
    val title: String = "",
    val area: String = "",
    val description: String = "",
    val pdfUri: String = "",
    val imageUris: List<String> = emptyList(),
    val conclusions: String = "",
    val recommendations: String = ""
)
