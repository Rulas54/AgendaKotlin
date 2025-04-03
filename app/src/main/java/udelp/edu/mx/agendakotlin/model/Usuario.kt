package udelp.edu.mx.agendakotlin.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("apellidoMaterno") val apellidoMaterno: String?,
    @SerializedName("apellidoPaterno") val apellidoPaterno: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("code") val code: String?,
    @SerializedName("mensaje") val mensaje: String?,
)
