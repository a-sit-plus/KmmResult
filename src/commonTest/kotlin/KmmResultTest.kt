import at.asitplus.Failure
import at.asitplus.KmmResult
import at.asitplus.Success
import at.asitplus.wrap
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
    fun testGetOrElse() {

        val result: KmmResult<Int> = runCatching { throw NullPointerException("NULL") }.wrap()
        assertEquals(3, result.getOrElse { 3 })
        assertEquals(3, (KmmResult.failure(NullPointerException("NULL")) as KmmResult<Int>).getOrElse { 3 })
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun testHelpers() {
        val fail = Failure(Int::class, NullPointerException())
        val res2: KmmResult<Int> = fail

        val suc = Success(3)
        val res3: KmmResult<Int> = suc
    }
}