/*
 * Copyright 2021 - 2022 A-SIT Plus GmbH. Obviously inspired and partially copy-pasted from kotlin.Result.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package at.asitplus

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.native.HiddenFromObjC

/**
 * For easy use of this KMM library under iOS, we need a class like `Result`
 * that is not a `value` class (which is unsupported in Kotlin/Native)
 */
class KmmResult<T> private constructor(private val delegate: Result<T>) {

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or `null`
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = delegate.getOrNull()

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or throws the encapsulated [Throwable] exception
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
     */
    fun getOrThrow(): T = delegate.getOrThrow()

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = delegate.isSuccess

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = delegate.isFailure

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or the
     * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][isFailure].
     *
     * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
     *
     * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
     */
    fun <R : T> getOrElse(onFailure: (exception: Throwable) -> R): T = delegate.getOrElse(onFailure)

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? = delegate.exceptionOrNull()

    /**
     * Transforms this KmmResult's success-case according to `block` and leaves the failure case untouched
     * (type erasure FTW!)
     */
    @Suppress("UNCHECKED_CAST")
    fun <R> map(block: (T) -> R): KmmResult<R> =
        if (delegate.isFailure) {
            this as KmmResult<R>
        } else {
            success(block(delegate.getOrThrow()))
        }

    /**
     * Transforms this KmmResult's failure-case according to `block` and leaves the success case untouched
     * (type erasure FTW!)
     */
    fun mapFailure(block: (Throwable) -> Throwable): KmmResult<T> =
        if (delegate.isFailure) {
            failure(block(delegate.exceptionOrNull()!!))
        } else {
            this
        }

    /**
     * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result.isSuccess]
     * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
     *
     * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
     */
    fun <R> fold(
        onSuccess: (value: T) -> R,
        onFailure: (exception: Throwable) -> R,
    ): R {
        return delegate.fold(
            onSuccess = { onSuccess(delegate.getOrThrow()) },
            onFailure = { onFailure(delegate.exceptionOrNull()!!) },
        )
    }

    /**
     * singular version of `fold`'s `onSuccess`
     */
    fun <R> onSuccess(block: (value: T) -> R) {
        delegate.getOrNull()?.let { block(it) }
    }

    /**
     * singular version of `fold`'s `onFailure`
     */
    fun <R> onFailure(block: (error: Throwable) -> R) {
        delegate.exceptionOrNull()?.let { block(it) }
    }

    /**
     * Returns a [Result] equivalent of this KmmResult
     */
    fun unwrap(): Result<T> = delegate

    @OptIn(ExperimentalObjCRefinement::class)
    companion object {
        @HiddenFromObjC
        @JvmStatic
        fun <T> success(value: T): KmmResult<T> = KmmResult(Result.success(value))

        @HiddenFromObjC
        @JvmName("failureInternal")
        fun <T> failure(error: Throwable): KmmResult<T> = KmmResult(Result.failure(error))

        @HiddenFromObjC
        @JvmStatic
        @JvmName("failure")
        fun <T> failureJvm(error: Throwable): KmmResult<T> = failure(error)

        /**
         * Returns a [KmmResult] equivalent of this Result
         */
        fun <T> Result<T>.wrap(): KmmResult<T> = KmmResult(this)
    }
}

// for swift convenience
fun <T> success(value: T): KmmResult<T> = KmmResult.success(value)

// for swift convenience
fun <T> failure(error: Throwable): KmmResult<T> = KmmResult.failure(error)
