package com.example.moviles_proyecto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddEditResearchWorkFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var titleEditText: EditText
    private lateinit var areaSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var conclusionsEditText: EditText
    private lateinit var recommendationsEditText: EditText
    private lateinit var saveWorkButton: Button
    private lateinit var uploadPdfButton: Button
    private lateinit var addImageButton: Button
    private lateinit var imagesRecyclerView: RecyclerView

    private var pdfUri: Uri? = null
    private val imageUris = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit_research_work, container, false)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        titleEditText = view.findViewById(R.id.titleEditText)
        areaSpinner = view.findViewById(R.id.areaSpinner)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        conclusionsEditText = view.findViewById(R.id.conclusionsEditText)
        recommendationsEditText = view.findViewById(R.id.recommendationsEditText)
        saveWorkButton = view.findViewById(R.id.saveWorkButton)
        uploadPdfButton = view.findViewById(R.id.uploadPdfButton)
        addImageButton = view.findViewById(R.id.addImageButton)
        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView)

        // Configurar Spinner para las áreas de investigación
        val areas = listOf("Matemáticas", "Biología", "Ciencias Sociales")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, areas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        areaSpinner.adapter = adapter

        // Configurar RecyclerView para imágenes
        imagesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val imageAdapter = ImageAdapter(imageUris)
        imagesRecyclerView.adapter = imageAdapter

        // Selección de PDF
        uploadPdfButton.setOnClickListener { selectPdf() }

        // Agregar imagen
        addImageButton.setOnClickListener { selectImage() }

        // Guardar trabajo
        saveWorkButton.setOnClickListener {
            if (validateInputs()) {
                reloadUserAndSaveWork()
            }
        }

        return view
    }

    private fun selectPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        pdfPickerLauncher.launch(intent)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imagePickerLauncher.launch(intent)
    }

    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                pdfUri = result.data?.data
                Toast.makeText(context, "PDF seleccionado", Toast.LENGTH_SHORT).show()
            }
        }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        imageUris.add(imageUri)
                    }
                } ?: result.data?.data?.let { singleUri ->
                    imageUris.add(singleUri)
                }
                imagesRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

    private fun validateInputs(): Boolean {
        if (pdfUri == null) {
            Toast.makeText(context, "Por favor, selecciona un PDF", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageUris.size < 3 || imageUris.size > 6) {
            Toast.makeText(context, "Por favor, selecciona entre 3 y 6 imágenes", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun reloadUserAndSaveWork() {
        val currentUser = auth.currentUser
        currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val authorName = currentUser.displayName ?: "Anónimo"
                uploadImagesToStorage { imageUrls ->
                    saveResearchWork(authorName, imageUrls)
                }
            } else {
                Toast.makeText(context, "Error al recargar los datos del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImagesToStorage(onComplete: (List<String>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Debes iniciar sesión para subir imágenes", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUrls = mutableListOf<String>()
        val storageRef = FirebaseStorage.getInstance().reference
        var uploadedCount = 0

        for ((index, uri) in imageUris.withIndex()) {
            val imageRef = storageRef.child("research_images/${currentUser.uid}/${System.currentTimeMillis()}_${index}.jpg")
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { url ->
                        imageUrls.add(url.toString())
                        uploadedCount++
                        if (uploadedCount == imageUris.size) {
                            onComplete(imageUrls)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al subir imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun saveResearchWork(authorName: String, imageUrls: List<String>) {
        val title = titleEditText.text.toString().trim()
        val area = areaSpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString().trim()
        val conclusions = conclusionsEditText.text.toString().trim()
        val recommendations = recommendationsEditText.text.toString().trim()

        val currentUser = auth.currentUser
        val researchWork = hashMapOf(
            "title" to title,
            "area" to area,
            "description" to description,
            "conclusions" to conclusions,
            "recommendations" to recommendations,
            "authorId" to currentUser?.uid,
            "authorName" to authorName,
            "imageUrls" to imageUrls
        )

        firestore.collection("research_works")
            .add(researchWork)
            .addOnSuccessListener {
                Toast.makeText(context, "Trabajo guardado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al guardar el trabajo", Toast.LENGTH_SHORT).show()
            }
    }
}
