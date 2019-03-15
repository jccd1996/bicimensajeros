package com.example.bicimensajeros.Utilidades

import android.graphics.Bitmap
import android.graphics.Matrix

class Imagenes {
    fun redimensionarImagen(bitmap: Bitmap, anchoNuevo:Float, altoNuevo:Float): Bitmap {
        var ancho=bitmap.width
        var alto=bitmap.height
        var relacion=ancho/alto.toFloat()


        if (ancho>anchoNuevo || alto>altoNuevo){
            var escala:Float
            if(relacion>1){
                escala=(anchoNuevo/ancho)
            }else{
                escala=(altoNuevo/alto)
            }

            var matrix: Matrix?= Matrix()
            matrix?.postScale(escala,escala)
            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false)
        }else{
            return bitmap
        }

    }
}