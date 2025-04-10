package udelp.edu.mx.agendakotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import udelp.edu.mx.agendakotlin.model.Usuario
import udelp.edu.mx.agendakotlin.service.UsuarioService
import udelp.edu.mx.agendakotlin.ui.login.LoginActivity


class OlvideConstrasenaFragment : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_olvide_constrasena, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = Clients.instance(view.context).create(UsuarioService::class.java)
        val correo = view.findViewById<TextView>(R.id.txt_email)
        val newpasswdText = view.findViewById<TextView>(R.id.txt_newpasswd)
        val passwdText = view.findViewById<TextView>(R.id.txt_apply_passwd)



        val btn_aceptar = view.findViewById<Button>(R.id.button_aceptar_passwd)


        btn_aceptar.setOnClickListener {

            val newpasswd = newpasswdText.text.toString()
            val confirmarPasswd = passwdText.text.toString()


            if(newpasswd.isEmpty() && confirmarPasswd.isEmpty()){
                AlertDialog.Builder(requireContext()).setTitle("Contraseña")
                    .setMessage("El campo de las contraseñas no pueden ir vacios")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            val intent = Intent(view.context, LoginActivity::class.java)
                            startActivity(intent)
                        }).create().show()
            } else if (newpasswd == confirmarPasswd) {
                val usuario = Usuario(
                    null, null, null, null, correo.text.toString(), confirmarPasswd, "", ""
                )

                api.recuperarPassword(usuario).enqueue(object : Callback<Usuario>{
                    override fun onResponse(call: Call<Usuario?>, response: Response<Usuario>) {
                        if (response.isSuccessful && null != response.body()) {

                            val usuarioResponse = response.body()
                            var mensaje ="La contraseña ha sido cambiada exitosamente"

                            if(usuarioResponse?.code != "200"){

                                mensaje = usuarioResponse?.mensaje.toString()

                            }

                            AlertDialog.Builder(requireContext()).setTitle("Contraseña")
                                .setMessage(mensaje)
                                .setCancelable(false)
                                .setPositiveButton("Aceptar",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        if(usuarioResponse?.code == "200"){
                                            val intent = Intent(view.context, LoginActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }).create().show()
                        }

                    }
                    override fun onFailure(call: Call<Usuario?>, t: Throwable) {

                    }



                })

            } else {

                AlertDialog.Builder(requireContext()).setTitle("Constraseña")
                    .setMessage("Las contraseñas no son iguales")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            val intent = Intent(view.context, LoginActivity::class.java)
                            startActivity(intent)
                        }).create().show()

            }
        }
    }
}

