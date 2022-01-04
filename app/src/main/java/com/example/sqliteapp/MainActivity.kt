package com.example.sqliteapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sqliteapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var amigosDBHelper = MySQLHelper(this)

    private var list: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      LinearLayout para los contactos
        list = binding.contactsList

//      Boton guardar
        binding.btnGuardar.setOnClickListener {
            var nombre = binding.editTextNombre.text.toString()
            var email = binding.editTextEmail.text.toString()

            amigosDBHelper.anyadirDato(nombre, email)

        }

//      Opcion de modificar
        binding.switchModificar.setOnCheckedChangeListener { _, isChecked ->

            // Desmarca la opcion eliminar si esta activa y deshabilita el boton guardar
            if ( isChecked ){
                binding.btnGuardar.isEnabled = false
                binding.switchEliminar.isChecked = false
            }
            else if( !binding.switchEliminar.isChecked ){
                binding.btnGuardar.isEnabled = true
            }
        }

//      Opcion de eliminar
        binding.switchEliminar.setOnCheckedChangeListener { _, isChecked ->

            // Desmarca la opcion modificar si esta activa y deshabilita el boton guardar
            if ( isChecked ){
                binding.btnGuardar.isEnabled = false
                binding.switchModificar.isChecked = false
            }
            else if( !binding.switchModificar.isChecked ){
                binding.btnGuardar.isEnabled = true
            }
        }

//      Boton consultar
        binding.btnConsultar.setOnClickListener{

            getContacts()
        }
    }

    private fun getContacts(){

        list!!.removeAllViews()

        val cursor = amigosDBHelper.mostrarDatos()
        if (cursor!!.moveToFirst()) {

            do {

                // Crea un textview por cada contacto
                var textView = TextView(this)
                textView.setPadding(0, 20, 0, 20)
                textView.text = cursor.getInt(0).toString() + ": " +
                        cursor.getString(1).toString() + ", " +
                        cursor.getString(2).toString()

                // Evento click sobre los contactos
                textView.isClickable = true
                textView.setOnClickListener{

                    var id = textView.text.split(":")[0]

                    // Si queremos modificar
                    if ( binding.switchModificar.isChecked ) {

                        var nombre = binding.editTextNombre.text.toString()
                        var email = binding.editTextEmail.text.toString()

                        // Marca en rojo los campos si falta algun valor
                        if( nombre == "" || email == ""){
                            binding.editTextNombre.setHintTextColor( Color.RED )
                            binding.editTextEmail.setHintTextColor( Color.RED )
                        }
                        else{
                            binding.editTextNombre.setHintTextColor( 1627389952 )
                            binding.editTextEmail.setHintTextColor( 1627389952 )

                            amigosDBHelper.actualizarDatos(id.toInt(), nombre, email)
                            binding.editTextNombre.setText("")
                            binding.editTextEmail.setText("")
                            binding.switchModificar.isChecked = false
                            getContacts()
                        }
                    }

                    // Si queremos eliminar
                    else if( binding.switchEliminar.isChecked ) {
                        amigosDBHelper.eliminarDato(id.toInt())
                        getContacts()
                    }
                }

                // Añade el TextView al LinearLayout
                list!!.addView( textView )

            } while (cursor.moveToNext())
        }
    }



}