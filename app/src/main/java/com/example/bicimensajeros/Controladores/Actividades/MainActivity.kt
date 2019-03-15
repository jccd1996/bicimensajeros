package com.example.bicimensajeros.Actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.bicimensajeros.Controladores.Actividades.InicioActivity
import com.example.bicimensajeros.Controladores.Actividades.MensajerosActivity
import com.example.bicimensajeros.Controladores.Actividades.PerfilActivity
import com.example.bicimensajeros.Controladores.Modelos.Bicimensajero
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,GoogleMap.OnMarkerClickListener  {



    private lateinit var mMap:GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    var usuario:Usuario?=null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mMapa) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setSupportActionBar(toolbar)
        verificarUsuarioLogin()

       // obtenerUsuario()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Proximamente...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.setOnMarkerClickListener(this)
        setUpMap()



    }

    override fun onMarkerClick(p0: Marker?) = false


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.iPerfil -> {
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.iCerrarSesion -> {
                FirebaseAuth.getInstance().signOut()
                //val intent = Intent(this, LoginActivity::class.java)
                val intent = Intent(this, InicioActivity::class.java)
                //Inicia actividad main y no devuelve a la actividad registros
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_bicimensajeros -> {
                // Handle the camera action
                startActivity(Intent(this,MensajerosActivity::class.java))
            }
            R.id.nav_historial -> {

            }

            R.id.nav_configuracion -> {

            }
            R.id.nav_compartir -> {

            }
            R.id.nav_sugerencias -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun verificarUsuarioLogin(){
        val uid=FirebaseAuth.getInstance().uid
        if(uid==null){
           // val intent= Intent(this,LoginActivity::class.java)
            val intent= Intent(this, InicioActivity::class.java)
            //Inicia actividad main y no devuelve a la actividad registros
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }else{
            obtenerUsuario()
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources,R.mipmap.ic_user_location)))
        // 2
        mMap.addMarker(markerOptions)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }
    private fun obtenerUsuario() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
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
    private fun cargarUsuario() {
        tvNombres.text = usuario?.nombres + " " + usuario?.apellidos
        tvCorreo.text = usuario?.correo
        Picasso.get()
            .load(usuario?.fotoPerfil)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(ivFoto)
    }
}
