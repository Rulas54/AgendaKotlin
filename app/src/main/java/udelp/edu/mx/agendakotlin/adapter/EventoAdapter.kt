package udelp.edu.mx.agendakotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.model.Evento

class EventoAdapter(
    private var eventos: MutableList<Evento>,
    private val onEditarClick: (Evento) -> Unit
) : RecyclerView.Adapter<EventoAdapter.EventoViewHolder>() {

    private var eventosFiltrados = eventos.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return EventoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        val evento = eventosFiltrados[position]
        holder.bind(evento)
    }

    override fun getItemCount(): Int = eventosFiltrados.size


    fun filtrar(texto: String) {
        val textoLower = texto.lowercase()
        eventosFiltrados = if (textoLower.isEmpty()) {
            eventos.toMutableList()
        } else {
            eventos.filter { evento ->
                (evento.titulo?.lowercase()?.contains(textoLower) ?: false) ||
                        (evento.descripcion?.lowercase()?.contains(textoLower) ?: false) ||
                        (evento.ubicacion?.lowercase()?.contains(textoLower) ?: false)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }


    inner class EventoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lblTitulo = itemView.findViewById<TextView>(R.id.lblTitulo)
        private val lblDescripcion = itemView.findViewById<TextView>(R.id.lblDescripcion)
        private val lblFechaInicio = itemView.findViewById<TextView>(R.id.lblFechaInicio)
        private val lblFechaFin = itemView.findViewById<TextView>(R.id.lblFechaFin)
        private val lblUbicacion = itemView.findViewById<TextView>(R.id.lblUbicacion)
        private val btnEditar = itemView.findViewById<Button>(R.id.btnEditarEvento)

        fun bind(evento: Evento) {
            lblTitulo.text = evento.titulo
            lblDescripcion.text = evento.descripcion
            lblFechaInicio.text = evento.fechaInicio
            lblFechaFin.text = evento.fechaFin
            lblUbicacion.text = evento.ubicacion

            btnEditar.setOnClickListener {
                onEditarClick(evento)
            }
        }
    }
}

