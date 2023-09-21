package intellistart.interviewplanning.bean.post.processor.candidate.slot.validation

/**
 *  Annotation that validates an object that
 *  accepts an annotated method parameter with
 *  a specified method and validator
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidatingCandidateSlot(
    val validationMethod: ValidationMethod = ValidationMethod.CREATE,
)

/**
 * Enum of methods to validate creating or updating object
 */
enum class ValidationMethod {
    CREATE,
    UPDATE
}
