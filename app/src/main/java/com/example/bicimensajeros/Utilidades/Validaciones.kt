package com.example.bicimensajeros.Utilidades

import java.util.regex.Pattern

class Validaciones {
    companion object {

        fun correoElectronico(correoElectronico: String): Boolean{
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(correoElectronico).matches()

        }
        fun tamañoMin(texto:String,min:Int): Boolean{
            if (texto.length<min){
                return false
            }
            return true
        }
        fun contrasena(texto1:String,texto2:String):Boolean{

            if (texto1!=texto2){
                return false
            }
            return true
        }
    }
}