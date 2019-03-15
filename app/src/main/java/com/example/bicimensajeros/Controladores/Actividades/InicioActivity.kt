package com.example.bicimensajeros.Controladores.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.bicimensajeros.Actividades.LoginActivity
import com.example.bicimensajeros.R
import kotlinx.android.synthetic.main.activity_inicio.*




class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
    }
    fun irLoginUsuario(view: View){
        val intent=Intent(this,LoginActivity::class.java)
        intent.putExtra("tipo",0)
        startActivity(intent)
        finish()
    }
    fun irLoginBicimensajero(view:View){

        val intent=Intent(this,LoginActivity::class.java)
        intent.putExtra("tipo",1)
        startActivity(intent)
        finish()
    }

}
