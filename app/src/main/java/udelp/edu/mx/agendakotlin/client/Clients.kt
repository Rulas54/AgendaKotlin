package udelp.edu.mx.agendakotlin.client

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import udelp.edu.mx.agendakotlin.util.Properties
import udelp.edu.mx.agendakotlin.service.EmailService


object Clients {
    fun instance(context: Context) : Retrofit{
        return Retrofit.Builder()
            .baseUrl(Properties.getProperty("api.url",context) )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getEmailService(context: Context): EmailService {
        return instance(context).create(EmailService::class.java)
    }
}