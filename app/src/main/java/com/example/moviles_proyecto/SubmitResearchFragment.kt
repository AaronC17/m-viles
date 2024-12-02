package com.example.moviles_proyecto
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.moviles_proyecto.databinding.FragmentSubmitResearchBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class SubmitResearchFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var pdfUri: Uri
    private lateinit var imageUris: List<Uri>
    private lateinit var binding: FragmentSubmitResearchBinding // Declarar el binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar el binding
        binding = FragmentSubmitResearchBinding.inflate(inflater, container, false)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val submitButton = binding.submitResearchButton
        submitButton.setOnClickListener {
            submitResearch()
        }

        // Aquí deberías implementar la lógica para elegir y subir el PDF y las imágenes
        return binding.root
    }

    private fun submitResearch() {
        // Ahora puedes usar el binding sin errores de "unresolved binding"
        val title = binding.titleEditText.text.toString().trim()
        val area = binding.areaSpinner.selectedItem.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val conclusions = binding.conclusionsEditText.text.toString().trim()
        val recommendations = binding.recommendationsEditText.text.toString().trim()

        if (title.isEmpty() || area.isEmpty() || description.isEmpty() || conclusions.isEmpty() || recommendations.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        // Subir PDF a Firebase Storage
        val pdfRef = storage.reference.child("research_papers/$userId/${title}.pdf")
        val pdfUploadTask = pdfRef.putFile(pdfUri)

        // Subir imágenes a Firebase Storage
        val imageRefs = mutableListOf<StorageReference>()
        for (imageUri in imageUris) {
            val imageRef = storage.reference.child("research_images/$userId/${UUID.randomUUID()}.jpg")
            imageRefs.add(imageRef)
            imageRef.putFile(imageUri)
        }

        // Guardar los datos en Firestore
        val researchData = hashMapOf(
            "title" to title,
            "area" to area,
            "description" to description,
            "conclusions" to conclusions,
            "recommendations" to recommendations,
            "pdfUri" to pdfRef.downloadUrl.toString(),
            "imageUris" to imageRefs.map { it.downloadUrl.toString() }
        )

        firestore.collection("research_works").add(researchData)
            .addOnSuccessListener {
                Toast.makeText(context, "Trabajo enviado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al enviar el trabajo", Toast.LENGTH_SHORT).show()
            }
    }
}

