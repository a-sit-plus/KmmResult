import Foundation

/// Converts to Swift's Result while preserving the exported generic argument.
/// Success is optional because Kotlin's Objective-C export does not distinguish nullable T.
public extension Result where Failure == Error {
    init<T>(_ result: KmmResult<T>) where Success == T? {
        self.init { try result.getOrThrow() }
    }
}

// Swift cannot implement generic methods directly on an Objective-C generic class.
@objc public protocol KmmResultSwiftConvertible {
    var erasedKmmResult: KmmResult<AnyObject> { get }
}

extension KmmResult: KmmResultSwiftConvertible {
    // Objective-C generic arguments are erased, so this changes only the static type.
    public var erasedKmmResult: KmmResult<AnyObject> { (self as AnyObject) as! KmmResult<AnyObject> }
}

public extension KmmResultSwiftConvertible {
    /// Converts to a chosen Swift success type, including bridged arrays and optional values.
    /// A type mismatch becomes a failure rather than a forced cast.
    func swiftResult<Value>(as type: Value.Type) -> Result<Value, Error> {
        let result = erasedKmmResult
        return Result {
            let value = try result.getOrThrow()
            guard let typed = value as? Value else {
                throw NSError(
                    domain: "KmmResult.SwiftResult", code: 1,
                    userInfo: [NSLocalizedDescriptionKey: "Expected \(Value.self), received \(value.map { String(describing: Swift.type(of: $0)) } ?? "nil")"]
                )
            }
            return typed
        }
    }
}
