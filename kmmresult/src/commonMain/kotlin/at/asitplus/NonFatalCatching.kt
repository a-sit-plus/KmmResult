@file:OptIn(kotlin.contracts.ExperimentalContracts::class)
@file:Suppress("TooManyFunctions", "TooGenericExceptionCaught", "MultiLineIfElse")

package at.asitplus

import at.asitplus.KmmResult.Companion.wrap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.nonFatalOrThrow())
    }
}

/** @see catchingUnwrapped */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, R> T.catchingUnwrapped(block: T.() -> R): Result<R> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
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
inline fun <T> catching(block: () -> T): KmmResult<T> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return catchingUnwrapped(block).wrap()
}

/** @see catching */
inline fun <T, R> R.catching(block: R.() -> T): KmmResult<T> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return catchingUnwrapped(block).wrap()
}

/**
 * If the underlying [Result] is successful, returns it unchanged.
 * If it failed, and the contained exception is of the specified type, returns it unchanged.
 * Otherwise, wraps the contained exception in the specified type.
 *
 * Usage: `Result.wrapAs(a = ::ThrowableType)`
 */
inline fun <reified E : Throwable, T> Result<T>.wrapAs(a: (String?, Throwable) -> E): Result<T> {
    contract { callsInPlace(a, InvocationKind.AT_MOST_ONCE) }
    return exceptionOrNull().let { x ->
        if ((x == null) || (x is E)) this@wrapAs
        else Result.failure(a(x.message, x))
    }
}

/** @see wrapAs */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
inline fun <reified E : Throwable, R> Result<R>.wrapAs(a: (Throwable) -> E): Result<R> {
    contract { callsInPlace(a, InvocationKind.AT_MOST_ONCE) }
    return wrapAs(a = { _, x -> a(x) })
}

/**
 * Runs the specified function [block], returning a [Result].
 * Any non-fatal exception will be wrapped as the specified exception, unless it is already the specified type.
 *
 * Usage: `catchingUnwrappedAs(type = ::ThrowableType) { block }`.
 */
inline fun <reified E : Throwable, T> catchingUnwrappedAs(a: (String?, Throwable) -> E, block: () -> T): Result<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrapped(block).wrapAs(a)
}

/** @see catchingUnwrappedAs */
inline fun <reified E : Throwable, T, R> R.catchingUnwrappedAs(
    a: (String?, Throwable) -> E,
    block: R.() -> T
): Result<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrapped(block).wrapAs(a)
}

/** @see catchingUnwrappedAs */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
inline fun <reified E : Throwable, T> catchingUnwrappedAs(a: (Throwable) -> E, block: () -> T): Result<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrapped(block).wrapAs(a)
}

/** @see catchingUnwrappedAs */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
inline fun <reified E : Throwable, T, R> R.catchingUnwrappedAs(a: (Throwable) -> E, block: R.() -> T): Result<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return this.catchingUnwrapped(block).wrapAs(a)
}

/**
 * Runs the specified function [block], returning a [KmmResult].
 * Any non-fatal exception will be wrapped as the specified exception, unless it is already the specified type.
 *
 * Usage: `catchingAs(a = ::ThrowableType) { block }`.
 */
inline fun <reified E : Throwable, T> catchingAs(a: (String?, Throwable) -> E, block: () -> T): KmmResult<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrappedAs(a, block).wrap()
}

/** @see catchingAs */
inline fun <reified E : Throwable, T, R> R.catchingAs(a: (String?, Throwable) -> E, block: R.() -> T): KmmResult<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrappedAs(a, block).wrap()
}

/** @see catchingAs */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
inline fun <reified E : Throwable, T> catchingAs(a: (Throwable) -> E, block: () -> T): KmmResult<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrappedAs(a, block).wrap()
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
inline fun <reified E : Throwable, T, R> R.catchingAs(a: (Throwable) -> E, block: R.() -> T): KmmResult<T> {
    contract {
        callsInPlace(a, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingUnwrappedAs(a, block).wrap()
}

@Deprecated("Function name was misleading", ReplaceWith("catchingAs(asA, block)"))
inline fun <reified E : Throwable, R> wrapping(asA: (String?, Throwable) -> E, block: () -> R): KmmResult<R> {
    contract {
        callsInPlace(asA, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingAs(asA, block)
}

@Deprecated("Function name was misleading", ReplaceWith("catchingAs(asA, block)"))
inline fun <reified E : Throwable, T, R> R.wrapping(asA: (String?, Throwable) -> E, block: R.() -> T): KmmResult<T> {
    contract {
        callsInPlace(asA, InvocationKind.AT_MOST_ONCE)
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return catchingAs(asA, block)
}
