package me.davidguerrero.unabstore

import android.R
import android.util.Patterns

//return true si es velido y un false si no es valido
//también retorne una cadena de texto que me diga que pasó si no es valido
fun validateEmail (email: String): Pair<Boolean, String>{
    return when{
        email.isEmpty() -> Pair(false, "El correo es requerido")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Pair(false, "El correo es invalido")
        !email.endsWith("@test.com") -> Pair(false, "Ese email no pertenece a la corporación")
        else -> Pair(true,"")
    }

}

fun validatePassword (password: String): Pair<Boolean,String>{
    return when{
        password.isEmpty() -> Pair(false,"La contraseña es requerida")
        password.length < 8 -> Pair(false,"La contraseña debe tener al menos 8 caracteres")
        !password.any(){it.isDigit()} -> Pair(false,"la contraseña debe tener al menos un número")
        else -> Pair(true,"")
    }
}