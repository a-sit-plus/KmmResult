# Kotlin Multiplatform Result Class
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform--mobile-orange.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.10-blue.svg?logo=OPENJDK)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/at.asitplus/kmmresult)](https://mvnrepository.com/artifact/at.asitplus/kmmresult/)

Functional equivalent of `kotlin.Result` but with KMM goodness, s.t. it becomes possible to expose a Result class to 
public APIs interfacing with platform-specific code. For Kotlin/Native (read: iOS), this requires a result class, which
is *not* a value class.

`KmmResult` comes to the rescue!

## Usage
Creation of Success and Failure objects is provided through a companion:

```kotlin
var success: KmmResult<Int> = KmmResult.success(3)
success = KmmResult.failure (TODO("Not Implemented"))
```

Also provides `map()`  to transform success types while passing through errors
and `fold()` for conveniently operating on both success and failure branches 


There really is not much more to say, except for two things:
 - `KmmResult` sports `unwrap()` to conveniently map it to the `kotlin.Result` equivalent,
 - It provides a `Result.wrap()` extension function to go the opposite way.

Happy folding!

### Java
Jvm-specific convenience features are present, such that the following works:

```java
KmmResult<Boolean> demonstrate() {
    if (new Random().nextBoolean())
        return KmmResult.failure(new NullPointerException("null"));
    else
        return KmmResult.success(true);
}
```


### Swift
Swift clients may need the following wrapping methods for a nice looking code:

```swift
func KmmResultFailure<T>(_ error: KotlinThrowable) -> KmmResult<T> where T: AnyObject {
    return KmmResult<T>.companion.failure(error: error) as! KmmResult<T>
}

func KmmResultSuccess<T>(_ value: T) -> KmmResult<T> where T: AnyObject {
    return KmmResult<T>.companion.success(value: value) as! KmmResult<T>
}
```
