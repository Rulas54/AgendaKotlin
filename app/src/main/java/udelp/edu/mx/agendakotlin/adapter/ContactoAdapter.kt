package udelp.edu.mx.agendakotlin.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.contacto_form
import udelp.edu.mx.agendakotlin.model.Contacto

class ContactoAdapter(
    contactos: List<Contacto>,
    private val onEliminarClick: (Contacto) -> Unit
) : RecyclerView.Adapter<ContactoAdapter.ContactoHolder>() {

    // Lista original completa (sin modificar)
    private val contactosOriginales = contactos.toMutableList()

    // Lista que se muestra y se filtra
    private val contactosFiltrados = contactos.toMutableList()

    class ContactoHolder(val view: View, val onEliminarClick: (Contacto) -> Unit) : RecyclerView.ViewHolder(view) {
        private val lblNombre2: TextView = view.findViewById(R.id.lblNombre2)
        private val lblCorreo: TextView = view.findViewById(R.id.lblCorreo)
        private val lblTelefono: TextView = view.findViewById(R.id.lblTelefono)
        private var contactoActual: Contacto = Contacto(0, "", "", "", emptyList(), emptyList(), "")

        init {
            val btnEditar = view.findViewById<Button>(R.id.btnEditar)
            btnEditar.setOnClickListener {
                val fragment = contacto_form()
                val bundle = Bundle()
                contactoActual.id?.let { id ->
                    bundle.putLong("Contacto_ID", id)
                }
                fragment.arguments = bundle

                val activity = view.context as? AppCompatActivity
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.commit()
            }

            val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
            btnEliminar.setOnClickListener {
                onEliminarClick(contactoActual)
            }
        }

        fun render(contacto: Contacto) {
            contactoActual = contacto
            lblNombre2.text = contacto.nombre
            lblCorreo.text = contacto.email
            lblTelefono.text = contacto.numeroTelfono
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ContactoHolder(layoutInflater.inflate(R.layout.item_contacto, parent, false), onEliminarClick)
    }

    override fun onBindViewHolder(holder: ContactoHolder, position: Int) {
        holder.render(contactosFiltrados[position])
    }

    override fun getItemCount(): Int {
        return contactosFiltrados.size
    }

    // Método para filtrar la lista según el texto ingresado
    fun filtrar(query: String) {
        val texto = query.lowercase().trim()
        contactosFiltrados.clear()
        if (texto.isEmpty()) {
            contactosFiltrados.addAll(contactosOriginales)
        } else {
            val filtrados = contactosOriginales.filter {
                (it.nombre?.lowercase() ?: "").contains(texto) ||
                        (it.email?.lowercase() ?: "").contains(texto) ||
                        (it.numeroTelfono?.lowercase() ?: "").contains(texto)
            }
            contactosFiltrados.addAll(filtrados)

        }
        notifyDataSetChanged()
    }
}
