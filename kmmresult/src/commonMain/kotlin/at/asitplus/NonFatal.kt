package at.asitplus

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Throws any fatal exceptions. This is a re-implementation taken from Arrow's
 * [`nonFatalOrThrow`](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html) –
 * not because it is bad, it is actually pretty much perfect.
 * However, the arrow dependency triggered an obscure IDEA bug, resulting in `NoClasDefFoundErrors` instead of correct
 * behaviour.  * We therefore removed the dependency and added the functionality directly to KmmResult.
 *
 * Please note that this was never a problem building anything that depended on KmmResult, it only made debugging
 * in IDEA a nightmare.
 */
expect inline fun Throwable.nonFatalOrThrow(): Throwable

/**
 * Helper to effectively convert stdlib's [runCatching] to behave like KmmResults Non-fatal-only [catching]. I.e. any
 * fatal exceptions are thrown.
 * The reason this exists is that [catching] incurs instantiation cost.
 * This helper hence provides the best of both worlds.
 */
inline fun <T> Result<T>.nonFatalOrThrow(): Result<T> = this.onFailure { it.nonFatalOrThrow() }


/**
 * Non-fatal-only-catching version of stdlib's [runCatching], returning a [Result] --
 * Re-throws any fatal exceptions, such as `OutOfMemoryError`. Relies on [Arrow](https://arrow-kt.io)'s
 * [nonFatalOrThrow](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html) logic.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> catchingUnwrapped(block: () -> T): Result<T> = runCatching (block).nonFatalOrThrow()

/**
 * Non-fatal-only-catching version of stdlib's [runCatching] (calling the specified function [block] with `this` value
 * as its receiver), directly returning a [Result] --
 * Re-throws any fatal exceptions, such as `OutOfMemoryError`. Relies on [Arrow](https://arrow-kt.io)'s
 * [nonFatalOrThrow](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html) logic.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T, R> T.catchingUnwrapped(block: T.() -> R): Result<R> = runCatching (block).nonFatalOrThrow()