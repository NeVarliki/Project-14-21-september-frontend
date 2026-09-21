package ru.myitschool.work.data.source

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.myitschool.work.core.Constants
import ru.myitschool.work.data.dto.AuthRequestDto
import ru.myitschool.work.data.dto.AuthResponseDto
import ru.myitschool.work.data.dto.BookRequestDto
import ru.myitschool.work.data.dto.InfoRequestDto
import ru.myitschool.work.data.dto.PlaceDto
import ru.myitschool.work.data.dto.RefreshDto
import ru.myitschool.work.data.dto.RoomRequestDto
import ru.myitschool.work.data.dto.UserDto
import java.io.IOException

object NetworkDataSource {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = true
        encodeDefaults = true
    }

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                connectTimeoutMillis = 5_000
                requestTimeoutMillis = 10_000
            }
        }
    }

    suspend fun login(username: String, password: String): AuthResponseDto = call {
        client.post(url("/login")) {
            contentType(ContentType.Application.Json)
            setBody(AuthRequestDto(username, password))
        }
    }.body()

    suspend fun refresh(refresh: String): AuthResponseDto = call {
        client.post(url("/refresh")) {
            contentType(ContentType.Application.Json)
            setBody(RefreshDto(refresh))
        }
    }.body()

    suspend fun getInfo(token: String): UserDto = call {
        client.get(url("/info")) {
            auth(token)
            contentType(ContentType.Application.Json)
            setBody(InfoRequestDto())
        }
    }.body()

    suspend fun getBooking(token: String): Map<String, List<PlaceDto>> = call {
        client.get(url("/booking")) { auth(token) }
    }.body()

    suspend fun book(token: String, date: String, placeId: Long) {
        call {
            client.post(url("/book")) {
                auth(token)
                contentType(ContentType.Application.Json)
                setBody(BookRequestDto(date, placeId))
            }
        }
    }

    suspend fun bookRoomToday(token: String, placeId: Long) {
        call {
            client.post(url("/room")) {
                auth(token)
                contentType(ContentType.Application.Json)
                setBody(RoomRequestDto(placeId))
            }
        }
    }

    suspend fun free(token: String) {
        call {
            client.post(url("/free")) { auth(token) }
        }
    }

    private fun HttpRequestBuilder.auth(token: String) = bearerAuth(token)

    private fun url(path: String) = "${Constants.HOST}/api$path"

    private suspend fun call(block: suspend () -> HttpResponse): HttpResponse = withContext(Dispatchers.IO) {
        val response = try {
            block()
        } catch (e: IOException) {
            throw ApiException.NoConnection()
        } catch (e: Exception) {
            if (e is ApiException) throw e
            throw ApiException.NoConnection()
        }
        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.NoContent -> response
            HttpStatusCode.Unauthorized -> throw ApiException.Unauthorized()
            HttpStatusCode.Forbidden -> throw ApiException.Forbidden()
            HttpStatusCode.Conflict -> throw ApiException.Conflict(response.bodyAsText().ifBlank { "Уже занято" })
            else -> throw ApiException.Server(response.bodyAsText().ifBlank { "Ошибка ${response.status.value}" })
        }
    }
}
