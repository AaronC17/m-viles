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
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Cargar el fragmento inicial (Lista de trabajos)
        loadFragment(ResearchWorkListFragment())
        updateMenuItems() // Actualizar opciones del menú según el estado del usuario

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
                        Toast.makeText(this, "Inicia sesión para agregar trabajos", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_logout -> {
                    logout() // Llamar a la función de cerrar sesión
                    true
                }
                R.id.nav_login -> {
                    login() // Mostrar mensaje de iniciar sesión o redirigir si es necesario
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

    private fun logout() {
        auth.signOut() // Cerrar la sesión en Firebase
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        updateMenuItems() // Actualizar las opciones del menú dinámicamente
        loadFragment(ResearchWorkListFragment()) // Mantener al usuario en la página principal
    }

    private fun login() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirigir al LoginActivity si no hay un usuario autenticado
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Ya has iniciado sesión", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateMenuItems() {
        val menu = bottomNavigationView.menu
        val currentUser = auth.currentUser

        menu.findItem(R.id.nav_logout).isVisible = currentUser != null
        menu.findItem(R.id.nav_login).isVisible = currentUser == null
    }
}
