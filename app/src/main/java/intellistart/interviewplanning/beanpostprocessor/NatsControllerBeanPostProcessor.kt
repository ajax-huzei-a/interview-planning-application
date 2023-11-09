package intellistart.interviewplanning.beanpostprocessor

import com.google.protobuf.GeneratedMessageV3
import intellistart.interviewplanning.controllers.nats.NatsController
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class NatsControllerBeanPostProcessor : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            initializeNatsController(bean)
        }
        return bean
    }

    fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3>
    initializeNatsController(
        controller: NatsController<RequestT, ResponseT>
    ) {
        val dispatcher = controller.connection.createDispatcher { message ->
            val parsedData = controller.parser.parseFrom(message.data)
            Mono.defer { controller.handle(parsedData) }
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe {
                    controller.connection.publish(message.replyTo, it.toByteArray())
                }
        }
        dispatcher.subscribe(controller.subject)
    }
}
