package udelp.edu.mx.agendakotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import udelp.edu.mx.agendakotlin.client.Clients
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.model.Usuario
import udelp.edu.mx.agendakotlin.service.TareaService
import udelp.edu.mx.agendakotlin.service.UsuarioService
import udelp.edu.mx.agendakotlin.ui.login.LoginActivity

class RegistrarseFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registrarse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = Clients.instance(view.context).create(UsuarioService::class.java)
        val nombre = view.findViewById<TextView>(R.id.nombre)
        val apellidoMaterno = view.findViewById<TextView>(R.id.apellidoMaterno)
        val apellidoPaterno = view.findViewById<TextView>(R.id.apellidoPaterno)
        val email = view.findViewById<TextView>(R.id.email)
        val password = view.findViewById<TextView>(R.id.passwd)

        val btn_aceptar = view.findViewById<Button>(R.id.btn_aceptar)
        btn_aceptar.setOnClickListener {

            val ususario = Usuario(null,nombre.text.toString(),apellidoMaterno.text.toString(), apellidoPaterno.text.toString()
                                    ,email.text.toString(),password.text.toString(),"","")

            api.addUsuario(ususario).enqueue(object : Callback<Usuario>{
                override fun onResponse(call: Call<Usuario?>, response: Response<Usuario?>) {

                    if (response.isSuccessful) {
                        AlertDialog.Builder(requireContext()).setTitle("Mensaje")
                            .setMessage("El usuario se registro exitosamente")
                            .setCancelable(false)
                            .setPositiveButton("Aceptar",
                                DialogInterface.OnClickListener { dialog, id ->
                                val intent = Intent(view.context, LoginActivity::class.java)
                                startActivity(intent)
                            }).create().show()
                    }
                }

                override fun onFailure(p0: Call<Usuario?>, p1: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

}