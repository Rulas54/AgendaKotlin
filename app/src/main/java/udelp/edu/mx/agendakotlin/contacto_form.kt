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

    private var _binding: FragmentContactoFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactoFormBinding.inflate(inflater, container, false)

        // Inicializamos el contenedor de teléfonos y el botón utilizando binding
        phoneNumbersContainer = binding.phoneNumbersContainer
        btnAddPhone = binding.btnAddPhone

        // Establecemos el listener para el botón "Agregar Teléfono"
        btnAddPhone.setOnClickListener {
            addPhoneField()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        var contacto : Contacto = Contacto(null,"Rulas","2001-10-04","rulas@gmail.com","3318715804","fotos/rulas.jpg","Animas 16")
        val api = Clients.instance(view.context).create(TareaService::class.java)



        binding.btnAgregarContact.setOnClickListener {
            val nombre : TextView = view.findViewById<TextView>(R.id.txtNombreContact)
            val nacimiento : TextView = view.findViewById<TextView>(R.id.txtFechaNacimientoContact)
            val correo : TextView = view.findViewById<TextView>(R.id.txtCorreoContact)
            val direccion : TextView = view.findViewById<TextView>(R.id.txtDireccionContact)
            //-----Telefono-----------------
            val telefonos = mutableListOf<String>()
            for (i in 0 until phoneNumbersContainer.childCount) {
                val layout = phoneNumbersContainer.getChildAt(i) as LinearLayout
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

            //------------------------------------------------------------------------------------
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            var contacto : Contacto = Contacto(null,nombre.text.toString(),nacimiento.text.toString(),correo.text.toString(),telefonos.joinToString(",")
                ,"https://example.com/default-profile.jpg",direccion.text.toString())
            val api = Clients.instance(view.context).create(ContactoService::class.java)

            api.add(contacto).enqueue(object : Callback<Contacto> {
                override fun onResponse(call: Call<Contacto>, response: Response<Contacto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context,"Contacto agregada",Toast.LENGTH_SHORT).show()
                        Log.d("Tarea", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<Contacto?>, t: Throwable) {
                    Log.e("Error", "Error en la API: ${t.message}", t)
                }
            })


        }

    }


}