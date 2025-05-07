package udelp.edu.mx.agendakotlin.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.TareasFormFragment
import udelp.edu.mx.agendakotlin.contacto_form
import udelp.edu.mx.agendakotlin.model.Contacto

class ContactoAdapter(private val  contactos : List<Contacto>, private val onEliminarClick: (Contacto) -> Unit) : RecyclerView.Adapter<ContactoAdapter.ContactoHolder>() {
    class ContactoHolder(val view: View, val onEliminarClick: (Contacto) -> Unit) : RecyclerView.ViewHolder(view) {

        private val lblNombre2: TextView = view.findViewById(R.id.lblNombre2)
        private val lblCorreo: TextView = view.findViewById(R.id.lblCorreo)
        private val lblDireccion: TextView = view.findViewById(R.id.lblDireccion)
        private val lblTelefono: TextView = view.findViewById(R.id.lblTelefono)
        private var contactoActual: Contacto = Contacto(0, "", "", "", emptyList(), emptyList(), "")




        init {
                val btnEditar = view.findViewById<Button>(R.id.btnEditar)

                btnEditar.setOnClickListener {
                    val fragment = contacto_form()
                    val bundle = Bundle()
                    contactoActual.id?.let { it1 ->
                        bundle.putLong("Contacto_ID", it1)
                    }
                    fragment.arguments = bundle

                    val activity = view.context as? AppCompatActivity
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, fragment)
                        ?.commit()


                }

            val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

            btnEliminar.setOnClickListener {
                onEliminarClick(contactoActual) // ejecuta la función que viene del fragment
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
                    append("\nExtra: ${contacto.numeroAdicional.joinToString("\nExtra: ")}")
                }
            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactoHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return ContactoHolder(layoutInflater.inflate(R.layout.item_contacto,p0, false), onEliminarClick)

    }

    override fun onBindViewHolder(p0: ContactoHolder, p1: Int) {
        p0.render(contactos[p1])
    }


    override fun getItemCount(): Int {
        return contactos.size
    }

}