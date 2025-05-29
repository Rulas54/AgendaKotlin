package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class  Tarea(
    @SerializedName("id") val id: Long?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("fechaVencimiento") val fechaVencimiento: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("etiqueta") val etiqueta: String?,
    @SerializedName("prioridad") val prioridad: Int?,

)
