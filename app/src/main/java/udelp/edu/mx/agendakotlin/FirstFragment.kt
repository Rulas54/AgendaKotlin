package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentFirstBinding
import udelp.edu.mx.agendakotlin.service.TareaService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        var tarea : Tarea = Tarea(null,date,"Luis","Algo",1)
        val api = Clients.instance(view.context).create(TareaService::class.java)

        api.add(tarea).enqueue(object : Callback<Tarea> {
            override fun onResponse(call: Call<Tarea>, response: Response<Tarea>) {
                if(response.isSuccessful) {
                    Log.d("Tarea", response.body().toString())
                }
            }
            override fun onFailure(call: Call<Tarea>, t: Throwable) {
                Log.e("Error", "Error en la API: ${t.message}", t)
            }
        })



        binding.btnAceptar.setOnClickListener {
            val nombre : TextView = view.findViewById<TextView>(R.id.txtNombre)
            val prioridad : TextView = view.findViewById<TextView>(R.id.txtPrioridad)
            val descripcion : TextView = view.findViewById<TextView>(R.id.txtDescripcion)

            //------------------------------------------------------------------------------------
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            var tarea : Tarea = Tarea(null,date,nombre.text.toString(),descripcion.text.toString(),prioridad.text.toString().toInt())
            val api = Clients.instance(view.context).create(TareaService::class.java)

            api.add(tarea).enqueue(object : Callback<Tarea> {
                override fun onResponse(call: Call<Tarea>, response: Response<Tarea>) {
                    if(response.isSuccessful) {
                        Toast.makeText(context,"Tarea agregada",Toast.LENGTH_SHORT).show()
                        Log.d("Tarea", response.body().toString())

                    }
                }
                override fun onFailure(call: Call<Tarea>, t: Throwable) {
                    Log.e("Error", "Error en la API: ${t.message}", t)
                }
            })


            //Snackbar.make(view, nombre.text, Snackbar.LENGTH_LONG)
                //.setAction("Action", null)
               // .setAnchorView(R.id.fab).show()
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.btnVerTodos.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.btnVerContactos.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_contactoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}