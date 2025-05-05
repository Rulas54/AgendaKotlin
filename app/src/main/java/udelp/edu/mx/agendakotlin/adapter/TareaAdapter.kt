package udelp.edu.mx.agendakotlin.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import udelp.edu.mx.agendakotlin.R
import udelp.edu.mx.agendakotlin.TareasFormFragment
import udelp.edu.mx.agendakotlin.model.Tarea

class TareaAdapter(private val tareas : List<Tarea>) : RecyclerView.Adapter<TareaAdapter.TareaHolder> (){

    class TareaHolder(val view: View) : RecyclerView.ViewHolder(view){
        private val lblNombre : TextView = view.findViewById(R.id.lblNombre)
        private val lblPrioridad : TextView = view.findViewById(R.id.lblPrioridad)
        private val lblDescripcion : TextView = view.findViewById(R.id.lblDescripcion)
        private var tareaActual: Tarea = Tarea(0,"","","",0)


        init {
            view.setOnClickListener {

                val fragment = TareasFormFragment()
                val bundle = Bundle()
                tareaActual.id?.let { it1 ->
                    bundle.putLong("Tarea_ID", it1)
                }
                fragment.arguments = bundle

                val activity = view.context as? AppCompatActivity
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.commit()

            }
        }

        fun render(tarea: Tarea){
            tareaActual = tarea
            lblNombre.apply {
                text = tarea.nombre
            }
            lblDescripcion.apply {
                text = tarea.descripcion
            }
            lblPrioridad.apply {
                text = tarea.prioridad.toString()
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TareaHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return TareaHolder(layoutInflater.inflate(R.layout.item_tarea, p0, false))
    }

    override fun onBindViewHolder(p0: TareaHolder, p1: Int) {
        p0.render(tareas[p1])
    }

    override fun getItemCount(): Int {
        return tareas.size
    }



}