package com.example.bicimensajeros.Controladores.Actividades

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.text.TextUtils

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.bicimensajeros.Actividades.LoginActivity
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.example.bicimensajeros.Utilidades.Imagenes
import com.example.bicimensajeros.Utilidades.Validaciones
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.util.*


class EditarPerfilActivity : AppCompatActivity() {

    var usuario: Usuario? = null
    var uid: String? = null
    var ivFoto: ImageView?=null
    var nombreNuevo:String?=null
    val user = FirebaseAuth.getInstance().currentUser
    var nuevaFoto:Boolean?=false

    var nombres:String?=null
    var apellidos:String?=null
    var celular:String?=null
    var correo:String?=null
    var fotoPerfil:String?=null
    var direccion:String?=null
    var departamento:String?=null
    var ciudad:String?=null
    var numPedidos:String?=null

    var nuevaFotoPerfil: Bitmap?=null
    var fotoSeleccionada:Uri?=null
    var linkFotoPerfil:String?=null


    var usuarioActualizado:Usuario?=null
    private val SOLICITUD_SELECCIONAR_FOTO=2
    val permisoReadStorage=android.Manifest.permission.READ_EXTERNAL_STORAGE
    val permisoWriteStorage=android.Manifest.permission.WRITE_EXTERNAL_STORAGE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        ivFoto=findViewById(R.id.ivFoto)



