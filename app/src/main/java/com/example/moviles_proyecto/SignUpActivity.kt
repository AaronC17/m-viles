package com.example.moviles_proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val gradeEditText = findViewById<EditText>(R.id.gradeEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val grade = gradeEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || grade.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear el usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Actualizar el perfil del usuario para agregar el displayName
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Guardar datos adicionales en Firestore
                                saveUserToFirestore(user.uid, name, email, grade, description)
                            } else {
                                Toast.makeText(this, "Error al guardar el perfil del usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveUserToFirestore(userId: String, name: String, email: String, grade: String, description: String) {
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "grade" to grade,
            "description" to description
        )

        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Registro exitoso y datos guardados", Toast.LENGTH_SHORT).show()
                finish() // Cerrar la actividad y volver al Login
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar datos en Firestore: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
