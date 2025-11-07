package me.davidguerrero.unabstore

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db: FirebaseFirestore = Firebase.firestore
    private val productosCollection = db.collection("productos")

    suspend fun agregarProducto(producto: Producto): String {
        val document = productosCollection.add(producto).await()
        return document.id
    }

    suspend fun obtenerProductos(): List<Producto> {
        val querySnapshot = productosCollection.get().await()
        return querySnapshot.documents.map { document ->
            Producto(
                id = document.id,
                nombre = document.getString("nombre") ?: "",
                descripcion = document.getString("descripcion") ?: "",
                precio = document.getDouble("precio") ?: 0.0
            )
        }
    }

    suspend fun eliminarProducto(id: String) {
        productosCollection.document(id).delete().await()
    }
}