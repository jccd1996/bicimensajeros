package com.example.bicimensajeros.Actividades


import android.support.v7.app.AppCompatActivity

import android.os.Build
import android.os.Bundle

import android.text.TextUtils
import android.view.View

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log

import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.example.bicimensajeros.Controladores.Actividades.InicioActivity
import com.example.bicimensajeros.Controladores.Actividades.RegistroMensajeroActivity
import com.example.bicimensajeros.R
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var usuario:String=""
    var tipoUsuario:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.

        val ss:Int = intent.getIntExtra("tipo",0)
        tipoUsuario=ss
        Log.d("LoginActivity",tipoUsuario.toString())

        if (tipoUsuario==1){
            cambiarFoto()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cambiarColorBarraEstado()
        }
        auth=FirebaseAuth.getInstance()
        login_progress.visibility=View.GONE


    }
    fun olvidarContrasena(view:View){
        crearDialogoCorreo()
    }

    fun irRegistro(view:View){
        if (tipoUsuario==0){
            startActivity(Intent(this, RegistroActivity::class.java))
        }else{
            startActivity(Intent(this, RegistroMensajeroActivity::class.java))
        }

    }
    fun login(view:View){
        loginUsuario()
    }
    private fun loginUsuario(){
        usuario=etUsuario.text.toString()
        val contrasena=etContrasena.text.toString()

        if(!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(contrasena)){
                login_progress.visibility=View.VISIBLE

                auth.signInWithEmailAndPassword(usuario,contrasena)
                    .addOnCompleteListener(this){
                        task->
                        if(task.isSuccessful){
                            accion()
                        }else{
                            Toast.makeText(this, R.string.error_autenticacion, Toast.LENGTH_SHORT).show()
                            login_progress.visibility=View.GONE

                        }
                    }
            }
    }

    private fun accion(){
        val intent=Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    private fun crearDialogoCorreo(){
        val mDialogView= LayoutInflater.from(this).inflate(R.layout.recuperar_contrasena,null)
        val dialog= AlertDialog.Builder(this,
            R.style.Base_ThemeOverlay_MaterialComponents_Dialog_Alert
        )
            .setView(mDialogView)

            .setPositiveButton(R.string.enviar_recuperar_contrasena, DialogInterface.OnClickListener { dialogInterface, i ->  })

            .setNegativeButton(R.string.cancelar){ _, _->
            }
            .setTitle(R.string.recordar_contraseña)
            .create()

        var mDialogAlert=dialog.show()
        mDialogAlert

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            //validarCorreos(etContrasenaNueva?.text.toString(),etRepetirContrasena?.text.toString())
            var etCorreoElectronico=mDialogView.findViewById<EditText>(R.id.etCorreoElectronico)?.text.toString()
            login_progress.visibility=View.VISIBLE
            if (!TextUtils.isEmpty(etCorreoElectronico)){
                auth.sendPasswordResetEmail(etCorreoElectronico)
                    .addOnCompleteListener(this){
                            task ->
                        if (task.isSuccessful){
                            login_progress.visibility=View.VISIBLE
                            startActivity(Intent(this, LoginActivity::class.java))
                            dialog.dismiss()
                            finish()

                        }else{
                            Toast.makeText(this, R.string.correo_error, Toast.LENGTH_SHORT).show()
                            login_progress.visibility=View.GONE
                        }
                    }
            }

        }
    }

    fun cambiarColorBarraEstado(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor=this.resources.getColor(R.color.colorPrimaryDark,null)
        }
    }
    private fun cambiarFoto(){
        imageView.setImageResource(R.drawable.ic_bicimensajero)
    }

}
