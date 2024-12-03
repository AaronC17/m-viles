package com.example.moviles_proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Cargar el fragmento inicial (Lista de trabajos)
        loadFragment(ResearchWorkListFragment())

        // Configurar navegación
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_work_list -> {
                    loadFragment(ResearchWorkListFragment()) // Modo visitante: Lista de trabajos
                    true
                }
                R.id.nav_add_work -> {
                    // Verificar si el usuario está autenticado antes de permitir agregar trabajos
                    if (auth.currentUser != null) {
                        loadFragment(AddEditResearchWorkFragment())
                    } else {
                        redirectToLogin() // Redirigir al login si no está autenticado
                    }
                    true
                }
                R.id.nav_logout -> {
                    logout() // Llamar a la función de cerrar sesión
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpiar historial
        startActivity(intent)
        finish() // Cerrar la actividad actual
    }

    private fun logout() {
        auth.signOut() // Cerrar la sesión en Firebase
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        redirectToLogin() // Redirigir al LoginActivity
    }
}
