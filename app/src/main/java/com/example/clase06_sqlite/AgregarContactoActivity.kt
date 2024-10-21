package com.example.clase06_sqlite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AgregarContactoActivity : AppCompatActivity() {
    private lateinit var db: DBAdapter
    private lateinit var btnGuardarContacto: Button
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCelular: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_contacto)

        // Inicializar los componentes
        db = DBAdapter(this)
        btnGuardarContacto = findViewById(R.id.btnGuardarContacto)
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etEmail = findViewById(R.id.etEmail)
        etCelular = findViewById(R.id.etCelular)

        // Agregar acción al botón
        btnGuardarContacto.setOnClickListener {
            agregarContacto()
        }
    }

    // Función después de `onCreate()` para agregar contacto
    private fun agregarContacto() {
        // Obtener los valores de los EditText
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val email = etEmail.text.toString()
        val celular = etCelular.text.toString().toLongOrNull()

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || celular == null) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Insertar contacto en la base de datos
        db.open()
        db.insContact(nombre, apellidos, email, celular)
        db.close()

        // Mostrar mensaje y cerrar la actividad
        Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
        finish()
    }
}
