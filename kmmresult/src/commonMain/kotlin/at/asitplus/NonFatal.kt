package at.asitplus

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
inline fun <T> catchingUnwrapped(block: () -> T): Result<T> = runCatching(block).nonFatalOrThrow()

/**
 * Non-fatal-only-catching version of stdlib's [runCatching] (calling the specified function [block] with `this` value
 * as its receiver), directly returning a [Result] --
 * Re-throws any fatal exceptions, such as `OutOfMemoryError`. Re-implements [Arrow](https://arrow-kt.io)'s
 * [nonFatalOrThrow](https://apidocs.arrow-kt.io/arrow-core/arrow.core/non-fatal-or-throw.html)
 * logic to avoid a dependency on Arrow for a single function.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, R> T.catchingUnwrapped(block: T.() -> R): Result<R> = runCatching(block).nonFatalOrThrow()
