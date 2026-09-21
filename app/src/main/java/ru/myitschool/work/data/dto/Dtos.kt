package ru.myitschool.work.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestDto(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
)

@Serializable
data class AuthResponseDto(
    @SerialName("access") val access: String,
    @SerialName("refresh") val refresh: String,
)

@Serializable
data class RefreshDto(
    @SerialName("refresh") val refresh: String,
)

@Serializable
data class InfoRequestDto(
    @SerialName("username") val username: String? = null,
)

@Serializable
data class UserDto(
    @SerialName("name") val name: String?,
    @SerialName("photoUrl") val photoUrl: String?,
    @SerialName("booking") val booking: Map<String, PlaceDto>?,
)

@Serializable
data class PlaceDto(
    @SerialName("id") val id: Long?,
    @SerialName("place") val place: String?,
)

@Serializable
data class BookRequestDto(
    @SerialName("date") val date: String,
    @SerialName("placeId") val placeId: Long,
)

@Serializable
data class RoomRequestDto(
    @SerialName("id") val id: Long,
)
