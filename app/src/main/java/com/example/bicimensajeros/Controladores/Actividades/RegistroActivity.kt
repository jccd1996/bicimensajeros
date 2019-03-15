package com.example.bicimensajeros.Actividades

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.example.bicimensajeros.Utilidades.Imagenes
import com.example.bicimensajeros.Utilidades.Validaciones
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_registro.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class RegistroActivity : AppCompatActivity() {


    //permisos
    private val SOLICITUD_SELECCIONAR_FOTO=2
    val permisoReadStorage=android.Manifest.permission.READ_EXTERNAL_STORAGE
    val permisoWriteStorage=android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    var fotoPerfil: Bitmap?=null
    var fotoPerfilPrueba:Bitmap?=null

    var nuevaFoto:Boolean?=false
    var fotoSeleccionada:Uri?=null
    var linkFotoPerfil:String?=null

    var nombres:String=""
    var apellidos:String=""
    var correo:String=""
    var celular:String=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)




    }




    fun registrar(view: View){
        var connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo=connectivityManager.activeNetworkInfo

        if(networkInfo!=null && networkInfo.isConnected){
            crearUsuario()
        }else{
            Toast.makeText(this,R.string.error_internet,Toast.LENGTH_SHORT).show()
        }
    }
    fun escogerFoto(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            seleccionarFoto()
        }else{
            Toast.makeText(this,"Este celular es malo",Toast.LENGTH_SHORT).show()
        }
    }


    private fun crearUsuario(){
        nombres=etNombre.text.toString()
        apellidos=etApellidos.text.toString()
        correo=etCorreo.text.toString()
        celular=etCelular.text.toString()

        val contrasena=etContrasena.text.toString()
        val contrasena2=etRepetirContrasena.text.toString()

        if (!Validaciones.tamañoMin(nombres,1)){
            Toast.makeText(this,R.string.nombres_incorrectos,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.tamañoMin(apellidos,1)){
            Toast.makeText(this,R.string.apellidos_incorrectos,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.correoElectronico(correo)){
            Toast.makeText(this,R.string.correo_incorrecto,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.tamañoMin(celular,10)){
            Toast.makeText(this,R.string.celular_incorrecto,Toast.LENGTH_SHORT).show()
            return
        }
        if (!Validaciones.tamañoMin(contrasena, 6)) {
            Toast.makeText(this, R.string.contrasena_incorrecta, Toast.LENGTH_SHORT).show()
            return
        } else if (!Validaciones.contrasena(contrasena, contrasena2)) {
            Toast.makeText(this, R.string.coincidir_contrasena, Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo,contrasena)
            .addOnCompleteListener(this){
                task->
                if(task.isComplete){
                    progressBar.visibility=View.VISIBLE
                    Log.d("Main","usuario creado con: ${task.result?.user?.uid}")

                    subirImagenFirebaseStorage()
                }else{
                    Toast.makeText(this, R.string.correo_existente, Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(this,"Fallo",Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenFirebaseStorage(){
        if (fotoSeleccionada == null){
            val sinFoto:String="NoPhoto"
            guardarUsuarioFirebaseBaseDatos(sinFoto)
            return
        }
        val uid=FirebaseAuth.getInstance().uid ?:""
        //Crea un ID aleatorio y se pone como nombre a la imagen
        val filename=UUID.randomUUID().toString()
        //Referencia al Storage de Firebase
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid/$filename")


        ref.putFile(fotoSeleccionada!!)
            .addOnSuccessListener {
                Log.d("RegistroActivity","Imagen subida con exito: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegistroActivity","Ubicacion del archivo: $it")
                    linkFotoPerfil=it.toString()
                    guardarUsuarioFirebaseBaseDatos(it.toString())
                }
            }
            .addOnFailureListener {
                //Poner algo aqui
            }
    }

    private fun guardarUsuarioFirebaseBaseDatos(urlImagenPerfil:String){
        //Con ?:"" verifica si el ID es null (operador Elvis) si el string es vacio
        val uid=FirebaseAuth.getInstance().uid ?:""

        val ref = FirebaseDatabase.getInstance().getReference("/usuarios/$uid")

        var usuario= Usuario(uid,nombres,apellidos,correo,celular,urlImagenPerfil)
        ref.setValue(usuario)
            .addOnSuccessListener {
                val enviarUsuarioCorreo:FirebaseUser?=FirebaseAuth.getInstance().currentUser
                verificarCorreo(enviarUsuarioCorreo)
                finalizar()
            }
    }

    //Cuando todo esta correcto, esta funcion entra.
    private fun finalizar(){
        Toast.makeText(this,R.string.usuario_registrado,Toast.LENGTH_SHORT).show()
        val intent=Intent(this,LoginActivity::class.java)
        //Inicia actividad main y no devuelve a la actividad registros
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        finish()

    }

    private fun verificarCorreo(usuario:FirebaseUser?){
        usuario?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                task->
                if (task.isComplete){
                    Toast.makeText(this, R.string.correo_enviado,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, R.string.correo_error,Toast.LENGTH_SHORT).show()
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

                    fotoSeleccionada=getImageUri(this,fotoPerfil!!)


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
        fotoPerfil= BitmapFactory.decodeStream(stream)
        //redimensionarImagen
        fotoPerfil= Imagenes().redimensionarImagen(fotoPerfil!!,800f,1000f)
        ivFotoCircular!!.setImageBitmap(fotoPerfil)

    }
    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes)
        val path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

}
