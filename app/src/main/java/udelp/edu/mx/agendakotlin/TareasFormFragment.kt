package udelp.edu.mx.agendakotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentTareasFormBinding
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.service.TareaService
import java.text.SimpleDateFormat
import java.util.*

class TareasFormFragment : Fragment() {

    private var tareaId: Long? = null
    private var _binding: FragmentTareasFormBinding? = null
    private lateinit var btnFechaVencimiento: Button
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var spEstado: Spinner
    private lateinit var spEtiqueta: Spinner
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

        btnFechaVencimiento = view.findViewById(R.id.btnFechaVencimiento)
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada)
        spEstado = view.findViewById(R.id.spEstado)
        spEtiqueta = view.findViewById(R.id.spEtiqueta)

        val estados = listOf("Por hacer", "En proceso", "Hecho")
        val etiquetas = listOf("Escuela", "Trabajo", "Personal", "Urgente", "Otro")

        val adapterEstado = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEstado.adapter = adapterEstado

        val adapterEtiqueta = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, etiquetas)
        adapterEtiqueta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEtiqueta.adapter = adapterEtiqueta

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

        // Si es edición, cargar datos
        if (tareaId != null && tareaId != 0L) {
            binding.btnAceptar.text = "Actualizar Tarea"

            val api = Clients.instance(view.context).create(TareaService::class.java)
            api.get(tareaId!!).enqueue(object : Callback<Tarea> {
                override fun onResponse(call: Call<Tarea>, response: Response<Tarea>) {
                    if (response.isSuccessful) {
                        val tarea = response.body()

                        view.findViewById<EditText>(R.id.txtNombre).setText(tarea?.nombre)
                        view.findViewById<EditText>(R.id.txtPrioridad).setText(tarea?.prioridad.toString())
                        view.findViewById<EditText>(R.id.txtDescripcion).setText(tarea?.descripcion)
                        tvFechaSeleccionada.text = tarea?.fechaVencimiento

                        tarea?.let {
                            val estadoPos = adapterEstado.getPosition(it.estado)
                            spEstado.setSelection(estadoPos)

                            val etiquetaPos = adapterEtiqueta.getPosition(it.etiqueta)
                            spEtiqueta.setSelection(etiquetaPos)
                        }
                    }
                }

                override fun onFailure(call: Call<Tarea>, t: Throwable) {
                    Log.e("API", "Error al cargar tarea: ${t.message}", t)
                }
            })
        } else {
            binding.btnAceptar.text = "Agregar Tarea"
        }

        // Botón Aceptar
        binding.btnAceptar.setOnClickListener {
            val nombre = view.findViewById<EditText>(R.id.txtNombre).text.toString()
            val prioridad = view.findViewById<EditText>(R.id.txtPrioridad).text.toString().toIntOrNull() ?: 1
            val descripcion = view.findViewById<EditText>(R.id.txtDescripcion).text.toString()
            val estadoSeleccionado = spEstado.selectedItem.toString()
            val etiquetaSeleccionada = spEtiqueta.selectedItem.toString()
            val fechaVencimiento = tvFechaSeleccionada.text.toString()
            val fechaCreacion = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val api = Clients.instance(view.context).create(TareaService::class.java)

            if (tareaId != null && tareaId != 0L) {
                // Actualizar tarea
                val tareaActualizar = Tarea(
                    id = tareaId,
                    fechaCreacion = fechaCreacion,
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaVencimiento = fechaVencimiento,
                    estado = estadoSeleccionado,
                    etiqueta = etiquetaSeleccionada,
                    prioridad = prioridad
                )

                api.edit(tareaId!!, tareaActualizar).enqueue(object : Callback<Tarea> {
                    override fun onResponse(call: Call<Tarea>, response: Response<Tarea>) {
                        if (response.isSuccessful) {
                            Snackbar.make(requireView(), "Tarea Actualizada", Snackbar.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, TareasViewFragment())
                                .commit()
                        }
                    }

                    override fun onFailure(call: Call<Tarea>, t: Throwable) {
                        Log.e("API", "Error al actualizar tarea: ${t.message}", t)
                    }
                })
            } else {
                // Agregar nueva tarea
                val nuevaTarea = Tarea(
                    id = null,
                    fechaCreacion = fechaCreacion,
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaVencimiento = fechaVencimiento,
                    estado = estadoSeleccionado,
                    etiqueta = etiquetaSeleccionada,
                    prioridad = prioridad
                )

                api.add(nuevaTarea).enqueue(object : Callback<Tarea> {
                    override fun onResponse(call: Call<Tarea>, response: Response<Tarea>) {
                        if (response.isSuccessful) {
                            Snackbar.make(requireView(), "Tarea Agregada", Snackbar.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, TareasViewFragment())
                                .commit()
                        }
                    }

                    override fun onFailure(call: Call<Tarea>, t: Throwable) {
                        Log.e("API", "Error al agregar tarea: ${t.message}", t)
                    }
                })
            }
        }

        // Botones inferiores
        binding.btnVerTodos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TareasViewFragment())
                .commit()
        }

        binding.btnVerContactos.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ContactoFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
