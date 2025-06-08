// udelp.edu.mx.agendakotlin.service.EventoService.kt
package udelp.edu.mx.agendakotlin.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import udelp.edu.mx.agendakotlin.model.Evento

interface EventoService {

    @GET("eventos/")
    fun getAll(): Call<List<Evento>>

    @POST("eventos/get/{id}")
    fun get(@Path("id") id: Long): Call<Evento>

    @POST("eventos/add")
    fun add(@Body evento: Evento): Call<Evento>

    @POST("eventos/edit/{id}")
    fun edit(@Path("id") id: Long, @Body evento: Evento): Call<Evento>

    @POST("eventos/remove/{id}")
    fun delete(@Path("id") id: Long): Call<Void>

}