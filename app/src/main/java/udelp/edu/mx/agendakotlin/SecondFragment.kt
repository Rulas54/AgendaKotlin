package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.adapter.TareaAdapter
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentSecondBinding
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.service.TareaService

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    /*
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

     */
        val api = Clients.instance(view.context).create(TareaService::class.java)

        api.getAll().enqueue(object : Callback<List<Tarea>> {
            override fun onResponse(call: Call<List<Tarea>>, response: Response<List<Tarea>>) {
                Log.d("tarea",call.request().url().toString())
                if (response.isSuccessful) {
                    var tareas : List<Tarea> = ArrayList()

                    response.body()?.let {t ->
                        tareas = t
                        val rv : RecyclerView = view.findViewById(R.id.recycledDatos)
                        rv.layoutManager = LinearLayoutManager(view.context)

                        val adapter = TareaAdapter(tareas)
                        rv.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<Tarea>>, t: Throwable) {
                Log.e("Error", "Error en el API: ${t.message}")
            }
        })
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

