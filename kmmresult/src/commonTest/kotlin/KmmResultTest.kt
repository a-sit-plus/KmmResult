package at.asitplus

import at.asitplus.KmmResult.Companion.success
import at.asitplus.KmmResult.Companion.wrap
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.*

class KmmResultTest {

    @Test
    fun testCatching() {
        assertFailsWith(CancellationException::class) {
            catching { throw CancellationException("just a test", null) }
        }

        assertFailsWith(CancellationException::class) {
            runCatching { throw CancellationException("just a test", null) }.wrap()
        }

        assertFailsWith(CancellationException::class) {
            KmmResult.failure<Unit>(CancellationException("just a test", null))
        }
        assertFailsWith(CancellationException::class) {
            Result.failure<Unit>(CancellationException("just a test", null)).wrap()
        }

        runCatching { throw CancellationException("just a test", null) }
        Result.failure<Unit>(CancellationException("just a test", null))
        catching { throw NullPointerException("just a test") }
        runCatching { throw NullPointerException("just a test") }.wrap()
        KmmResult.failure<Unit>(NullPointerException("just a test"))
        Result.failure<Unit>(NullPointerException("just a test")).wrap()

    }

    @Test
    fun testMap() {
        assertEquals("1234", KmmResult.success(1234).map { it.toString() }.getOrThrow())
        val throwable = NullPointerException("Null")
        val fail: KmmResult<Int> = KmmResult.failure(throwable)
        assertEquals(fail, fail.map { it * 3 })
        assertEquals(throwable, fail.map { it * 3 }.exceptionOrNull())

        assertEquals(9, KmmResult.success(3).map { it * 3 }.getOrThrow())
    }

    @Test
    fun testTransform() {
        val intResult = success(1234)
        val stringResult = success("1234")
        assertEquals(stringResult, intResult.transform { success(it.toString()) })
        val throwable = NullPointerException("Null")
        val fail: KmmResult<Int> = KmmResult.failure(throwable)
        assertEquals(fail, fail.transform { success( it * 3 ) })
    }

    @Test
    fun testMapFailure() {
        assertTrue(
            KmmResult.failure<Int>(NullPointerException()).mapFailure {
                assertTrue(it is NullPointerException)
                IllegalStateException(it)
            }.exceptionOrNull() is IllegalStateException,
        )

        val success = KmmResult.success(3)
        assertEquals(success, success.mapFailure { it })
    }



    @Test
    fun testDestructuredMapSuccess() {
        val fail: KmmResult<Int> = KmmResult.failure(NullPointerException("Null"))
        fail.onSuccess { throw IllegalStateException("this must never happen") }
        fail.onFailure { assertTrue(it is NullPointerException) }
    }

    @Test
    fun testDestructuredMapFailure() {
        val success = KmmResult.success(3)
        success.onFailure { throw IllegalStateException("this must never happen") }
        success.onSuccess { assertEquals(3, it) }
    }

    @Test
    fun testGetOrElse() {
        val result: KmmResult<Int> = runCatching { throw NullPointerException("NULL") }.wrap()
        assertEquals(3, result.getOrElse { 3 })
        assertEquals(9, (KmmResult.failure<Int>(NullPointerException("NULL"))).getOrElse { 9 })
        assertEquals(3, KmmResult.success(3).getOrElse { 9 })
    }

    @Test
    fun testGetOrNull() {
        assertNull(KmmResult.failure<Int>(NullPointerException()).getOrNull())
        assertEquals(3, KmmResult.success(3).getOrNull())
    }

    @Test
    fun testIsFailure() {
        assertTrue(KmmResult.failure<Int>(NullPointerException()).isFailure)
        assertFalse(KmmResult.failure<Int>(NullPointerException()).isSuccess)
    }

    @Test
    fun testIsSuccess() {
        assertTrue(KmmResult.success(4).isSuccess)
        assertFalse(KmmResult.success(4).isFailure)
    }

    @Test
    fun testFold() {
        assertEquals(
            9,
            KmmResult.success(3).fold(
                onSuccess = { it * 3 },
                onFailure = { throw NullPointerException() },
            ),
        )
        assertEquals(
            9,
            KmmResult.failure<Int>(IllegalStateException()).fold(
                onSuccess = { it * 42 },
                onFailure = { 9 },
            ),
        )
    }

    @Test
    fun testResultWrapping() {
        val s = Result.success(3)
        assertEquals(s, s.wrap().unwrap())

        val f = Result.failure<Int>(NullPointerException())
        assertEquals(f, f.wrap().unwrap())
    }

    @Test
    fun testWrapping() {
        class CustomException(message: String? = null, cause: Throwable? = null) : Throwable(message, cause)
        wrapping(asA = ::CustomException) {
            throw RuntimeException("foo")
        }.let {
            val ex = it.exceptionOrNull()
            assertNotNull(ex)
            assertIs<CustomException>(ex)
            assertIs<RuntimeException>(ex.cause)
            assertEquals(ex.message, "foo")
        }
        wrapping(asA = ::CustomException) {
            throw CustomException("bar")
        }.let {
            val ex = it.exceptionOrNull()
            assertNotNull(ex)
            assertIs<CustomException>(ex)
            assertNull(ex.cause)
            assertEquals(ex.message, "bar")
        }
    }

    @Test
    fun testToString() {
        assertEquals("KmmResult.success(null)", KmmResult.success(null).toString())
        assertEquals("KmmResult.success<Int>(3)", KmmResult.success(3).toString())
        assertEquals("KmmResult.success(null)", KmmResult.success<Int?>(null).toString())
        assertEquals(
            "KmmResult.failure(NullPointerException)",
            KmmResult.failure<Int>(NullPointerException()).toString()
        )

        assertEquals(
            "KmmResult.failure(NullPointerException())",
            KmmResult.failure<Int>(NullPointerException("")).toString()
        )

        assertEquals(
            "KmmResult.failure(NullPointerException(null))",
            KmmResult.failure<Int>(NullPointerException("null")).toString()
        )

        assertEquals(
            "KmmResult.failure(NullPointerException(foo))",
            KmmResult.failure<Int>(NullPointerException("foo")).toString()
        )

    }

    @Test
    fun testNullability() {
        val result = KmmResult.success<Unit?>(null)
        assertNull(result.getOrThrow())
        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)

        fun assertCalled(block: ((Unit?)->Unit)->Unit) {
            var hit = false
            block { assertNull(it); hit = true }
            if (!hit) asserter.fail("Expected function was not called")
        }

        fun assertUnreachable(): Nothing {
            asserter.fail("Unreachable code is reachable")
        }

        assertCalled { fn -> result.onSuccess(fn) }

        assertCalled { fn ->
            result.fold(
                onSuccess = fn,
                onFailure = { assertUnreachable() }
            )
        }

        assertCalled { fn ->
            result.transform { catching { fn(it) }}
        }
    }

    @Test
    fun testNonFatal() {



        runCatching { throw CancellationException() }

        assertFailsWith(CancellationException::class) {
            runCatching { throw CancellationException() }.nonFatalOrThrow()
        }
        assertFailsWith(CancellationException::class) {
            catching { throw CancellationException() }
        }

        runCatching { throw IndexOutOfBoundsException() }.nonFatalOrThrow()
        catching { throw IndexOutOfBoundsException() }

    }
}
