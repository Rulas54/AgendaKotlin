package udelp.edu.mx.agendakotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentContactoFormBinding
import udelp.edu.mx.agendakotlin.databinding.FragmentTareasFormBinding
import udelp.edu.mx.agendakotlin.model.Contacto
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.service.ContactoService
import udelp.edu.mx.agendakotlin.service.TareaService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER




/**
 * A simple [Fragment] subclass.
 * Use the [contacto_form.newInstance] factory method to
 * create an instance of this fragment.
 */
class contacto_form : Fragment() {
    private lateinit var phoneNumbersContainer: LinearLayout
    private lateinit var btnAddPhone: ImageButton
    private lateinit var emailsContainer: LinearLayout
    private lateinit var btnAddMail: ImageButton


    private var contactoId: Long? = null
    private var _binding: FragmentContactoFormBinding? = null
    private val binding get() = _binding!!


    private var contactoExistente: Contacto? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactoId = it.getLong("Contacto_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactoFormBinding.inflate(inflater, container, false)

        // Inicializamos el contenedor de teléfonos y el botón utilizando binding
        phoneNumbersContainer = binding.phoneNumbersContainer
        btnAddPhone = binding.btnAddPhone

        // Inicializamos el contenedor de correos y el botón de agregar correo
        emailsContainer = binding.emailsContainer // Esto debe ser el contenedor de correos en tu XML
        btnAddMail = binding.btnAddMail // Esto debe ser el botón de agregar correo en tu XML

        // Establecemos el listener para el botón "Agregar Teléfono"
        btnAddPhone.setOnClickListener {
            addPhoneField()
        }

        // Establecemos el listener para el botón "Agregar Correo"
        btnAddMail.setOnClickListener {
            addEmailField() // Método para agregar un campo de correo electrónico
        }

        return binding.root
    }



    private fun addPhoneField() {
        // Creamos un LinearLayout para contener tanto el campo de teléfono como el botón de eliminación
        val phoneLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Creamos un EditText para el teléfono
        val editText = EditText(context).apply {
            hint = "Número de teléfono"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f  // Esto hará que el EditText ocupe el espacio disponible en el LinearLayout
            ).apply {
                setMargins(0, 8, 8, 8)  // Margen entre los campos
            }
            inputType = android.text.InputType.TYPE_CLASS_PHONE  // Tipo de campo para teléfono
        }

