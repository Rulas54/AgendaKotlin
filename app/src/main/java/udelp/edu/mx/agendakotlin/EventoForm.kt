package udelp.edu.mx.agendakotlin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentEventoFormBinding
import udelp.edu.mx.agendakotlin.model.Contacto
import udelp.edu.mx.agendakotlin.model.Evento
import udelp.edu.mx.agendakotlin.service.ContactoService
import udelp.edu.mx.agendakotlin.service.EventoService

import java.text.SimpleDateFormat
import java.util.*

class EventoForm : Fragment() {

    private var _binding: FragmentEventoFormBinding? = null
    private val binding get() = _binding!!

    private var selectedFechaInicio: Calendar = Calendar.getInstance()
    private var selectedFechaFin: Calendar = Calendar.getInstance()

    private val participantes: MutableList<Contacto> = mutableListOf()

    private val dateTimeFormatForBackend: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    }


    private val dateTimeFormatForDisplay: SimpleDateFormat by lazy {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventoFormBinding.inflate(inflater, container, false)

        selectedFechaInicio.time = Date()
        selectedFechaFin.time = Date(System.currentTimeMillis() + 3600000)
        updateDateButtons()

        binding.btnFechaInicio.setOnClickListener {
            showDateTimePicker(selectedFechaInicio) { year, month, day, hour, minute ->
                selectedFechaInicio.set(year, month, day, hour, minute)
                updateDateButtons()
            }
        }

        binding.btnFechaFin.setOnClickListener {
            showDateTimePicker(selectedFechaFin) { year, month, day, hour, minute ->
                selectedFechaFin.set(year, month, day, hour, minute)
                updateDateButtons()
            }
        }

        binding.btnSeleccionarParticipantes.setOnClickListener {
            mostrarDialogoSeleccionContactos()
        }

        binding.btnGuardarEvento.setOnClickListener {
            guardarNuevoEvento()
        }

        updateParticipantesChips()

        return binding.root
    }

    private fun showDateTimePicker(calendar: Calendar, onDateTimeSelected: (Int, Int, Int, Int, Int) -> Unit) {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                onDateTimeSelected(year, month, dayOfMonth, hourOfDay, minute)
            }, currentHour, currentMinute, true).show()
        }, currentYear, currentMonth, currentDay).show()
    }

    private fun updateDateButtons() {
        binding.btnFechaInicio.text = dateTimeFormatForDisplay.format(selectedFechaInicio.time)
        binding.btnFechaFin.text = dateTimeFormatForDisplay.format(selectedFechaFin.time)
    }

    private fun mostrarDialogoSeleccionContactos() {
        val api = Clients.instance(requireContext()).create(ContactoService::class.java)

        api.getAll().enqueue(object : Callback<List<Contacto>> {
            override fun onResponse(call: Call<List<Contacto>>, response: Response<List<Contacto>>) {
                if (response.isSuccessful) {
                    val allContacts = response.body() ?: emptyList()
                    if (allContacts.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay contactos disponibles para seleccionar.", Toast.LENGTH_LONG).show()
                        return
                    }

                    val contactNames = allContacts.map { it.nombre }.toTypedArray()
                    val selectedIndices = BooleanArray(allContacts.size) { i ->
                        participantes.contains(allContacts[i])
                    }

                    AlertDialog.Builder(requireContext())
                        .setTitle("Selecciona participantes")
                        .setMultiChoiceItems(contactNames, selectedIndices) { _, which, isChecked ->
                            selectedIndices[which] = isChecked
                        }
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            participantes.clear()
                            for (i in allContacts.indices) {
                                if (selectedIndices[i]) {
                                    participantes.add(allContacts[i])
                                }
                            }
                            updateParticipantesChips()
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.cancel()
                        }
                        .show()

                } else {
                    Log.e("ContactoAPI", "Error al cargar contactos: ${response.code()} - ${response.errorBody()?.string()}")
                    Snackbar.make(binding.root, "Error al cargar contactos desde el servidor.", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Contacto>>, t: Throwable) {
                Log.e("ContactoAPI", "Fallo de conexión al cargar contactos: ${t.message}", t)
                Snackbar.make(binding.root, "No se pudo conectar al servidor para obtener contactos.", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun updateParticipantesChips() {
        binding.chipGroupParticipantes.removeAllViews()

        if (participantes.isEmpty()) {
            val noParticipantsChip = Chip(requireContext())
            noParticipantsChip.text = "Ningún participante seleccionado"
            noParticipantsChip.isCloseIconVisible = false
            binding.chipGroupParticipantes.addView(noParticipantsChip)
        } else {
            participantes.forEach { contacto ->
                val chip = Chip(requireContext())
                chip.text = contacto.nombre
                chip.isCloseIconVisible = true

                chip.setOnCloseIconClickListener {
                    participantes.remove(contacto)
                    updateParticipantesChips()
                    Snackbar.make(binding.root, "${contacto.nombre} eliminado", Snackbar.LENGTH_SHORT).show()
                }
                binding.chipGroupParticipantes.addView(chip)
            }
        }
    }

    private fun guardarNuevoEvento() {
        val titulo = binding.etTitulo.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()

        if (titulo.isEmpty()) {
            binding.tilTitulo.error = "El título es obligatorio"
            return
        } else {
            binding.tilTitulo.error = null
        }

        if (selectedFechaInicio == null || selectedFechaFin == null) {
            Snackbar.make(requireView(), "Por favor, selecciona las fechas de inicio y fin.", Snackbar.LENGTH_LONG).show()
            return
        }

        if (selectedFechaInicio.time.after(selectedFechaFin.time)) {
            Snackbar.make(
                requireView(),
                "La fecha de inicio no puede ser posterior a la fecha de fin.",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }

        val fechaInicioString = dateTimeFormatForBackend.format(selectedFechaInicio.time)
        val fechaFinString = dateTimeFormatForBackend.format(selectedFechaFin.time)

        val nuevoEvento = Evento(
            id = null,
            titulo = titulo,
            descripcion = descripcion,
            fechaInicio = fechaInicioString,
            fechaFin = fechaFinString,
            ubicacion = ubicacion,
            participantes = participantes.toList()
        )

        val api = Clients.instance(requireContext()).create(EventoService::class.java)

        api.add(nuevoEvento).enqueue(object : Callback<Evento> {
            override fun onResponse(call: Call<Evento>, response: Response<Evento>) {
                if (response.isSuccessful) {
                    Snackbar.make(
                        requireView(),
                        "Evento Registrado Correctamente",
                        BaseTransientBottomBar.LENGTH_SHORT
                    ).show()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, EventoFragment())
                        .commit()
                } else {
                    Log.e("API_EVENTO", "Error al registrar evento: ${response.code()} - ${response.errorBody()?.string()}")
                    Snackbar.make(requireView(), "Error al registrar evento.", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Evento>, t: Throwable) {
                Log.e("API_EVENTO", "Fallo de red al registrar evento: ${t.message}", t)
                Snackbar.make(requireView(), "No se pudo conectar para registrar el evento.", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}