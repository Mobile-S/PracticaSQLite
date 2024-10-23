package com.example.clase06_sqlite

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.database.Cursor
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.Button
import android.content.Intent
import com.example.clase06_sqlite.Contacto
import android.app.AlertDialog

class SimpleListActivity : AppCompatActivity() {
    var db = DBAdapter(this)
    private lateinit var btnModificar: Button
    private lateinit var lstSimp: ListView
    private lateinit var listaContactos: MutableList<Contacto> // Lista para almacenar los contactos
    private lateinit var btnEliminar: Button
    private var contactoIdSeleccionado: Long = -1 // Change to Long


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simple_list)

        // Inicializar la lista de contactos
        listaContactos = mutableListOf()

        // Crear el objeto ListView
        lstSimp = findViewById(R.id.lstSimp) as ListView

        // Inicializar el botón 'Modificar'
        btnModificar = findViewById(R.id.btnModificar)

        // Cargar nombres y llenar la lista de contactos
        val nombres = cargaNombres()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, android.R.id.text1, nombres
        )

        // Asignar el adaptador al ListView
        lstSimp.adapter = adapter

        // Definir la acción de clic sobre los ítems de la lista
        lstSimp.onItemClickListener = OnItemClickListener { parent, view, position, _ ->
            // Obtener el ID del contacto seleccionado
            contactoIdSeleccionado = obtenerIdContactoSeleccionado(position).toLong()

            // Mostrar un Toast con el nombre del contacto seleccionado
            val itemValue = lstSimp.getItemAtPosition(position) as String
            Toast.makeText(
                applicationContext,
                "Position: $position, Nombre: $itemValue", Toast.LENGTH_LONG
            ).show()

            // Asignar la funcionalidad del botón 'Modificar'
            btnModificar.setOnClickListener {
                val intent = Intent(this, ModificarContactoActivity::class.java)
                intent.putExtra("contact_id", contactoIdSeleccionado)
                startActivity(intent)
            }
        }

        // Asignación del botón
        btnModificar = findViewById(R.id.btnModificar)

        // Conectar el botón 'Eliminar' con su ID en el layout
        btnEliminar = findViewById(R.id.btnEliminar)

        // Asignar la funcionalidad al botón
        btnEliminar.setOnClickListener {
            if (contactoIdSeleccionado != -1L) {
                // Mostrar la pantalla emergente de confirmación
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Eliminar Contacto")
                dialog.setMessage("¿De verdad deseas eliminar este contacto?")
                dialog.setPositiveButton("Sí") { _, _ ->
                    db.open()
                    val resultado = db.delContact(contactoIdSeleccionado.toLong())
                    db.close()

                    if (resultado) {
                        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad y regresa a la anterior
                    } else {
                        Toast.makeText(this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.setNegativeButton("No", null)
                dialog.show()
            } else {
                Toast.makeText(this, "Seleccione un contacto para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Obtener el ID del contacto seleccionado según la posición en la lista
    private fun obtenerIdContactoSeleccionado(position: Int): Int {
        return listaContactos[position].id // Obtener el ID del contacto en la posición seleccionada
    }

    // Cargar nombres y llenar la lista de contactos
    private fun cargaNombres(): Array<String?> {
        // Abrir la base de datos
        db.open()

        // Obtener todos los contactos
        val c: Cursor = db.getAllcontacts

        // Crear array para los nombres
        val nombres = arrayOfNulls<String>(c.count)

        // Cargar datos en la lista de contactos y nombres
        var i = 0
        if (c.moveToFirst()) {
            do {
                val contacto = Contacto(
                    c.getInt(0), // ID
                    c.getString(1), // Nombre
                    c.getString(2), // Apellidos
                    c.getString(3), // Email
                    c.getLong(4) // Celular
                )
                listaContactos.add(contacto) // Añadir a la lista de contactos
                nombres[i++] = c.getString(1) // Añadir el nombre al array
            } while (c.moveToNext())
        }

        // Cerrar la base de datos
        db.close()

        // Retornar la lista de nombres
        return nombres
    }
}
