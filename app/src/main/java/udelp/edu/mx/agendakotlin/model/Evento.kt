package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class Evento(
    val id: Long?,
    val titulo: String?,
    val descripcion: String?,
    val fechaInicio: String?,
    val fechaFin: String?,
    val ubicacion: String?,
    val participantes: List<Contacto>?
) : Serializable