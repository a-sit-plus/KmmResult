import io.kotest.matchers.compilation.CompileConfig
import io.kotest.matchers.compilation.codeSnippet
import io.kotest.matchers.compilation.shouldNotCompile
import kotlin.test.Test

class KmmResultTestJvm {
    @Test
    fun catchPathologicalUnitBehavior() {
        // blocked by https://youtrack.jetbrains.com/issue/KT-82863
        codeSnippet("val x: at.asitplus.KmmResult<Unit> = at.asitplus.catching { 42 }.mapCatching { 42 }")
                .shouldNotCompile("Initializer type mismatch")
    }
}
