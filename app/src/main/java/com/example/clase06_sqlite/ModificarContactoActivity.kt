package com.example.clase06_sqlite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class ModificarContactoActivity : AppCompatActivity() {
    private lateinit var db: DBAdapter
    private var contactoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_contacto)

        db = DBAdapter(this)

        contactoId = intent.getLongExtra("contact_id", -1)

        if (contactoId != -1L) {
            cargarDatosContacto(contactoId)
        }

        val btnGuardarCambios = findViewById<Button>(R.id.btnGuardarCambios)
        btnGuardarCambios.setOnClickListener {
            confirmarModificacion()
        }
    }

    private fun cargarDatosContacto(id: Long) {
        db.open()
        val cursor = db.getContact_by_Id(id.toInt())

        if (cursor != null && cursor.moveToFirst()) {
            findViewById<EditText>(R.id.etNombre).setText(cursor.getString(1))
            findViewById<EditText>(R.id.etApellidos).setText(cursor.getString(2))
            findViewById<EditText>(R.id.etEmail).setText(cursor.getString(3))
            findViewById<EditText>(R.id.etCelular).setText(cursor.getString(4))
        } else {
            Toast.makeText(this, "No se encontró el contacto", Toast.LENGTH_SHORT).show()
        }

        cursor?.close()
        db.close()
    }


    private fun confirmarModificacion() {
        // Mostrar un cuadro de diálogo de confirmación antes de modificar
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Modificar Contacto")
        builder.setMessage("¿Estás seguro de que deseas modificar este contacto?")

        builder.setPositiveButton("Sí") { _, _ ->
            guardarCambios()
        }

        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun guardarCambios() {
        val nombre = findViewById<EditText>(R.id.etNombre).text.toString()
        val apellidos = findViewById<EditText>(R.id.etApellidos).text.toString()
        val email = findViewById<EditText>(R.id.etEmail).text.toString()
        val celularStr = findViewById<EditText>(R.id.etCelular).text.toString()

        // Validar el campo de celular
        if (celularStr.isEmpty() || !celularStr.all { it.isDigit() }) {
            Toast.makeText(this, "Por favor, ingrese un número de celular válido", Toast.LENGTH_SHORT).show()
            return
        }

        val celular = celularStr.toLong()

        db.open()
        val resultado = db.updContact(contactoId, nombre, apellidos, email, celular)
        db.close()

        if (resultado) {
            Toast.makeText(this, "Contacto modificado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al modificar el contacto", Toast.LENGTH_SHORT).show()
        }
    }
}