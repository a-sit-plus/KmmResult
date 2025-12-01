import io.kotest.matchers.compilation.shouldNotCompile
import kotlin.test.Test

class KmmResultTestJvm {
    @Test
    fun catchPathologicalUnitBehavior() {
        "val x: at.asitplus.KmmResult<Unit> = at.asitplus.catching { 42 }.mapCatching { 42 }".shouldNotCompile("Initializer type mismatch")
    }
}