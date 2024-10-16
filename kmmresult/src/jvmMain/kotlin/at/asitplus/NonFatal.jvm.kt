package at.asitplus

import kotlin.coroutines.cancellation.CancellationException
//Taken from Arrow: https://github.com/arrow-kt/arrow/blob/99de6148320a4299a5aef20686a6063ca732026b/arrow-libs/core/arrow-core/src/jvmMain/kotlin/arrow/core/NonFatal.kt
@Suppress("NOTHING_TO_INLINE")
actual inline fun Throwable.nonFatalOrThrow(): Throwable = when (this) {
    is VirtualMachineError, is ThreadDeath, is InterruptedException, is LinkageError, is CancellationException -> throw this
    else -> this
}
