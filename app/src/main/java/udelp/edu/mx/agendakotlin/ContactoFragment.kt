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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.adapter.ContactoAdapter
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentContactoBinding
import udelp.edu.mx.agendakotlin.model.Contacto
import udelp.edu.mx.agendakotlin.service.ContactoService

class ContactoFragment : Fragment() {

    private var _binding: FragmentContactoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ContactoAdapter
    private lateinit var recyclerView: RecyclerView
    private var contactosOriginal: MutableList<Contacto> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycleContacto)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val api = Clients.instance(requireContext()).create(ContactoService::class.java)

        api.getAll().enqueue(object : Callback<List<Contacto>> {
            override fun onResponse(call: Call<List<Contacto>>, response: Response<List<Contacto>>) {
                if (response.isSuccessful) {
                    contactosOriginal = response.body()?.toMutableList() ?: mutableListOf()

                    adapter = ContactoAdapter(contactosOriginal.toMutableList()) { contactoAEliminar ->
                        api.remove(contactoAEliminar.id!!, contactoAEliminar).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    contactosOriginal.remove(contactoAEliminar)
                                    adapter.filtrar("") // Refresca la lista
                                    Log.d("Contacto", "Eliminado correctamente")
                                } else {
                                    Log.e("Error", "Error al eliminar contacto")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("Error", "Fallo de red al eliminar: ${t.message}")
                            }
                        })
                    }

                    recyclerView.adapter = adapter
                } else {
                    Log.e("Error", "Error al obtener los contactos")
                }
            }

            override fun onFailure(call: Call<List<Contacto>>, t: Throwable) {
                Log.e("Error", "Fallo de red: ${t.message}")
            }
        })

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filtrar(newText ?: "")
                return true
            }
        })

        binding.btnAdd.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, contacto_form())?.commit()
            arguments?.putInt("Contacto_ID", 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
