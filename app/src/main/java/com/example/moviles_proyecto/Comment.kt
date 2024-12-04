data class Comment(
    val user: String = "", // Nombre del usuario
    val commentText: String = "", // Texto del comentario
    val timestamp: Long = System.currentTimeMillis() // Marca de tiempo para ordenar
)
