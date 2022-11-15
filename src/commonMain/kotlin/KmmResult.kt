/*
 * Copyright 2021 - 2022 A-SIT Plus GmbH. Obviously inspired and partially copy-pasted from kotlin.Result.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package at.asitplus.wallet.lib

/**
 * For easy use of this KMM library under iOS, we need a class like `Result`
 * that is not a `value` class (which is unsupported in Kotlin/Native)
 */
class KmmResult<T> {

    val error: Throwable?
    val value: T?

    constructor(value: T) {
        this.value = value
        this.error = null
    }

    constructor(error: Throwable) {
        this.value = null
        this.error = error
    }

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or `null`
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? {
        return value
    }

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or throws the encapsulated [Throwable] exception
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
     */
    fun getOrThrow(): T {
        if (value != null)
            return value
        throw error!!
    }

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = value != null

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value == null

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or the
     * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][isFailure].
     *
     * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
     *
     * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
     */
    inline fun getOrElse(onFailure: (exception: Throwable) -> T): T {
        @Suppress("UNCHECKED_CAST")
        return when (error) {
            null -> value as T
            else -> onFailure(error)
        }
    }


    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    inline fun exceptionOrNull(): Throwable? = if (isFailure) error else null


    /**
     * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result.isSuccess]
     * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
     *
     * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
     */
    inline fun <R> fold(
        onSuccess: (value: T) -> R,
        onFailure: (exception: Throwable) -> R
    ): R {
        return when (val exception = exceptionOrNull()) {
            null -> onSuccess(value!!)
            else -> onFailure(exception)
        }
    }

    /**
     * Returns a [Result] equivalent of this KmmResult
     */
    inline fun unwrap(): Result<T> = fold({ Result.success(it) }) { Result.failure(it) }

    companion object {
        fun <T> success(value: T): KmmResult<T> {
            return KmmResult(value)
        }

        fun <T> failure(error: Throwable): KmmResult<T> {
            return KmmResult(error)
        }
    }

}


/**
 * Returns a [KmmResult] equivalent of this Result
 */
inline fun <T> Result<T>.wrap(): KmmResult<T> = fold({ KmmResult.success(it) }) { KmmResult.failure(it) }