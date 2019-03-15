package com.example.bicimensajeros.Controladores.Modelos

import android.provider.MediaStore
import android.graphics.Bitmap



open class Usuario() {
   // constructor():this("","","","","","")

    var uid:String=""
    var nombres:String=""
    var apellidos:String=""
    var correo:String=""
    var celular:String=""
    var fotoPerfil:String=""
    var direccion:String=""
    var numeroPedidos:String=""
    var departamento:String=""
    var ciudad:String=""
    var modoPago:String=""

    constructor(uid:String,nombres:String,apellidos:String,correo:String,celular:String,fotoPerfil:String):this(){
        this.uid=uid
        this.nombres=nombres
        this.apellidos=apellidos
        this.correo=correo
        this.celular=celular
        this.fotoPerfil=fotoPerfil

    }

    constructor(uid:String,nombres:String,apellidos:String,correo:String,celular:String,fotoPerfil:String,direccion:String,numeroPedidos:String,departamento:String,ciudad:String,modoPago:String):this(){
        this.uid=uid
        this.nombres=nombres
        this.apellidos=apellidos
        this.correo=correo
        this.celular=celular
        this.fotoPerfil=fotoPerfil
        this.direccion=direccion
        this.numeroPedidos=numeroPedidos
        this.departamento=departamento
        this.ciudad=ciudad
        this.modoPago=modoPago


    }

}