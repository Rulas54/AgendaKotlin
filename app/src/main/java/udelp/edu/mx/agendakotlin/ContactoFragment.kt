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
import udelp.edu.mx.agendakotlin.adapter.ContactoAdapter
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentContactoBinding
import udelp.edu.mx.agendakotlin.databinding.FragmentTareasViewBinding
import udelp.edu.mx.agendakotlin.model.Contacto
import udelp.edu.mx.agendakotlin.service.ContactoService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_contacto.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentContactoBinding? = null
    private lateinit var adapter: ContactoAdapter
    private lateinit var rv: RecyclerView
    private var contactos: List<Contacto> = listOf()



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {

        _binding = FragmentContactoBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
            binding.buttonSecond.setOnClickListener {
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }

         */
        val api = Clients.instance(view.context).create(ContactoService::class.java)
        rv = view.findViewById(R.id.recycleContacto)


        api.getAll().enqueue(object : Callback<List<Contacto>> {
            override fun onResponse(call: Call<List<Contacto>>, response: Response<List<Contacto>>) {
                Log.d("contacto", call.request().url().toString())

                if (response.isSuccessful) {
                    // Inicializa la lista de contactos de forma mutable
                    val mutableContactos = response.body()?.toMutableList() ?: mutableListOf()

                    // Configura el RecyclerView
                    val rv: RecyclerView = view.findViewById(R.id.recycleContacto)
                    rv.layoutManager = LinearLayoutManager(view.context)

                    adapter = ContactoAdapter(mutableContactos) { contactoAEliminar ->
                        // La lógica de eliminación sigue igual
                        api.remove(contactoAEliminar.id!!, contactoAEliminar).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    mutableContactos.remove(contactoAEliminar)
                                    adapter.notifyDataSetChanged()
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
                    rv.layoutManager = LinearLayoutManager(view.context)
                    rv.adapter = adapter

                    // Asigna el adapter al RecyclerView
                    rv.adapter = adapter
                } else {
                    Log.e("Error", "Error al obtener los contactos")
                }
            }

            override fun onFailure(call: Call<List<Contacto>>, t: Throwable) {
                Log.e("Error", "Fallo de red al obtener los contactos: ${t.message}")
            }
        })




        binding.btnAdd.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()!!
                .replace(R.id.fragment_container, contacto_form()).commit()

            arguments?.putInt("Contacto_ID",0);
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_contacto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}