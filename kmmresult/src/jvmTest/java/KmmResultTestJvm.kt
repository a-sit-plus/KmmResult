import io.kotest.matchers.compilation.CompileConfig
import io.kotest.matchers.compilation.codeSnippet
import io.kotest.matchers.compilation.shouldNotCompile
import kotlin.test.Test

class KmmResultTestJvm {
    @Test
    fun catchPathologicalUnitBehavior() {
        // currently disabled, cf. https://github.com/kotest/kotest/issues/5244
        /*codeSnippet("val x: at.asitplus.KmmResult<Unit> = at.asitplus.catching { 42 }.mapCatching { 42 }")
                .shouldNotCompile("Initializer type mismatch")*/
    }
}
