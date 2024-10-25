package kotlin.internal

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
/**
 * Specifies that a corresponding member has the lowest priority in overload resolution.
 * Yo, JB; if you find an obvious need for this, why is it not part of the public API???
 */
internal annotation class LowPriorityInOverloadResolution
