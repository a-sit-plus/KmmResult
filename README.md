# Kotlin Multiplatform Result Class
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
![Build KMP](https://github.com/a-sit-plus/kmmresult/actions/workflows/build-jvm.yml/badge.svg)
![Build iOS](https://github.com/a-sit-plus/kmmresult/actions/workflows/build-ios.yml/badge.svg)

[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform--mobile-orange.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
![Java](https://img.shields.io/badge/java-11-blue.svg?logo=OPENJDK)
[![Maven Central](https://img.shields.io/maven-central/v/at.asitplus/kmmresult)](https://mvnrepository.com/artifact/at.asitplus/kmmresult/)

Wrapper for `kotlin.Result` with KMM goodness, s.t. it becomes possible to expose a result class to 
public APIs interfacing with platform-specific code. For Kotlin/Native (read: iOS), this requires a `Result` equivalent, which
is *not* a value class (a sealed `Either` type also does not interop well with Swift). 

`KmmResult` comes to the rescue! →[Full documentation](https://a-sit-plus.github.io/kmmresult/).


## Using in your Projects

This library is available at maven central.

### Gradle

```kotlin
dependencies {
    api("at.asitplus:kmmresult:$version")   //This library was designed to play well with multiplatform APIs
}                                           //and is therefore intended to be exposed through your public API
```

## Quick Start
Creation of `Success` and `Failure` objects is provided through a companion:

```kotlin
var intResult = KmmResult.success(3)
intResult = KmmResult.failure (NotImplementedError("Not Implemented"))
```

Also provides `map()`  to transform success types while passing through errors and `mapFailure` to transform error types
while passing through success cases.
In addition, the more generic `fold()` is available for conveniently operating on both success and failure branches. 


There really is not much more to say, except for two things:
 - `KmmResult` sports `unwrap()` to conveniently map it to the `kotlin.Result` equivalent
 - It provides a `Result.wrap()` extension function to go the opposite way.

Refer to the [full documentation](https://a-sit-plus.github.io/kmmresult/) for more info. 

### Java
Works from the JVM as expected:

```java
KmmResult<Boolean> demonstrate() {
    if (new Random().nextBoolean())
        return KmmResult.failure(new NotImplementedError("Not Implemented"));
    else
        return KmmResult.success(true);
}
```

### Swift
Use the initializers:

```swift
func funWithKotlin() -> KmmResult<NSString> {
    if 2 != 3 {
        return KmmResult(failure: KotlinThrowable(message: "error!"))
    } else {
        return KmmResult(value: "works!")
    }
}
```

Happy folding!