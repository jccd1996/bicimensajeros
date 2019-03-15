package com.example.bicimensajeros.Controladores.Actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.example.bicimensajeros.Controladores.Adaptadores.AdaptadorMensajeros
import com.example.bicimensajeros.Controladores.Modelos.Bicimensajero
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_mensajeros.*
import kotlinx.android.synthetic.main.template_lista_mensajeros.view.*

class MensajerosActivity : AppCompatActivity() {
    companion object {
        val USER_KEY="USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajeros)

        initToolbar("Mensajeros")

    /*    val adapter = GroupAdapter <ViewHolder> ()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        rvMensajeros.hasFixedSize()
        rvMensajeros.adapter=adapter*/

        traerMensajeros()
    }

    private fun traerMensajeros(){
        val ref= FirebaseDatabase.getInstance().getReference("/mensajeros")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter=GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("Mensajero",it.toString())
                    val mensajero=it.getValue(Bicimensajero::class.java)
                    if (mensajero!=null){
                        if (mensajero.activo == "1"){
                            adapter.add(AdaptadorMensajeros(mensajero!!))
                        }
                    }
                    adapter.setOnItemClickListener { item, view ->
                        val mensajeroItem= item as AdaptadorMensajeros
                        val intent= Intent(view.context,PerfilMensajeroActivity::class.java)
                        Toast.makeText(applicationContext,"Seleccionaste a ${mensajeroItem.mensajero.nombres}",Toast.LENGTH_SHORT).show()
                        intent.putExtra(USER_KEY,mensajeroItem.mensajero)
                        startActivity(intent)
                    }

                }
                rvMensajeros.adapter=adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}
