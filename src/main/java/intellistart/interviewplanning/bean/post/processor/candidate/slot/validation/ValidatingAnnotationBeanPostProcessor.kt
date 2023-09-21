package intellistart.interviewplanning.bean.post.processor.candidate.slot.validation

import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.candidateslot.validation.CandidateSlotValidator
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.Enhancer
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.stereotype.Component

@Component
class ValidatingAnnotationBeanPostProcessor : BeanPostProcessor, BeanFactoryAware {

    private val validatableBeans = mutableMapOf<String, ValidatableBean>()

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(p0: BeanFactory) {
        this.beanFactory = p0
    }

    private class ValidatableBean(
        val beanType: Class<*>,
        val validatableMethods: Map<String, ValidatingCandidateSlot>
    )

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val annotatedMethods = bean.javaClass.declaredMethods.asSequence()
            .filter { method -> method.isAnnotationPresent(ValidatingCandidateSlot::class.java) }
            .map { method -> method.name to method.getAnnotation(ValidatingCandidateSlot::class.java) }
            .toMap()

        if (annotatedMethods.isNotEmpty()) {
            validatableBeans[beanName] = ValidatableBean(bean::class.java, annotatedMethods)
        }

        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        return validatableBeans[beanName]
            ?.let { validatableBean ->
                decorate(validatableBean, bean, beanName)
            } ?: bean
    }

    private fun decorate(validatableBean: ValidatableBean, bean: Any, beanName: String): Any {
        return Enhancer().apply {
            setSuperclass(validatableBean.beanType)
            setCallback(buildInterceptor(bean, beanName))
        }.create()
    }

    private fun buildInterceptor(bean: Any, beanName: String): MethodInterceptor =
        MethodInterceptor { _, method, args, proxy ->
            validatableBeans[beanName]?.validatableMethods?.get(method.name)?.let { annotation ->
                val validationMethod = annotation.validationMethod
                val validator: CandidateSlotValidator = beanFactory.getBean(CandidateSlotValidator::class.java)

                when (validationMethod) {
                    ValidationMethod.CREATE -> validator.validateCreating(args[0] as CandidateSlot)
                    ValidationMethod.UPDATE -> validator.validateUpdating(args[0] as CandidateSlot)
                }
            }
            proxy.invoke(bean, args)
        }
}
