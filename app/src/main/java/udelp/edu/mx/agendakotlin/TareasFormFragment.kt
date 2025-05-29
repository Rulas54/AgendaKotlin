package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentTareasFormBinding
import udelp.edu.mx.agendakotlin.service.TareaService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TareasFormFragment : Fragment() {

    private var tareaId: Long? = null
    private var _binding: FragmentTareasFormBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            tareaId = it.getLong("Tarea_ID")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTareasFormBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tareaId != null && tareaId != 0L) {
            binding.btnAceptar.text = "Actualizar Tarea"
        } else {
            binding.btnAceptar.text = "Agregar Tarea"
        }


        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        var tarea : Tarea = Tarea(null,date,"Luis","Algo",1)
        val api = Clients.instance(view.context).create(TareaService::class.java)
        Log.e(tareaId.toString(),tareaId.toString())


        if (null != tareaId && tareaId != 0L){
            val api = Clients.instance(view.context).create(TareaService::class.java)
            api.get(tareaId!!).enqueue(object : Callback<Tarea> {
                override fun onResponse(p0: Call<Tarea>, response: Response<Tarea>) {
                    if (response.isSuccessful){
                        val tarea = response.body()

                        val lblNombre: TextView = view.findViewById<TextView>(R.id.txtNombre)
                        lblNombre.apply {
                            text = tarea?.nombre
                        }

                        val lblPrioridad: TextView = view.findViewById<TextView>(R.id.txtPrioridad)
                        lblPrioridad.apply {
                            text = tarea?.prioridad.toString()
                        }

                        val lblDescripción: TextView = view.findViewById<TextView>(R.id.txtDescripcion)
                        lblDescripción.apply {
                            text = tarea?.descripcion
                        }
                    }
                }

                override fun onFailure(p0: Call<Tarea>, p1: Throwable) {

                }
            })
        }

        Log.i("Tag", tareaId.toString())


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
                        Snackbar.make(
                            requireView(),
                            "Tarea Agregada",
                            BaseTransientBottomBar.LENGTH_SHORT
                        ).show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, TareasViewFragment())
                            .commit()

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
            activity?.supportFragmentManager?.beginTransaction()!!
                .replace(R.id.fragment_container, TareasViewFragment()).commit()
        }

        binding.btnVerContactos.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()!!
                .replace(R.id.fragment_container, ContactoFragment()).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}