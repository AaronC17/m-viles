package com.example.moviles_proyecto

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está logueado, si no, redirigir al login
        if (auth.currentUser == null) {
            // Si no está logueado, ir al LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Cierra MainActivity para evitar que el usuario regrese con el botón atrás
        }

        // Configurar el botón de "Ver trabajos de investigación"
        val viewResearchButton = findViewById<Button>(R.id.viewResearchButton)
        viewResearchButton.setOnClickListener {
            // Cargar el fragmento de trabajos de investigación
            loadResearchListFragment()
        }

        // Configurar el botón de "Subir Foto"
        val uploadPhotoButton = findViewById<Button>(R.id.uploadPhotoButton)
        uploadPhotoButton.setOnClickListener {
            // Lógica para abrir una actividad de cámara o galería
            // Si deseas usar un Intent para la galería:
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        // Configurar el botón de "Cerrar sesión"
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Cerrar sesión de Firebase
            auth.signOut()
            // Redirigir al LoginActivity después de cerrar sesión
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadResearchListFragment() {
        // Verifica si el fragmento ya está cargado para evitar duplicados
        val fragment = ResearchListFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)  // Usa el contenedor de fragmentos de tu actividad
        transaction.addToBackStack(null)  // Agrega al back stack para poder navegar hacia atrás
        transaction.commit()
    }

    // Para manejar el resultado del picker de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            // Puedes manejar el URI de la imagen y subirla a Firebase Storage o Firestore
            Toast.makeText(this, "Imagen seleccionada: $imageUri", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 1001 // Código para identificar el resultado de la imagen
    }
}
