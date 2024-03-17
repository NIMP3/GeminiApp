package dev.yovany.geminiapp.model

sealed class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    class Success<T>(data: T) : Resource<T>(Status.SUCCESS, data, null)
    class Error<T>(data: T?, message: String) : Resource<T>(Status.ERROR, data, message)
    class Loading<T>() : Resource<T>(Status.LOADING, null, null)
    class Empty<T>(data: T?) : Resource<T>(Status.EMPTY, data, null)
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING,
    EMPTY
}