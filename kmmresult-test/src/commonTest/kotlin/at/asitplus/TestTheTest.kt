package at.asitplus

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.be
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.shouldBeTypeOf
import kotlin.test.Test

class KmmResultTestTest {
    @Test
    fun success() {
        val result = catching { 42 }
        shouldNotThrowAny { result should succeed }
        shouldNotThrowAny { result.shouldSucceed() } shouldBe 42
        shouldNotThrowAny { result shouldSucceedAnd be(42) }
        shouldNotThrowAny { result shouldSucceedAndNot be(41) }
        shouldNotThrowAny { result shouldSucceedWith 42 }

        shouldThrow<AssertionError> { result shouldNot succeed }
        shouldThrow<AssertionError> { result shouldSucceedWith 41 }
        shouldThrow<AssertionError> { result shouldSucceedAndNot be(42) }
        shouldThrow<AssertionError> { result.shouldNotSucceed() }
    }

    @Test
    fun failure() {
        class TheMagicException : Throwable()
        val result = catching { throw TheMagicException() }
        shouldThrow<AssertionError> { result should succeed }
        shouldThrow<AssertionError> { result shouldSucceedAnd be(42) }
        shouldThrow<AssertionError> { result shouldSucceedAndNot be(42) }
        shouldThrow<AssertionError> { result shouldSucceedWith 42 }

        shouldNotThrowAny { result shouldNot succeed }
        shouldNotThrowAny { result.shouldNotSucceed() }.shouldBeTypeOf<TheMagicException>()
    }
}
