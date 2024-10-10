package at.asitplus

import kotlin.coroutines.cancellation.CancellationException
//Taken from Arrow: https://github.com/arrow-kt/arrow/blob/99de6148320a4299a5aef20686a6063ca732026b/arrow-libs/core/arrow-core/src/nonJvmMain/kotlin/arrow/core/NonFatal.kt
actual inline fun Throwable.nonFatalOrThrow(): Throwable = when (this) {
    is CancellationException -> throw this
    else -> this
}
