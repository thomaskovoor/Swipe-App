package com.example.swipeapp.dataclass

/**
 * A generic class that holds a value with its loading status.
 * `Resource` is usually used in the repository layer for providing a
 * consistent way to handle data loading including success, error and loading states.
 *
 * @param T The type of the data.
 */
sealed class Resource<out T> {

    /**
     * A data class representing a successful state of the Resource.
     *
     * @param T The type of the data.
     * @param value The data that was loaded successfully.
     */
    data class Success<out T>(val value: T) : Resource<T>()

    /**
     * A data class representing a failure state of the Resource.
     *
     * @param isNetworkError A Boolean flag indicating whether the failure was due to a network error.
     * @param errorCode An optional error code, if one was provided.
     * @param errorBody A string providing more details about the error.
     */
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: String
    ) : Resource<Nothing>()

    /**
     * An object representing a loading state of the Resource.
     */
    object Loading : Resource<Nothing>()
}
