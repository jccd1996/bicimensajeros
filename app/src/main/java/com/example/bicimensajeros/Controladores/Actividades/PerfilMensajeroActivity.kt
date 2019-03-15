package com.example.bicimensajeros.Controladores.Actividades

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import com.example.bicimensajeros.Controladores.Modelos.Bicimensajero
import com.example.bicimensajeros.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_perfil_mensajero.*


class PerfilMensajeroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_mensajero)

        val mensajero=intent.getParcelableExtra<Bicimensajero>(MensajerosActivity.USER_KEY) as Bicimensajero

        Log.d("PerfilMensajero",mensajero.toString())
        initToolbar(mensajero.nombres)
        //asignarObjetos(mensajero)

    }
    fun initToolbar(titulo: String) {
        var toolbar: Toolbar? = null
        toolbar = findViewById(R.id.toolbar) as Toolbar?

        toolbar?.title = titulo
        toolbar?.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener {
            finish()
        }

    }
    fun asignarObjetos(usuario:Bicimensajero){
        tvNombre.text = usuario.nombres + " " + usuario.apellidos
        tvCorreo.text= usuario.correo
        tvCelular.text= usuario.celular
        tvEdad.text= usuario.edad
        if (usuario.tipoVehiculo=="Bicicleta"){
            tvVehiculo.text= "Bicicleta"
            ivVehiculo.setImageResource(R.drawable.ic_bicimensajero_navmenu)
        }else{
            tvVehiculo.text= "Moto"
            ivVehiculo.setImageResource(R.drawable.ic_motocicleta_negra)
        }
        Picasso.get()
            .load(usuario.fotoPerfil)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(ivFotoCircular)
        Picasso.get()
            .load(usuario.foto2)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(ivPortada)
    }
}
