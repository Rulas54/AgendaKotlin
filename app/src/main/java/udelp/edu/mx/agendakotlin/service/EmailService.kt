package udelp.edu.mx.agendakotlin.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import udelp.edu.mx.agendakotlin.model.EmailRequest

interface EmailService {
    @POST("api/email/send")
    fun sendEmail(@Body emailRequest: EmailRequest): Call<Void>
}