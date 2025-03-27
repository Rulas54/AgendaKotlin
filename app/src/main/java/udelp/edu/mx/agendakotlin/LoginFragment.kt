package udelp.edu.mx.agendakotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import udelp.edu.mx.agendakotlin.databinding.FragmentLoginBinding


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

        binding.login.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Algo")
                .setMessage("Algo")
                .setCancelable(false)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(this.context, MainActivity::class.java)
                    startActivity(intent)
                }).create().show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}