        // Creamos el botón de eliminación
        val btnRemovePhone = ImageButton(context).apply {
            setImageResource(android.R.drawable.ic_delete)  // Ícono de eliminación
            contentDescription = "Eliminar teléfono"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 0, 8)  // Margen entre el campo de teléfono y el botón
            }
            setOnClickListener {
                phoneNumbersContainer.removeView(phoneLayout)  // Eliminar el LinearLayout completo (campo + botón)
            }
        }

        // Añadimos el EditText y el botón de eliminación al LinearLayout
        phoneLayout.addView(editText)
        phoneLayout.addView(btnRemovePhone)

        // Añadimos el LinearLayout al contenedor de teléfonos
        phoneNumbersContainer.addView(phoneLayout)
    }


    private fun addEmailField() {
        val emailsContainer = view?.findViewById<LinearLayout>(R.id.emailsContainer) ?: return

        // Creamos un LinearLayout horizontal para contener el campo y el botón
        val emailLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // EditText para el nuevo correo
        val editText = EditText(requireContext()).apply {
            hint = "Correo electrónico"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(0, 8, 8, 8)
            }
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        // Botón para eliminar el campo
        val btnRemoveEmail = ImageButton(requireContext()).apply {
            setImageResource(android.R.drawable.ic_delete)
            contentDescription = "Eliminar correo"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 0, 8)
            }
            setBackgroundResource(android.R.color.transparent)
            setOnClickListener {
                emailsContainer.removeView(emailLayout)
            }
        }

        // Agrega el EditText y el botón al layout
        emailLayout.addView(editText)
        emailLayout.addView(btnRemoveEmail)

        // Agrega el layout al contenedor de correos
        emailsContainer.addView(emailLayout)
    }






        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            if (contactoId != null && contactoId != 0L) {
                binding.btnAgregarContact.text = "Actualizar Contacto"
            } else {
                binding.btnAgregarContact.text = "Agregar Contacto"
            }


            val emailsContainer = binding.emailsContainer
            val api = Clients.instance(view.context).create(ContactoService::class.java)

            //----------------------- Remplazar datos para editar -------------------------
            if (contactoId != null && contactoId != 0L) {
                val api = Clients.instance(requireContext()).create(ContactoService::class.java)
                api.get(contactoId!!).enqueue(object : Callback<Contacto> {
                    override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                        if (response.isSuccessful) {
                            val contacto = response.body()  // Ahora directamente es un solo objeto Contacto
                            if (contacto != null) {
                                // Establece los valores del contacto en los campos correspondientes
                                binding.txtNombreContact.setText(contacto.nombre)
                                binding.txtDireccionContact.setText(contacto.direccion)

                                // Teléfono principal
                                if (!contacto.numeroTelfono.isNullOrEmpty()) {
                                    // Verifica si ya se ha agregado un campo para teléfono
                                    if (binding.phoneNumbersContainer.childCount == 0) {
                                        addPhoneField()
                                    }
                                    val layout = binding.phoneNumbersContainer.getChildAt(0) as LinearLayout
                                    val editText = layout.getChildAt(0) as EditText
                                    editText.setText(contacto.numeroTelfono)
                                }

                                // Teléfonos adicionales
                                contacto.numeroAdicional?.forEach { numero ->
                                    // Agrega solo si hay espacio en el contenedor para un nuevo teléfono
                                    addPhoneField()
                                    val index = binding.phoneNumbersContainer.childCount - 1
                                    val layout = binding.phoneNumbersContainer.getChildAt(index) as LinearLayout
                                    val editText = layout.getChildAt(0) as EditText
                                    editText.setText(numero)
                                }

                                // Correo principal
                                if (!contacto.email.isNullOrEmpty()) {
                                    // Verifica si ya se ha agregado un campo para correo
                                    if (binding.emailsContainer.childCount == 0) {
                                        addEmailField()
                                    }
                                    val layout = binding.emailsContainer.getChildAt(0) as LinearLayout
                                    val editText = layout.getChildAt(0) as EditText
                                    editText.setText(contacto.email)
                                }

                                // Correos adicionales
                                contacto.correoAdicional?.forEach { correo ->
                                    // Agrega solo si hay espacio en el contenedor para un nuevo correo
                                    addEmailField()
                                    val index = binding.emailsContainer.childCount - 1
                                    val layout = binding.emailsContainer.getChildAt(index) as LinearLayout
                                    val editText = layout.getChildAt(0) as EditText
                                    editText.setText(correo)
                                }
                            } else {
                                Log.e("API", "El contacto es nulo.")
                            }
                        } else {
                            Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Contacto>, t: Throwable) {
                        Log.e("Error", "Error al obtener el contacto: ${t.message}", t)
                    }
                })
            }








            //-------------------------------------------------------------------------------

            binding.btnAgregarContact.setOnClickListener {
                val nombre = binding.txtNombreContact.text.toString().trim()
                val direccion = binding.txtDireccionContact.text.toString().trim()

                // ---------------- Captura TELÉFONOS ----------------
                val telefonos = mutableListOf<String>()
                for (i in 0 until binding.phoneNumbersContainer.childCount) {
                    val layout = binding.phoneNumbersContainer.getChildAt(i) as LinearLayout
                    for (j in 0 until layout.childCount) {
                        val viewInside = layout.getChildAt(j)
                        if (viewInside is EditText) {
                            val telefono = viewInside.text.toString().trim()
                            if (telefono.isNotEmpty()) {
                                telefonos.add(telefono)
                            }
                        }
                    }
                }
                val numeroPrincipal = if (telefonos.isNotEmpty()) telefonos[0] else ""
                val numerosAdicionales = if (telefonos.size > 1) telefonos.subList(1, telefonos.size) else emptyList()

                // ---------------- Captura EMAILS ----------------
                val emailsContainer = binding.emailsContainer
                val correos = mutableListOf<String>()
                for (i in 0 until emailsContainer.childCount) {
                    val layout = emailsContainer.getChildAt(i) as LinearLayout
                    for (j in 0 until layout.childCount) {
                        val viewInside = layout.getChildAt(j)
                        if (viewInside is EditText) {
                            val email = viewInside.text.toString().trim()
                            if (email.isNotEmpty()) {
                                correos.add(email)
                            }
                        }
                    }
                }
                val correoPrincipal = if (correos.isNotEmpty()) correos[0] else ""
                val correosAdicionales = if (correos.size > 1) correos.subList(1, correos.size) else emptyList()

                // ---------------- CREA OBJETO Contacto ----------------
                val contacto = if (contactoId != null && contactoId != 0L) {
                    // Si hay un ID, es una edición
                    Contacto(contactoId, nombre, correoPrincipal, numeroPrincipal, numerosAdicionales, correosAdicionales, direccion)
                } else {
                    // Si no hay ID, es un nuevo contacto
                    Contacto(null, nombre, correoPrincipal, numeroPrincipal, numerosAdicionales, correosAdicionales, direccion)
                }

                // ---------------- LLAMADA A API ----------------
                val api = Clients.instance(requireContext()).create(ContactoService::class.java)

                if (contactoId != null && contactoId != 0L) {
                    // Actualizar contacto existente
                    api.edit(contactoId!!, contacto).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Error", "Error en la API: ${t.message}", t)
                        }
                    })
                } else {
                    // Agregar nuevo contacto
                    api.add(contacto).enqueue(object : Callback<Contacto> {
                        override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Contacto agregado", Toast.LENGTH_SHORT).show()
                                Log.d("Contacto", response.body().toString())
                            } else {
                                Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<Contacto>, t: Throwable) {
                            Log.e("Error", "Error en la API: ${t.message}", t)
                        }
                    })
                }
            }


            binding.btnVerTareas.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()!!
                    .replace(R.id.fragment_container, TareasViewFragment()).commit()
            }

            binding.btnVerTodosContact.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()!!
                    .replace(R.id.fragment_container, ContactoFragment()).commit()
            }

        }



    }


