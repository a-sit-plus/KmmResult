# Changelog

## 1.4.0
 - First public release

### 1.4.1
- Update to Kotlin 1.8.10
- JVM-specific niceness

## 1.5.0
- complete rewrite to directly encapsulate a `Kotlin.result`
- Fix generics-related swift interop issues by relying on initializers rather than functions
- Kotlin 1.8.20
- Gradle 8.1.1

### 1.5.1
- fix missing `inline modifiers`
- Kotlin 1.8.21

### 1.5.2
- Kotlin 1.9.0

### 1.5.3
- Kotlin 1.9.10

### 1.5.4
- Add `transform()` function to map results without nesting
- Add `mapCatching()`
- Implement `equals()` and `hashCode()`

## 1.6.0
- Kotlin 2.0
- Failure re-throws any fatal and coroutine-related exceptions
- `catching` function, modelling stdlib's `runCatching`, directly returning a `KmmResult`

## 1.6.1
- add missing `catching` variant

## 1.6.2
- `wrapping` function, which wraps any exception as the specified type

## 1.7.0
- add `out` qualifier to KmmResult's type parameter
- add `recoverCatching` to match Kotlin's `result`
- add `callsInPlace` contracts
- return result fon `onFailure` and `onSuccess`

## 1.8.0
- migrate to dokka 2 for documentation
- multi-module project setup
- introduce `kmmresult-test`, featuring
  - `result should succeed`
  - `result shouldNot succeed`
  - `result shouldSucceedWith expectedValue`
  - `result.shouldSucceed()` returning the contained value
- remove Arrow dependency and import arrow's list of Fatal exceptions directly into our code
- Introduce `Result.nonFatalOrThrow` to mimic KmmResult's non-fatal-only behaviour, but without the object instantiation overhead
- Introduce `carchingUnwrapped`, which mimics KmmResult's non-fatal-only behaviour, but without the object instantiation overhead

## 1.9.0
- add WasmJS target
- add WasmWasi target (not for KmmResult-test, as Kotest does not support WASI yet)
- Function Renames (old ones are still present, but deprecated)
- rename `wrapping` -> `catchingAs` but keep the old names as deprecated alternative
  - add `catchingUnwrappedAs`, which works just like `catchingAs` but on a `Result` rather than a `KmmResult` to avoid instantiation overhead

## 1.9.1
- Kotlin 2.1.0
- Add dedicated Android target
  - JVM target uses JDK 11
  - Android Target uses JDK 1.8 for maximum compatibility