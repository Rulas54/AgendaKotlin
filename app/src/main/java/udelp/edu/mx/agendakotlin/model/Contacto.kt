package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName


data class Contacto(
    @SerializedName("id") val id: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("numeroTelefono") val numeroTelfono: String?,
    @SerializedName("numeroAdicional") val numeroAdicional: List<String>?,
    @SerializedName("correoAdicional") val correoAdicional: List<String>?,
    @SerializedName("direccion") val direccion: String?,
)
