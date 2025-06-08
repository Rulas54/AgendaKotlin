package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.adapter.TareaAdapter // Asegúrate de que esta importación sea correcta
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentTareasViewBinding
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.service.TareaService
import android.widget.Toast // Importación necesaria para Toast

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareasViewFragment : Fragment() {

    private var _binding: FragmentTareasViewBinding? = null
    private val binding get() = _binding!!

    // Hacemos mutable la lista para poder modificarla
    private val tareasMutable = mutableListOf<Tarea>()

    // Adapter variable para poder notificar cambios después
    private lateinit var adapter: TareaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTareasViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = Clients.instance(view.context).create(TareaService::class.java)

        api.getAll().enqueue(object : Callback<List<Tarea>> {
            override fun onResponse(call: Call<List<Tarea>>, response: Response<List<Tarea>>) {
                if (response.isSuccessful) {
                    response.body()?.let { tareas ->
                        // Copiamos la lista recibida a mutableList
                        tareasMutable.clear()
                        tareasMutable.addAll(tareas)

                        val rv: RecyclerView = view.findViewById(R.id.recycledDatos)
                        rv.layoutManager = LinearLayoutManager(view.context)
                        adapter = TareaAdapter(tareasMutable) { tareaActualizada: Tarea, posicionOriginal: Int ->
                            // Aquí se actualiza la lista local y se notifica al adaptador.
                            // Generalmente, para una actualización de un elemento existente (como marcar "hecho"),
                            // es mejor actualizar el elemento en su posición original.
                            if (posicionOriginal >= 0 && posicionOriginal < tareasMutable.size) {
                                tareasMutable[posicionOriginal] = tareaActualizada
                                adapter.notifyItemChanged(posicionOriginal)
                            } else {
                                // Esto es un fallback o si realmente se quiere mover el elemento al final.
                                // Si este bloque se ejecuta mucho, revisa tu lógica de posiciones.
                                Log.w("TareasViewFragment", "Posición original fuera de rango o lógica de movimiento no estándar.")
                                tareasMutable.removeAt(posicionOriginal)
                                tareasMutable.add(tareaActualizada)
                                adapter.notifyItemMoved(posicionOriginal, tareasMutable.size - 1)
                                adapter.notifyItemChanged(tareasMutable.size - 1)
                            }

                            // Toast con requireContext() porque estamos dentro del fragmento
                            Toast.makeText(requireContext(), "Estado de la tarea actualizado", Toast.LENGTH_SHORT).show()
                        }

                        rv.adapter = adapter
                    }
                } else {
                    Log.e("TareasViewFragment", "Error en la respuesta del API: ${response.code()}")
                    Toast.makeText(requireContext(), "Error al cargar tareas: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Tarea>>, t: Throwable) {
                Log.e("TareasViewFragment", "Error de red o API: ${t.message}", t)
                Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        binding.btnAdd.setOnClickListener {
            // Crea una instancia del Fragmento de formulario
            val tareasFormFragment = TareasFormFragment()

            // Crea un Bundle para pasar argumentos (por ejemplo, para indicar que es una nueva tarea)
            val args = Bundle()
            args.putInt("Tareas_ID", 0) // Usamos 0 o un valor similar para indicar "nueva tarea"
            tareasFormFragment.arguments = args // Asigna los argumentos al nuevo fragmento

            // Inicia la transacción del fragmento
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, tareasFormFragment)
                ?.addToBackStack(null) // Opcional: añade la transacción a la pila de retroceso para volver con el botón "atrás"
                ?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}