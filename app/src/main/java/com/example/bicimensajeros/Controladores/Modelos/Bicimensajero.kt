package com.example.bicimensajeros.Controladores.Modelos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Bicimensajero(var uid:String="",var nombres:String="",var apellidos:String="",var correo:String="",var celular:String="",
                    var fotoPerfil:String="",var activo:String="",var disponible:String="",var edad:String="",var tiempoTrabajando:String="",
                    var rating:String="",var foto2:String="",var tipoVehiculo:String=""):Parcelable {
 /*   var uid:String=""
    var nombres:String=""
    var apellidos:String=""
    var correo:String=""
    var celular:String=""
    var fotoPerfil:String=""
    var activo:String=""
    var disponible:String=""
    var edad:String=""
    var tiempoTrabajando:String=""
    var rating:String=""
    var foto2:String=""
    var tipoVehiculo:String=""

    constructor(uid:String,nombres:String,apellidos:String,correo:String,celular:String,fotoPerfil:String,foto2:String,activo:String):this(){
        this.uid=uid
        this.nombres=nombres
        this.apellidos=apellidos
        this.correo=correo
        this.celular=celular
        this.fotoPerfil=fotoPerfil
        this.foto2=foto2
        this.activo=activo

    }
    constructor(uid: String,activo:String,disponible:String,nombres:String,tipoVehiculo:String,fotoPerfil:String):this(){
        this.uid=uid
        this.activo=activo
        this.disponible=disponible
        this.nombres=nombres
        this.tipoVehiculo=tipoVehiculo
        this.fotoPerfil=fotoPerfil
    }*/

}