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

## 1.7.1
- migrate to dokka 2 for documentation
- multi-module project setup
- introduce `kmmresult-test`, enabling
  - `result should succeed`
  - `result shouldNot succeed`
  - `result shouldSucceedWith expectedValue`
