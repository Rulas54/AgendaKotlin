package udelp.edu.mx.agendakotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.databinding.FragmentLoginBinding
import udelp.edu.mx.agendakotlin.model.Usuario
import udelp.edu.mx.agendakotlin.service.UsuarioService
import udelp.edu.mx.agendakotlin.ui.login.LoginActivity


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
        private var _binding: FragmentLoginBinding? = null
        private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegistrarseFragment)
        }

        binding.btnPasswd.setOnClickListener {
            findNavController().navigate(R.id.action_LogFragment_to_olvideFragment)
        }

        val api = Clients.instance(view.context).create(UsuarioService::class.java)
        val emailText = view.findViewById<TextView>(R.id.username)
        val passwordText = view.findViewById<TextView>(R.id.password)

        val email = emailText.text.toString()
        val password = passwordText.text.toString()


        binding.login.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()


            if(email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder(requireContext()).setTitle("Inicio de sesión")
                    .setMessage("No puede haber campos vacios")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        val intent = Intent(this.context, LoginActivity::class.java)
                        startActivity(intent)
                    }).create().show()
            }else {
                val usuario = Usuario(
                    null, null, null, null, email, password, "", ""
                )
                api.login(usuario).enqueue(object : Callback<Usuario> {
                    override fun onResponse(call: Call<Usuario?>, response: Response<Usuario?>) {
                        if (response.isSuccessful && null != response.body()) {
                            if (response.isSuccessful && null != response.body()) {

                                val usuarioResponse = response.body()
                                var mensaje ="Redirigiendo al menu"

                                if(usuarioResponse?.code != "200"){

                                    mensaje = usuarioResponse?.mensaje.toString()

                                }

                                AlertDialog.Builder(requireContext()).setTitle("Inicio de sesión")
                                    .setMessage(mensaje)
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            if(usuarioResponse?.code == "200"){
                                                val intent = Intent(view.context, MainActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }).create().show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<Usuario?>, t: Throwable) {

                    }
                })
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}