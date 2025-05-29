package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
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
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import java.util.Calendar

import kotlin.math.log


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TareasFormFragment : Fragment() {

    private var tareaId: Long? = null
    private var _binding: FragmentTareasFormBinding? = null
    private lateinit var btnFechaVencimiento: Button
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var spEstado: Spinner
    private lateinit var spEtiqueta: Spinner

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

        //Cambio en el botón según la acción que se haga
        if (tareaId != null && tareaId != 0L) {
            binding.btnAceptar.text = "Actualizar Tarea"
        } else {
            binding.btnAceptar.text = "Agregar Tarea"
        }


        // Inicialización de Spinners y Fecha
        btnFechaVencimiento = view.findViewById(R.id.btnFechaVencimiento)
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada)
        spEstado = view.findViewById(R.id.spEstado)
        spEtiqueta = view.findViewById(R.id.spEtiqueta)

            // Spinner de Estado y etiqueta
        val estados = listOf("Por hacer", "En proceso", "Hecho")
        val etiquetas = listOf("Escuela", "Trabajo", "Personal", "Urgente", "Otro")

        val adapterEstado = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEstado.adapter = adapterEstado

        val adapterEtiqueta = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, etiquetas)
        adapterEtiqueta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEtiqueta.adapter = adapterEtiqueta


        // Calendario para Fecha de Vencimiento
        btnFechaVencimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, y, m, d ->
                val fecha = String.format("%04d-%02d-%02d", y, m + 1, d)
                tvFechaSeleccionada.text = fecha
            }, year, month, day).show()
        }



        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        var tarea : Tarea = Tarea(null, date, "Luis", "Algo", "2025-06-01", "Por hacer", "Escuela", 1)
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

                        tarea?.let {
                            val estadoPosition = adapterEstado.getPosition(it.estado)
                            spEstado.setSelection(estadoPosition)

                            val etiquetaPosition = adapterEtiqueta.getPosition(it.etiqueta)
                            spEtiqueta.setSelection(etiquetaPosition)

                            tvFechaSeleccionada.text = it.fechaVencimiento
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
            val estadoSeleccionado = spEstado.selectedItem.toString()
            val etiquetaSeleccionada = spEtiqueta.selectedItem.toString()
            val fechaVencimiento = tvFechaSeleccionada.text.toString()


            //------------------------------------------------------------------------------------
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            var tarea : Tarea = Tarea(null, date, nombre.text.toString(), descripcion.text.toString(), tvFechaSeleccionada.text.toString(), spEstado.selectedItem.toString(), spEtiqueta.selectedItem.toString(), prioridad.text.toString().toInt())
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