package ru.myitschool.work.data.source

sealed class ApiException(message: String) : Exception(message) {
    class NoConnection : ApiException("Нет соединения!")
    class Unauthorized : ApiException("Неверный логин или пароль")
    class Forbidden : ApiException("Недостаточно прав")
    class Conflict(message: String) : ApiException(message)
    class Server(message: String) : ApiException(message)
}
