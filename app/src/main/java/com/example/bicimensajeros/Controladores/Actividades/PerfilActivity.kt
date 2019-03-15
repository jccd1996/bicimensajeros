package com.example.bicimensajeros.Controladores.Actividades

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bicimensajeros.Actividades.LoginActivity
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_perfil.*
import java.io.ByteArrayOutputStream
import java.io.File

class PerfilActivity : AppCompatActivity()/*,EasyPermissions.PermissionCallbacks*/{

    var usuario: Usuario? = null
    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        initToolbar(resources.getString(R.string.perfil))
        //obtenerUsuario()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_perfil, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.iEditarPerfil -> {
                val intent=Intent(this,EditarPerfilActivity::class.java)
                startActivity(intent)
                //Toast.makeText(this,"Proximamente...",Toast.LENGTH_SHORT).show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)

        }
    }

    //Desde aca
    private fun obtenerUsuario() {
        uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("Perfil", it.toString())
                    val nombres = p0.child("nombres").getValue(String::class.java)
                    val apellidos = p0.child("apellidos").getValue(String::class.java)
                    val celular = p0.child("celular").getValue(String::class.java)
                    val correo = p0.child("correo").getValue(String::class.java)
                    val uid = p0.child("uid").getValue(String::class.java)
                    val fotoPerfil = p0.child("fotoPerfil").getValue(String::class.java)
                    val direccion = p0.child("direccion").getValue(String::class.java)
                    val numeroPedidos = p0.child("numeroPedidos").getValue(String::class.java)
                    val departamento = p0.child("departamento").getValue(String::class.java)
                    val ciudad = p0.child("ciudad").getValue(String::class.java)
                    val modoPago = p0.child("modoPago").getValue(String::class.java)

                    usuario = Usuario(uid!!, nombres!!, apellidos!!, correo!!, celular!!, fotoPerfil!!,direccion!!,numeroPedidos!!,departamento!!,ciudad!!,modoPago!!)

                    cargarUsuario()
                }
            }

        })

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

    private fun cargarUsuario() {
        tvNombres.text = usuario?.nombres
        tvApellidos.text = usuario?.apellidos
        tvCorreo.text = usuario?.correo
        tvCelular.text = usuario?.celular
        tvDireccion.text=usuario?.direccion
        Picasso.get()
            .load(usuario?.fotoPerfil)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(ivFoto)
    }

    override fun onResume() {
        super.onResume()
        obtenerUsuario()
    }
}
