package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.adapter.EventoAdapter
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentEventoBinding
import udelp.edu.mx.agendakotlin.model.Evento
import udelp.edu.mx.agendakotlin.service.EventoService

class EventoFragment : Fragment() {

    private var _binding: FragmentEventoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EventoAdapter
    private lateinit var recyclerView: RecyclerView
    private var eventosOriginal: MutableList<Evento> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycleEvento)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val api = Clients.instance(requireContext()).create(EventoService::class.java)

        api.getAll().enqueue(object : Callback<List<Evento>> {
            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                if (response.isSuccessful) {
                    eventosOriginal = response.body()?.toMutableList() ?: mutableListOf()
                    Log.d("EventoFragment", "Eventos recibidos: ${eventosOriginal.size}")

                    adapter = EventoAdapter(eventosOriginal) { eventoSeleccionado ->
                        Log.d("EventoFragment", "Editar evento: ${eventoSeleccionado.titulo}")
                    }

                    recyclerView.adapter = adapter
                } else {
                    Log.e("EventoFragment", "Error al obtener eventos, código: ${response.code()}, mensaje: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                Log.e("EventoFragment", "Fallo de red: ${t.message}")
            }
        })

        val searchView = view.findViewById<SearchView>(R.id.searchViewEvent)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::adapter.isInitialized) {  // Evitar llamar antes de asignar adapter
                    adapter.filtrar(newText ?: "")
                }
                return true
            }
        })

        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAddEvent)
        btnAdd.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, EventoForm())
                ?.commit()
            arguments?.putInt("Evento_ID", 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
