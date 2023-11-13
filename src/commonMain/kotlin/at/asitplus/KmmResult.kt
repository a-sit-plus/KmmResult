/*
 * Copyright 2021 - 2023 A-SIT Plus GmbH. Obviously inspired and partially copy-pasted from kotlin.Result.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */
@file:Suppress("TooManyFunctions")

package at.asitplus

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.jvm.JvmStatic
import kotlin.native.HiddenFromObjC

/**
 * For easy use under iOS, we need a class like `Result`
 * that is not a `value` class (which is unsupported in Kotlin/Native)
 */
class KmmResult<T> private constructor(
    private val delegate: Result<T>,
    @Suppress("UNUSED_PARAMETER") unusedButPreventsSignatureClashes: Boolean
) {

    /**
     * Creates a success result from the given [value]
     */
    constructor(value: T) : this(Result.success(value), false)

    /**
     * Creates a failure result from the given [failure]
     */
    constructor(failure: Throwable) : this(Result.failure(failure), false)

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or `null`
     * if it is [failure][isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = delegate.getOrNull()

    /**
     * Returns the encapsulated value if this instance represents [success][isSuccess] or throws the
     * encapsulated [Throwable] exception if it is [failure][isFailure].
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
    inline fun getOrElse(onFailure: (exception: Throwable) -> T): T =
        if (isSuccess) getOrThrow() else onFailure(exceptionOrNull()!!)

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
    inline fun <R> map(block: (T) -> R): KmmResult<R> =
        getOrNull()?.let { success(block(it)) } ?: this as KmmResult<R>

    /**
     * Transforms this KmmResult into a KmmResult of different success type according to `block` and leaves the failure case untouched
     * Avoids nested KmmResults
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <R> transform(block: (T) -> KmmResult<R>): KmmResult<R> =
        getOrNull()?.let { block(it) } ?: this as KmmResult<R>


    /**
     * Returns the encapsulated result of the given [block] function applied to the encapsulated value
     * if this instance represents [success][KmmResult.isSuccess] or the
     * original encapsulated [Throwable] exception if it is [failure][KmmResult.isFailure].
     *
     * This function catches any [Throwable] exception thrown by [block] function and encapsulates it as a failure.
     * See [map] for an alternative that rethrows exceptions from `transform` function.
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <R> mapCatching(block: (T) -> R): KmmResult<R> = unwrap().mapCatching { block(it) }.wrap()


    /**
     * Transforms this KmmResult's failure-case according to `block` and leaves the success case untouched
     * (type erasure FTW!)
     */
    inline fun mapFailure(block: (Throwable) -> Throwable): KmmResult<T> =
        exceptionOrNull()?.let { KmmResult(block(it)) } ?: this

    /**
     * Returns the result of [onSuccess] for the encapsulated value if this instance represents
     * [success][Result.isSuccess] or the result of [onFailure] function for the encapsulated [Throwable] exception if
     * it is [failure][Result.isFailure].
     *
     * Note: this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
     */
    inline fun <R> fold(
        onSuccess: (value: T) -> R,
        onFailure: (exception: Throwable) -> R,
    ): R = if (isSuccess) onSuccess(getOrThrow())
    else onFailure(exceptionOrNull()!!)

    /**
     * singular version of `fold`'s `onSuccess`
     */
    inline fun <R> onSuccess(block: (value: T) -> R) {
        getOrNull()?.let { block(it) }
    }

    /**
     * singular version of `fold`'s `onFailure`
     */
    inline fun <R> onFailure(block: (error: Throwable) -> R) {
        exceptionOrNull()?.let { block(it) }
    }

    /**
     * Returns a [Result] equivalent of this KmmResult
     */
    fun unwrap(): Result<T> = delegate

    override fun toString() = "KmmResult" + if (isSuccess) {
        ".success" +
            runCatching { "<" + delegate.getOrThrow()!!::class.simpleName + ">" }.getOrElse { "" } +
            "(${getOrThrow()})"
    } else {
        val exName = exceptionOrNull()?.let { runCatching { it::class.simpleName }.getOrNull() }
        ".failure" + (exName?.let { "($it" }) + exceptionOrNull()?.let { err -> err.message?.let { "($it)" } ?: "" } +
            exName?.let { ")" }
    }

    @OptIn(ExperimentalObjCRefinement::class)
    companion object {
        @HiddenFromObjC
        @JvmStatic
        fun <T> success(value: T): KmmResult<T> = KmmResult(value)

        @HiddenFromObjC
        @JvmStatic
        fun <T> failure(error: Throwable): KmmResult<T> = KmmResult(error)

        /**
         * Returns a [KmmResult] equivalent of this Result
         */
        fun <T> Result<T>.wrap(): KmmResult<T> = KmmResult(this, false)
    }
}
