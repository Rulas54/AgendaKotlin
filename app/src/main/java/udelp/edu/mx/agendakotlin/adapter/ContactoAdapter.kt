package udelp.edu.mx.agendakotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.model.Contacto

class ContactoAdapter(private val  contactos : List<Contacto>) : RecyclerView.Adapter<ContactoAdapter.ContactoHolder>() {
    class ContactoHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val lblNombre2: TextView = view.findViewById(R.id.lblNombre2)
        private val lblCorreo: TextView = view.findViewById(R.id.lblCorreo)
        private val lblDireccion: TextView = view.findViewById(R.id.lblDireccion)
        private val lblTelefono: TextView = view.findViewById(R.id.lblTelefono)
        private var contactoActual: Contacto = Contacto(0, "", "", "", emptyList(), emptyList(), "")

        init {
            view.setOnClickListener {
                Toast.makeText(view.context, contactoActual.nombre, Toast.LENGTH_LONG).show()
            }
        }

        fun render(contacto: Contacto) {
            contactoActual = contacto

            lblNombre2.text = contacto.nombre
            lblCorreo.text = buildString {
                append(contacto.email)
                if (!contacto.correoAdicional.isNullOrEmpty()) {
                    append("\nExtra: ${contacto.correoAdicional.joinToString()}")
                }
            }

            lblDireccion.text = contacto.direccion

            lblTelefono.text = buildString {
                append(contacto.numeroTelfono)
                if (!contacto.numeroAdicional.isNullOrEmpty()) {
                    append("\nExtra: ${contacto.numeroAdicional.joinToString()}")
                }
            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactoHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return ContactoHolder(layoutInflater.inflate(R.layout.item_contacto,p0, false))
    }

    override fun onBindViewHolder(p0: ContactoHolder, p1: Int) {
        p0.render(contactos[p1])
    }


    override fun getItemCount(): Int {
        return contactos.size
    }

}