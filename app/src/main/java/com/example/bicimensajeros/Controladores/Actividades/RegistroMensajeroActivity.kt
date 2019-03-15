package com.example.bicimensajeros.Controladores.Actividades

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
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.bicimensajeros.Actividades.LoginActivity
import com.example.bicimensajeros.Controladores.Modelos.Bicimensajero
import com.example.bicimensajeros.Controladores.Modelos.Usuario
import com.example.bicimensajeros.R
import com.example.bicimensajeros.Utilidades.Imagenes
import com.example.bicimensajeros.Utilidades.Validaciones
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registro_mensajero.*
import java.io.ByteArrayOutputStream
import java.util.*

class RegistroMensajeroActivity : AppCompatActivity() {

    //permisos
    private val SOLICITUD_SELECCIONAR_FOTO=2
    val permisoReadStorage=android.Manifest.permission.READ_EXTERNAL_STORAGE
    val permisoWriteStorage=android.Manifest.permission.WRITE_EXTERNAL_STORAGE

    var nombres:String=""
    var apellidos:String=""
    var correo:String=""
    var celular:String=""
    var cualFoto=0
    var fotoSeleccionada: Uri?=null
    var fotoSeleccionadaPortada:Uri?=null
    var linkFotoPerfil:String?=null
    var linkFotoPortada:String?=null
    var urlImagenPortada:String?=""
    var nuevaFoto:Boolean?=false
    var fotoPerfil: Bitmap?=null
    var fotoPortada: Bitmap?=null
    var fotoComprimida:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mensajero)
    }

    fun registrar(view: View){
        var connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo=connectivityManager.activeNetworkInfo

        if(networkInfo!=null && networkInfo.isConnected){
            crearMensajero()
        }else{
            Toast.makeText(this,R.string.error_internet, Toast.LENGTH_SHORT).show()
        }
    }
    fun escogerFotoPerfil(view:View){
        cualFoto=0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            seleccionarFoto()
        }

    }
    fun escogerFotoPortada(view:View){
        cualFoto=1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            seleccionarFoto()
        }
    }


    private fun crearMensajero(){
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
        if (fotoSeleccionada !=null && fotoSeleccionadaPortada != null){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo,contrasena)
                .addOnCompleteListener(this){
                        task->
                    if(task.isComplete){
                        progressBar.visibility=View.VISIBLE
                        Log.d("Main","usuario creado con: ${task.result?.user?.uid}")
                        //Falta hacer la validacion de las fotos aca, si no crea la autenticacion pero no le usuario ni las fotos

                        subirImagenFirebaseStorage()

                    }else{
                        Toast.makeText(this, R.string.correo_existente, Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this,"Fallo",Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, R.string.foto_por_seguridad, Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirImagenFirebaseStorage(){

        val uid=FirebaseAuth.getInstance().uid ?:""

        if (fotoSeleccionada!=null){
            val filename= UUID.randomUUID().toString()
            //Referencia al Storage de Firebase
            val ref = FirebaseStorage.getInstance().getReference("/images/mensajeros/$uid/$filename")
            ref.putFile(fotoSeleccionada!!)
                .addOnSuccessListener {
                    Log.d("RegistroActivity","Imagen subida con exito: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        Log.d("RegistroActivity","Ubicacion del archivo: $it")
                        linkFotoPerfil=it.toString()
                        if (fotoSeleccionadaPortada!=null){
                            val filename= UUID.randomUUID().toString()
                            //Referencia al Storage de Firebase
                            val ref = FirebaseStorage.getInstance().getReference("/images/mensajeros/$uid/$filename")
                            ref.putFile(fotoSeleccionadaPortada!!)
                                .addOnSuccessListener {
                                    Log.d("RegistroActivity","Imagen subida con exito: ${it.metadata?.path}")

                                    ref.downloadUrl.addOnSuccessListener {
                                        it.toString()
                                        Log.d("RegistroActivity","Ubicacion del archivo: $it")
                                        linkFotoPortada=it.toString()
                                        guardarUsuarioFirebaseBaseDatos(linkFotoPerfil!!,linkFotoPortada!!)
                                    }
                                }
                                .addOnFailureListener {
                                    //Poner algo aqui
                                }
                        }else{
                            progressBar.visibility=View.GONE
                            Toast.makeText(this,R.string.foto_por_seguridad_portada,Toast.LENGTH_SHORT).show()
                            linkFotoPortada="NoPhoto"
                        }
                    }
                }
                .addOnFailureListener {
                    //Poner algo aqui
                }
        }else{
            linkFotoPerfil="NoPhoto"
            progressBar.visibility=View.GONE
            Toast.makeText(this,R.string.foto_por_seguridad_perfil,Toast.LENGTH_SHORT).show()
        }


      //  guardarUsuarioFirebaseBaseDatos(linkFotoPerfil!!,linkFotoPortada!!)

    }
    private fun guardarUsuarioFirebaseBaseDatos(urlImagenPerfil:String,urlImagenPortada:String) {
        //Con ?:"" verifica si el ID es null (operador Elvis) si el string es vacio
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/mensajeros/$uid")

        var mensajero = Bicimensajero(uid,nombres,apellidos,correo,celular,urlImagenPerfil,urlImagenPortada,"0")
        ref.setValue(mensajero)
            .addOnSuccessListener {
                val enviarUsuarioCorreo: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                verificarCorreo(enviarUsuarioCorreo)
                finalizar()
            }
    }
    //Cuando todo esta correcto, esta funcion entra.
    private fun finalizar(){
        Toast.makeText(this,R.string.usuario_registrado,Toast.LENGTH_SHORT).show()
        val intent= Intent(this, LoginActivity::class.java)
        intent.putExtra("tipo",1)
        //Inicia actividad main y no devuelve a la actividad registros
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                    if (cualFoto==0){
                        fotoPerfil=mostrarBitmap(data.data.toString(),ivFotoCircular)
                        fotoSeleccionada=getImageUri(this,fotoPerfil!!)
                    }else{
                        fotoPortada=mostrarBitmap(data.data.toString(),ivPortada)
                        fotoSeleccionadaPortada=getImageUri(this,fotoPortada!!)
                    }
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

    private fun mostrarBitmap(url:String,fotoActivity: ImageView):Bitmap{
        val uri= Uri.parse(url)
        val stream=contentResolver.openInputStream(uri)
        fotoComprimida= BitmapFactory.decodeStream(stream)
        //redimensionarImagen
        fotoComprimida= Imagenes().redimensionarImagen(fotoComprimida!!,800f,1000f)
       /* if (cualFoto==0){
            fotoActivity!!.setImageBitmap(fotoPerfil)
        }else{
            fotoActivity!!.setImageBitmap(fotoPortada)
        }*/
        fotoActivity!!.setImageBitmap(fotoComprimida)
        return fotoComprimida!!
    }
    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes)
        val path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }

}
