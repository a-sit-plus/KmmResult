/*
 * Copyright 2021 - 2023 A-SIT Plus GmbH. Obviously inspired and partially copy-pasted from kotlin.Result.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */
package at.asitplus

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

/** [KmmResult] matcher. Use as follows: `okResult should succeed`, `errResult shouldNot succeed` */
@Suppress("ClassNaming")
object succeed : Matcher<KmmResult<*>> {
    override fun test(value: KmmResult<*>) =
        MatcherResult(
            value.isSuccess,
            failureMessageFn = {
                "Should have succeeded, but failed:\n${
                    value.exceptionOrNull()!!.stackTraceToString()
                }"
            },
            negatedFailureMessageFn = { "Should have failed, but succeeded with ${value.getOrNull()!!}" }
        )
}

/** Asserts that this KmmResult should succeed, and returns the contained value */
fun <T> KmmResult<T>.shouldSucceed(): T {
    this should succeed
    return getOrThrow()
}

/** Asserts that this KmmResult should fail, and returns the contained exception */
fun KmmResult<*>.shouldFail(): Throwable {
    this shouldNot succeed
    return exceptionOrNull()!!
}

/** Alias for [shouldFail] */
fun KmmResult<*>.shouldNotSucceed() = shouldFail()

/** Shorthand for `getOrThrow() should <matcher>` */
infix fun <T> KmmResult<T>.shouldSucceedAnd(matcher: Matcher<T>): Unit = shouldSucceed() should matcher

/** Shorthand for `getOrThrow() shouldNot <matcher>` */
infix fun <T> KmmResult<T>.shouldSucceedAndNot(matcher: Matcher<T>): Unit = shouldSucceed() shouldNot matcher

/** Shorthand for `getOrThrow() shouldBe expected` */
infix fun <T> KmmResult<T>.shouldSucceedWith(expected: T): T = shouldSucceed() shouldBe expected
