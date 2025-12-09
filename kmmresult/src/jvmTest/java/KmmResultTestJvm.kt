import io.kotest.assertions.asClue
import io.kotest.matchers.compilation.CompileConfig
import io.kotest.matchers.compilation.codeSnippet
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import kotlin.test.Test

class KmmResultTestJvm {
    @Test
    fun catchPathologicalUnitBehavior() {
        // mapCatching lambda arguments should not be automatically coerced to Unit, regardless of the specified return type
        // blocked by https://youtrack.jetbrains.com/issue/KT-82863
        /*
        codeSnippet("val x: at.asitplus.KmmResult<Unit> = at.asitplus.catching { 42 }.mapCatching { 42 }")
                .shouldNotCompile("Initializer type mismatch")
        */
    }

    @Test
    fun isThePrecedingTestFinallyWorking() {
        "refer to the source code file, this failure is a good thing!".asClue {
            // kotlin compilers from 2.2.20 onward do not properly honor @NoInfer, cf. KT-82863 linked above
            // -> if this test fails to compile with "Initializer type mismatch", that likely means that the kotlin compiler was fixed!
            // check that this is the case! if it is, you can remove this test and re-enable the "catchPathologicalUnitBehavior" test above it. yippie!
            codeSnippet("val x: at.asitplus.KmmResult<Unit> = at.asitplus.catching { 42 }.mapCatching { 42 }")
                .shouldCompile()
        }
    }
}
