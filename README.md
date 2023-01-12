# Kotlin Multiplatform Result Class
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform--mobile-orange.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)

Functional equivalent of `kotlin.Result` but with KMM goodness, s.t. it becomes possible to expose a Result class to 
public APIs interfacing with platform-specific code. For Kotlin/Native (read: iOS), this requires a result class, which
is *not* a value class.

`KmmResult` comes to the rescue!

There really is not much more to say, except for two things:
 - `KmmResult` sports `unwrap()` to conveniently map it to the `kotlin.Result` equivalent,
 - It provides a `Result.wrap()` extension function to go the opposite way.

Happy folding!

Swift clients may need the following wrapping methods for a nice looking code:

```swift
func KmmResultFailure<T>(_ error: KotlinThrowable) -> KmmResult<T> where T: AnyObject {
    return KmmResult<T>.companion.failure(error: error) as! KmmResult<T>
}

func KmmResultSuccess<T>(_ value: T) -> KmmResult<T> where T: AnyObject {
    return KmmResult<T>.companion.success(value: value) as! KmmResult<T>
}
```
