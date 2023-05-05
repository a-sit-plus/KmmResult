import at.asitplus.KmmResult
import at.asitplus.KmmResult.Companion.wrap
import kotlin.test.Test
import kotlin.test.assertEquals

class KmmResultTest {
    @Test
    fun testMap() {
        assertEquals("1234", KmmResult.success(1234).map { it.toString() }.getOrThrow())
        val throwable = NullPointerException("Null")
        val fail: KmmResult<Int> = KmmResult.failure(throwable)
        assertEquals(fail, fail.map { it * 3 })
        assertEquals(throwable, fail.map { it * 3 }.exceptionOrNull())
    }

    @Test
    fun testWhen() {
        val fail: KmmResult<Int> = KmmResult.failure(NullPointerException("Null"))
        fail.onSuccess { throw IllegalStateException("this must never happen") }
    }

    @Test
    fun testGetOrElse() {
        val result: KmmResult<Int> = runCatching { throw NullPointerException("NULL") }.wrap()
        assertEquals(3, result.getOrElse { 3 })
        assertEquals(3, (KmmResult.failure<Int>(NullPointerException("NULL"))).getOrElse { 3 })
    }
}
