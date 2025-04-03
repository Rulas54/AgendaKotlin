package udelp.edu.mx.agendakotlin.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import udelp.edu.mx.agendakotlin.model.Tarea
import udelp.edu.mx.agendakotlin.model.Usuario

interface UsuarioService {

    @POST("usuario/")
    fun getAllUsuarios(): Call<List<Usuario>>

    @POST("usuario/add")
    fun addUsuario(@Body usuario: Usuario): Call<Usuario>

    @POST("usuario/get/{id}")
    fun getUsuario(@Path("id") id: String): Call<Usuario>

    @POST("usuario/recuperar-password/{id}")
    fun recuperarPassword(@Path("id") id: Long, @Body usuario: Usuario): Call<Usuario>

    @POST("usuario/login")
    fun login(@Body usuario: Usuario): Call<Usuario>



}