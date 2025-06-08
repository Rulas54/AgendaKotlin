package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Evento(
    @SerializedName("id") val id: Long?,
    @SerializedName("titulo") val titulo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("fechaInicio") val fechaInicio: String?,
    @SerializedName("fechaFin") val fechaFin: String?,
    @SerializedName("ubicacion") val ubicacion: String?,
    @SerializedName("participantes") val participantes: List<Contacto>?
)