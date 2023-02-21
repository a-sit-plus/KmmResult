# Kotlin Multiplatform Result Class
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-brightgreen.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform--mobile-orange.svg?logo=kotlin)](http://kotlinlang.org)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
![Java](https://img.shields.io/badge/java-11-blue.svg?logo=OPENJDK)
[![Maven Central](https://img.shields.io/maven-central/v/at.asitplus/kmmresult)](https://mvnrepository.com/artifact/at.asitplus/kmmresult/)

Functional equivalent of `kotlin.Result` but with KMM goodness, s.t. it becomes possible to expose a Result class to 
public APIs interfacing with platform-specific code. For Kotlin/Native (read: iOS), this requires a result class, which
is *not* a value class.

`KmmResult` comes to the rescue!


## Using in your Projects

This library is available at maven central.

### Gradle

```kotlin
dependencies {
    implementation("at.asitplus:kmmresult:1.4.1")   //This library was designed to play well with multiplatform APIs
                                                    //so feel free to expose it through your public API
}
```

## Quick Start
Creation of `Success` and `Failure` objects is provided through a companion:

```kotlin
var intResult = KmmResult.success(3)
success = KmmResult.failure (NotImplementedError("Not Implemented"))
```

Also provides `map()`  to transform success types while passing through errors and `mapFailure` to transform error types
while passing through success cases.
In addition, the more generic `fold()` is available for conveniently operating on both success and failure branches. 


There really is not much more to say, except for two things:
 - `KmmResult` sports `unwrap()` to conveniently map it to the `kotlin.Result` equivalent
 - It provides a `Result.wrap()` extension function to go the opposite way.

### Java
Jvm-specific convenience features are present, such that the following works:

```java
KmmResult<Boolean> demonstrate() {
    if (new Random().nextBoolean())
        return KmmResult.failure(new NotImplementedError("Not Implemented"));
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

Happy folding!