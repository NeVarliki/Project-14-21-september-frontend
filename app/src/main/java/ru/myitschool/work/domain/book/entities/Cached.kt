package ru.myitschool.work.domain.book.entities

data class Cached<T>(
    val value: T,
    val fromCache: Boolean
)
