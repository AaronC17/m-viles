
package com.example.moviles_proyecto

data class Comment(
    val user: String = "", // Nombre del usuario
    val commentText: String = "", // Texto del comentario
    val rating: Int = 0, // Calificación del comentario (1-5)
    val timestamp: Long = System.currentTimeMillis() // Marca de tiempo
)
