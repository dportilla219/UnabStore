package me.davidguerrero.unabstore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    onClickBack: () -> Unit = {},
    onProductoAgregado: () -> Unit = {}
) {
    val productoRepository = ProductoRepository()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf("") }
    var descripcionError by remember { mutableStateOf("") }
    var precioError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            modifier = Modifier // AGREGAR modifier
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Producto") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Nombre",
                        modifier = Modifier
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = nombreError.isNotEmpty(),
                supportingText = {
                    if (nombreError.isNotEmpty()) {
                        Text(nombreError, color = Color.Red)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Descripción",
                        modifier = Modifier
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                isError = descripcionError.isNotEmpty(),
                supportingText = {
                    if (descripcionError.isNotEmpty()) {
                        Text(descripcionError, color = Color.Red)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Precio
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Precio",
                        modifier = Modifier // AGREGAR modifier
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = precioError.isNotEmpty(),
                supportingText = {
                    if (precioError.isNotEmpty()) {
                        Text(precioError, color = Color.Red)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mensaje de éxito
            if (mensajeExito.isNotEmpty()) {
                Text(
                    mensajeExito,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Botón Agregar
            Button(
                onClick = {
                    // Validaciones básicas
                    nombreError = if (nombre.isEmpty()) "El nombre es requerido" else ""
                    descripcionError = if (descripcion.isEmpty()) "La descripción es requerida" else ""
                    precioError = when{
                        precio.isEmpty() -> "El precio es requerido"
                        precio.toDoubleOrNull() == null -> "El precio debe ser un número válido"
                        precio.toDouble() <= 0 -> "El precio debe ser mayor a 0"
                        else -> ""
                    }
                    if (nombreError.isEmpty() && descripcionError.isEmpty() && precioError.isEmpty()) {

                        val precioDouble = precio.toDoubleOrNull() ?: 0.0
                        val nuevoProducto = Producto(
                            nombre = nombre,
                            descripcion = descripcion,
                            precio = precioDouble
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                productoRepository.agregarProducto(nuevoProducto)
                                nombre = ""
                                descripcion = ""
                                precio = ""
                                mensajeExito = "Producto agregado exitosamente"
                                onProductoAgregado()
                            } catch (e: Exception) {
                                precioError = "Error al guardar el producto: ${e.message}"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
            ) {
                Text("Agregar Producto", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}