        initToolbar(resources.getString(R.string.editar_perfil))
        obtenerUsuario()




    }

    fun escogerFoto(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            seleccionarFoto()
        }else{
            //Toast.makeText(this,"Este celular es malo",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editar_perfil, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.iAceptar -> {
                //actualizarUsuario()
                subirImagenFirebaseStorage()
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
                    nombres = p0.child("nombres").getValue(String::class.java)
                    apellidos = p0.child("apellidos").getValue(String::class.java)
                    celular = p0.child("celular").getValue(String::class.java)
                    correo = p0.child("correo").getValue(String::class.java)
                    uid = p0.child("uid").getValue(String::class.java)
                    fotoPerfil = p0.child("fotoPerfil").getValue(String::class.java)
                    direccion = p0.child("direccion").getValue(String::class.java)
                    departamento = p0.child("departamento").getValue(String::class.java)
                    ciudad = p0.child("ciudad").getValue(String::class.java)
                    numPedidos = p0.child("ciudad").getValue(String::class.java)


                    usuario = Usuario(uid!!, nombres!!, apellidos!!, correo!!, celular!!, fotoPerfil!!,direccion!!,numPedidos!!,departamento!!,ciudad!!,"Efectivo")

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
        toolbar?.setNavigationIcon(R.drawable.ic_cancelar)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.setNavigationOnClickListener {
            finish()
        }

    }

    private fun cargarUsuario() {
        etNombres.setText(usuario?.nombres)
        etApellidos.setText(usuario?.apellidos)
        tvCorreo.text=usuario?.correo
        etCelular.setText(usuario?.celular)
        etDireccion.setText(usuario?.direccion)
        tvCiudad.text=ciudad
        Picasso.get()
            .load(usuario?.fotoPerfil)
            .error(R.drawable.error_icon1)
            .placeholder(R.drawable.animacion_carga)
            .into(ivFoto)
    }

    fun actualizarUsuario(){
        nombres=etNombres.text.toString()
        apellidos=etApellidos.text.toString()
        correo=tvCorreo.text.toString()
        celular=etCelular.text.toString()
        direccion=etDireccion.text.toString()

        if (!Validaciones.tamañoMin(nombres!!,3)){
            Toast.makeText(this,R.string.nombres_incorrectos,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.tamañoMin(apellidos!!,3)){
            Toast.makeText(this,R.string.apellidos_incorrectos,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.tamañoMin(celular!!,10)){
            Toast.makeText(this,R.string.celular_incorrecto,Toast.LENGTH_SHORT).show()
            return
        }

        usuario=Usuario(uid!!,nombres!!,apellidos!!,correo!!,celular!!,fotoPerfil!!,direccion!!,"0","Tolima","Ibagué","Efectivo")
        val ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid")
        //nombreNuevo=etNombres.text.toString()

        ref.setValue(usuario)
            .addOnSuccessListener {
                //Toast.makeText(this,"EditarPerfilActivity",Toast.LENGTH_SHORT).show()
                Toast.makeText(this,R.string.perfil_actualizado,Toast.LENGTH_SHORT).show()
            }


    }


    fun cambiarCorreo(view:View){
        val mDialogView= LayoutInflater.from(this).inflate(R.layout.cambiar_correo,null)
        val dialog= AlertDialog.Builder(this,
            R.style.Base_ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setView(mDialogView)

            .setPositiveButton(R.string.actualizar, DialogInterface.OnClickListener { dialogInterface, i ->  })

            .setNegativeButton(R.string.cancelar){ _, _->
            }
            .setTitle(R.string.cambiar_correo)
            .create()

        var mDialogAlert=dialog.show()
        mDialogAlert

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            //validarCorreos(etContrasenaNueva?.text.toString(),etRepetirContrasena?.text.toString())
            var contrasenaAtuh=mDialogView.findViewById<EditText>(R.id.etContrasena)?.text.toString()
            var nuevoCorreo=mDialogView.findViewById<EditText>(R.id.etCorreoElectronico)?.text.toString()
            if (!Validaciones.correoElectronico(nuevoCorreo)){
                Toast.makeText(this,R.string.correo_incorrecto,Toast.LENGTH_SHORT).show()
            }
            if (!Validaciones.tamañoMin(contrasenaAtuh, 6)) {
                Toast.makeText(this, R.string.contrasena_incorrecta, Toast.LENGTH_SHORT).show()
            }
            //login_progress.visibility=View.VISIBLE
            if (!TextUtils.isEmpty(nuevoCorreo)){
                val credential = EmailAuthProvider
                    .getCredential(correo!!, contrasenaAtuh!!)

                user?.reauthenticate(credential)
                    ?.addOnCompleteListener {
                        Log.d("EditarPerfilActivity", "User re-authenticated.")
                        if (it.isSuccessful){
                            user?.updateEmail(nuevoCorreo!!)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("CorreoActualizado", "User email address updated.")
                                        correo = nuevoCorreo
                                        tvCorreo.text = correo
                                        val ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid/correo")
                                        ref.setValue(correo)
                                        Toast.makeText(this, R.string.correo_actualizado, Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    } else {
                                        Toast.makeText(this, R.string.correo_existente, Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                }
                        }



                    }
                    ?.addOnFailureListener {
                        Toast.makeText(this,R.string.error_contrasena,Toast.LENGTH_SHORT).show()
                        Log.d("CorreoActualizado", "Aca esta el error.")
                        dialog.dismiss()
                    }
            }
        }
    }
    fun subirImagenFirebaseStorage(){
        if (fotoSeleccionada == null){
            actualizarUsuario()
            return
        }else{
            //Crea un ID aleatorio y se pone como nombre a la imagen
            val filename= UUID.randomUUID().toString()
            //Referencia al Storage de Firebase
            val ref = FirebaseStorage.getInstance().getReference("/images/$uid/$filename")


            ref.putFile(fotoSeleccionada!!)
                .addOnSuccessListener {
                    Log.d("RegistroActivity","Imagen subida con exito: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        Log.d("RegistroActivity","Ubicacion del archivo: $it")
                        linkFotoPerfil=it.toString()
                        fotoPerfil=linkFotoPerfil
                        actualizarUsuario()
                    }
                }
                .addOnFailureListener {
                    //Poner algo aqui
                }
        }


    }

    //Parte de seleccionar foto
    @RequiresApi(Build.VERSION_CODES.M)
    fun solicitudPermisosSeleccionarFoto(){
        requestPermissions(arrayOf(permisoReadStorage,permisoWriteStorage),SOLICITUD_SELECCIONAR_FOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode){
            SOLICITUD_SELECCIONAR_FOTO->{
                if(grantResults.size>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //disparar intent foto
                    dispararIntentSeleccionarFoto()
                }else{
                    //no tenemos permiso para foto
                    Toast.makeText(this,R.string.no_permiso_galeria,Toast.LENGTH_SHORT).show()
                }

            }
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SOLICITUD_SELECCIONAR_FOTO->{
                if(resultCode== Activity.RESULT_OK && data!=null){
                    nuevaFoto=true
                    mostrarBitmap(data.data.toString())
                    fotoSeleccionada=getImageUri(this,nuevaFotoPerfil!!)


                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun seleccionarFoto(){

        pedirPermisosSeleccionarFoto()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun pedirPermisosSeleccionarFoto(){


        val deboProveerContexto= ActivityCompat.shouldShowRequestPermissionRationale(this,permisoReadStorage)
        val permisoStorage= ActivityCompat.shouldShowRequestPermissionRationale(this,permisoWriteStorage)



        if (deboProveerContexto){
            solicitudPermisosSeleccionarFoto()
        }else{
            solicitudPermisosSeleccionarFoto()
        }


    }
    fun dispararIntentSeleccionarFoto(){
        val intent= Intent(Intent.ACTION_GET_CONTENT)
        //val intent= Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,"Seleccionar una foto"),SOLICITUD_SELECCIONAR_FOTO)
    }

    private fun mostrarBitmap(url:String){
        val uri= Uri.parse(url)
        val stream=contentResolver.openInputStream(uri)
        nuevaFotoPerfil= BitmapFactory.decodeStream(stream)
        //redimensionarImagen
        nuevaFotoPerfil= Imagenes().redimensionarImagen(nuevaFotoPerfil!!,800f,1000f)
        ivFoto!!.setImageBitmap(nuevaFotoPerfil)

    }
    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes)
        val path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }
}
