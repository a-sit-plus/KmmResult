/*
 * Copyright 2021 - 2022 A-SIT Plus GmbH. Obviously inspired and partially copy-pasted from kotlin.Result.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package at.asitplus

inline fun <T> KmmResult(value: T) = KmmResult.Success(value)

inline fun KmmResult(error: Throwable) = KmmResult.Failure(error)

/**
 * For easy use of this KMM library under iOS, we need a class like `Result`
 * that is not a `value` class (which is unsupported in Kotlin/Native)
 */
sealed class KmmResult<out T> {
    data class Success<T>(val value: T) : KmmResult<T>()

    data class Failure(val error: Throwable) : KmmResult<Nothing>()


    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or `null`
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or throws the encapsulated [Throwable] exception
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
     */
    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw error
    }

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean inline get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean inline get() = this is Failure

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or the
     * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][isFailure].
     *
     * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
     *
     * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
     */
    inline fun <R : @UnsafeVariance T> getOrElse(onFailure: (exception: Throwable) -> R): T {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is Success -> value
            is Failure -> onFailure(error)
        }
    }


    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    inline fun exceptionOrNull(): Throwable? = when (this) {
        is Failure -> error
        is Success -> null
    }


    /**
     * Transforms this KmmResult's success-case according to `block` and leaves the failure case untouched
     * (type erasure FTW!)
     */
    inline fun <R> map(block: (T) -> R): KmmResult<R> =
        when (this) {
            is Failure -> this
            is Success -> Success(block(value))
        }


    /**
     * Transforms this KmmResult's failure-case according to `block` and leaves the success case untouched
     * (type erasure FTW!)
     */
    inline fun mapFailure(block: (Throwable) -> Throwable): KmmResult<T> =
        when (this) {
            is Failure -> Failure(block(error))
            is Success -> this
        }


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
        return when (this) {
            is Success -> onSuccess(value)
            is Failure -> onFailure(error)
        }
    }

    /**
     * Returns a [Result] equivalent of this KmmResult
     */
    inline fun unwrap(): Result<T> = fold({ Result.success(it) }) { Result.failure(it) }

    companion object {
        fun <T> success(value: T): Success<T> {
            return KmmResult(value)
        }

        fun failure(error: Throwable): Failure {
            return KmmResult(error)
        }
    }

}


/**
 * Returns a [KmmResult] equivalent of this Result
 */
inline fun <T> Result<T>.wrap(): KmmResult<T> = fold({ KmmResult.success(it) }) { KmmResult.failure(it) }