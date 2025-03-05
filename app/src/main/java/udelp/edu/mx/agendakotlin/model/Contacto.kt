package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Contacto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("foto") val foto: String,
    @SerializedName("direccion") val data: String,
)
