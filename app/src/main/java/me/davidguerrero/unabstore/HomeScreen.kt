package me.davidguerrero.unabstore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp

import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onClickLogout: () -> Unit={},
    onClickAgregarProducto: () -> Unit = {}
) {
    val auth = Firebase.auth
    val user = auth.currentUser
    val productoRepository = ProductoRepository()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isLoading by remember {mutableStateOf(true)}

    LaunchedEffect(key1 = Unit) {
        try {
            productos = productoRepository.obtenerProductos()
        } catch (e: Exception) {
            // Manejar error
        } finally {
            isLoading = false
        }
    }

    fun recargarProductos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                productos = productoRepository.obtenerProductos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Unab Shop",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Notifications, "Notificaciones")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.ShoppingCart, "Carrito")
                    }
                    IconButton(onClick = {
                        auth.signOut()
                        onClickLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFFF9900),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickAgregarProducto,
                containerColor = Color(0xFFFF9900)
            ) {
                Icon(Icons.Filled.Add, "Agregar Producto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido, ${user?.email ?: "Usuario"}",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isLoading){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator(color = Color(0xFFFF9900))
                    }
                } else if (productos.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            "No hay productos disponibles",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }else {
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ){
                        items(productos){ producto ->
                            ProductoItem(
                                producto = producto,
                                onEliminar = {
                                    CoroutineScope(Dispatchers.IO).launch{
                                        productoRepository.eliminarProducto(producto.id!!)
                                        recargarProductos()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = producto.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = producto.descripcion,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )
                Text(
                    text = "$${"%.2f".format(producto.precio)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9900),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            IconButton(
                onClick = onEliminar,
                modifier = Modifier.padding(start = 8.dp)
            ){
                Icon(
                    Icons.Filled.Delete,
                    "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}