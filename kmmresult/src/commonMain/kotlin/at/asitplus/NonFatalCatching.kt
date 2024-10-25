@file:OptIn(kotlin.contracts.ExperimentalContracts::class)
package at.asitplus

import at.asitplus.KmmResult.Companion.wrap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.internal.LowPriorityInOverloadResolution

/**
 * Throws any fatal exceptions. This is a re-implementation taken from Arrow's
 * [`nonFatalOrThrow`](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html) –
 * to avoid a dependency on Arrow for a single function.
 */
@Suppress("NOTHING_TO_INLINE")
expect inline fun Throwable.nonFatalOrThrow(): Throwable

/**
 * Helper to effectively convert stdlib's [runCatching] to behave like KmmResult's Non-fatal-only [catching]. I.e. any
 * fatal exceptions are thrown.
 * The reason this exists is that [catching] incurs instantiation cost.
 * This helper hence provides the best of both worlds.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> Result<T>.nonFatalOrThrow(): Result<T> = this.onFailure { it.nonFatalOrThrow() }

/**
 * Non-fatal-only-catching version of stdlib's [runCatching], returning a [Result] --
 * Re-throws any fatal exceptions, such as `OutOfMemoryError`. Re-implements [Arrow](https://arrow-kt.io)'s
 * [nonFatalOrThrow](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html)
 * logic to avoid a dependency on Arrow for a single function.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> catchingUnwrapped(block: () -> T): Result<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.nonFatalOrThrow())
    }
}

/** @see catchingUnwrapped */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, R> T.catchingUnwrapped(block: T.() -> R): Result<R> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.nonFatalOrThrow())
    }
}

/**
 * Non-fatal-only-catching version of stdlib's [runCatching], directly returning a [KmmResult] --
 * Re-throws any fatal exceptions, such as `OutOfMemoryError`. Re-implements [Arrow](https://arrow-kt.io)'s
 * [nonFatalOrThrow](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html)
 * logic to avoid a dependency on Arrow for a single function.
 */
inline fun <T> catching(block: () -> T) =
    catchingUnwrapped(block).wrap()

/** @see catching */
inline fun <T, R> T.catching(block: T.() -> R) =
    catchingUnwrapped(block).wrap()

/**
 * If the underlying [Result] is successful, returns it unchanged.
 * If it is failed, and the contained exception is of the specified type, returns it unchanged.
 * Otherwise, wraps the contained exception in the specified type.
 *
 * Usage: `Result.wrapAs(a = ::ThrowableType)`
 */
inline fun <reified E: Throwable, R> Result<R>.wrapAs
            (a: (String?, Throwable) -> E): Result<R>
{
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
    }
    return exceptionOrNull().let { x ->
        if ((x == null) || (x is E)) this@wrapAs
        else Result.failure(a(x.message, x))
    }
}

/** @see wrapAs */
@LowPriorityInOverloadResolution
inline fun <reified E: Throwable, R> Result<R>.wrapAs
            (a: (Throwable) -> E) =
    wrapAs(a={ _,x -> a(x) })

/**
 * Runs the specified function [block], returning a [Result].
 * Any non-fatal exception will be wrapped as the specified exception, unless it is already the specified type.
 *
 * Usage: `catchingUnwrappedAs(type = ::ThrowableType) { block }`.
 */
inline fun <reified E : Throwable, R> catchingUnwrappedAs
            (a: (String?, Throwable) -> E, block: () -> R) =
    catchingUnwrapped(block).wrapAs(a)

/** @see catchingUnwrappedAs */
inline fun <reified E : Throwable, T, R> T.catchingUnwrappedAs
            (a: (String?, Throwable) -> E, block: T.() -> R) =
    catchingUnwrapped(block).wrapAs(a)

/** @see catchingUnwrappedAs */
@LowPriorityInOverloadResolution
inline fun <reified E : Throwable, R> catchingUnwrappedAs
            (a: (Throwable) -> E, block: () -> R ) =
    catchingUnwrapped(block).wrapAs(a)

/** @see catchingUnwrappedAs */
@LowPriorityInOverloadResolution
inline fun <reified E : Throwable, T, R> T.catchingUnwrappedAs
            (a: (Throwable) -> E, block: T.() -> R) =
    this.catchingUnwrapped(block).wrapAs(a)

/**
 * Runs the specified function [block], returning a [KmmResult].
 * Any non-fatal exception will be wrapped as the specified exception, unless it is already the specified type.
 *
 * Usage: `catchingAs(a = ::ThrowableType) { block }`.
 */
inline fun <reified E : Throwable, R> catchingAs
            (a: (String?, Throwable) -> E, block: () -> R) =
    catchingUnwrappedAs(a, block).wrap()

/** @see catchingAs */
inline fun <reified E : Throwable, T, R> T.catchingAs
            (a: (String?, Throwable) -> E, block: T.() -> R) =
    catchingUnwrappedAs(a, block).wrap()

/** @see catchingAs */
@LowPriorityInOverloadResolution
inline fun <reified E : Throwable, R> catchingAs
            (a: (Throwable) -> E, block: () -> R) =
    catchingUnwrappedAs(a, block).wrap()

@LowPriorityInOverloadResolution
inline fun <reified E: Throwable, T, R> T.catchingAs
            (a: (Throwable) -> E, block: T.() -> R) =
    catchingUnwrappedAs(a, block).wrap()

@Deprecated("Function name was misleading", ReplaceWith("catchingAs(asA, block)"))
inline fun <reified E : Throwable, R> wrapping
            (asA: (String?, Throwable) -> E, block: () -> R): KmmResult<R> =
    catchingAs(asA, block)

@Deprecated("Function name was misleading", ReplaceWith("catchingAs(asA, block)"))
inline fun <reified E : Throwable, T, R> T.wrapping
            (asA: (String?, Throwable) -> E, block: T.() -> R): KmmResult<R> =
    catchingAs(asA, block)
