package udelp.edu.mx.agendakotlin.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.TareasFormFragment
import udelp.edu.mx.agendakotlin.model.Tarea

// Define un alias de tipo para la función de callback para mayor legibilidad
typealias OnTareaUpdateListener = (Tarea, Int) -> Unit

class TareaAdapter(
    private val tareas: MutableList<Tarea>,
    private val onTareaUpdate: OnTareaUpdateListener
) : RecyclerView.Adapter<TareaAdapter.TareaHolder>() {

    inner class TareaHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val lblNombre: TextView = view.findViewById(R.id.lblNombre)
        private val lblPrioridad: TextView = view.findViewById(R.id.lblPrioridad)
        private val lblDescripcion: TextView = view.findViewById(R.id.lblDescripcion)
        private val tvFechaSeleccionada: TextView = view.findViewById(R.id.tvFechaSeleccionada)
        private val spEstado: Spinner = view.findViewById(R.id.spEstado)
        private val spEtiqueta: Spinner = view.findViewById(R.id.spEtiqueta)
        private val btnHecho: Button = view.findViewById(R.id.btnHecho)

        private var tareaActual: Tarea? = null

        init {
            // Click para editar tarea
            view.setOnClickListener {
                val fragment = TareasFormFragment()
                val bundle = Bundle()
                tareaActual?.id?.let { id ->
                    bundle.putLong("Tarea_ID", id)
                }
                fragment.arguments = bundle

                val activity = view.context as? AppCompatActivity
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }

            // Botón "Hecho" para cambiar estado
            btnHecho.setOnClickListener {
                tareaActual?.let { tarea ->
                    // We will only allow marking as "Hecho". To "Deshacer" it,
                    // the user would need to edit the task, or we'd add another button/logic.
                    // If the button is hidden when "Hecho", this click listener won't be triggered.
                    if (tarea.estado != "Hecho") { // Only proceed if not already "Hecho"
                        val tareaActualizada = tarea.copy(estado = "Hecho")
                        val posicion = adapterPosition

                        if (posicion != RecyclerView.NO_POSITION) {
                            // Update the item in the list
                            tareas[posicion] = tareaActualizada
                            // No need to notifyItemChanged if we're also moving and hiding
                            // notifyItemChanged(posicion)

                            // Mover la tarea al final de la lista si lo deseas
                            // Remove it from its current position
                            tareas.removeAt(posicion)
                            // Add it to the end
                            tareas.add(tareaActualizada)
                            // Notify about the move
                            notifyItemMoved(posicion, tareas.size - 1)

                            // Notificar callback para que el fragmento/actividad pueda guardar o actualizar UI
                            onTareaUpdate(tareaActualizada, posicion)

                            // Since the button will disappear, no need to update its text here.
                            // The next call to render (if the view is rebound) will handle visibility.
                        }
                    }
                }
            }
        }

        fun render(tarea: Tarea) {
            tareaActual = tarea

            lblNombre.text = tarea.nombre
            lblDescripcion.text = tarea.descripcion
            lblPrioridad.text = tarea.prioridad.toString()
            tvFechaSeleccionada.text = tarea.fechaVencimiento

            // Spinner Estado (deshabilitado)
            val estados = listOf("Por hacer", "En proceso", "Hecho")
            val adapterEstado = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, estados)
            adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spEstado.adapter = adapterEstado
            val posEstado = adapterEstado.getPosition(tarea.estado)
            if (posEstado >= 0) spEstado.setSelection(posEstado)
            spEstado.isEnabled = false
            spEstado.isClickable = false

            // Spinner Etiqueta (deshabilitado)
            val etiquetas = listOf("Escuela", "Trabajo", "Personal", "Urgente", "Otro")
            val adapterEtiqueta = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, etiquetas)
            adapterEtiqueta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spEtiqueta.adapter = adapterEtiqueta
            val posEtiqueta = adapterEtiqueta.getPosition(tarea.etiqueta)
            if (posEtiqueta >= 0) spEtiqueta.setSelection(posEtiqueta)
            spEtiqueta.isEnabled = false
            spEtiqueta.isClickable = false

            // --- CRUCIAL CHANGE HERE ---
            // Set button visibility based on task state
            if (tarea.estado == "Hecho") {
                btnHecho.visibility = View.GONE // Make the button disappear (takes no space)
                // If you want it to just be invisible but still take space:
                // btnHecho.visibility = View.INVISIBLE
            } else {
                btnHecho.visibility = View.VISIBLE // Make the button visible
                btnHecho.text = "Marcar Hecho" // Ensure text is correct for non-Hecho state
            }
            // If you implement a "Deshacer" functionality, you'd add a separate button for it
            // or have the current button toggle between "Marcar Hecho" and "Deshacer"
            // AND control its visibility based on whether it's "Hecho" or not.
            // For now, only "Marcar Hecho" button is managed, and it disappears when done.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TareaHolder(layoutInflater.inflate(R.layout.item_tarea, parent, false))
    }

    override fun onBindViewHolder(holder: TareaHolder, position: Int) {
        holder.render(tareas[position])
    }

    override fun getItemCount(): Int = tareas.size
}