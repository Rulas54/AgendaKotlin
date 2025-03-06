package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName


data class Contacto(
    @SerializedName("id") val id: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("numeroTelefono") val numeroTelfono: String?,
    @SerializedName("foto") val foto: String?,
    @SerializedName("direccion") val direccion: String?,
)
