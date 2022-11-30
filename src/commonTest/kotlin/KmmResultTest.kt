import at.asitplus.KmmResult
import kotlin.test.Test
import kotlin.test.assertEquals

class KmmResultTest {
    @Test
    fun testMap() {
        assertEquals("1234", KmmResult.success(1234).map { it.toString() }.value)
        val throwable = NullPointerException("Null")
        val fail = KmmResult.failure<Int>(throwable)
        assertEquals(fail, fail.map { it * 3 })
        assertEquals(throwable, fail.map { it * 3 }.error)
    }
}