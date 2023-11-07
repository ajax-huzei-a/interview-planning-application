package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository
class SlotRepository(private val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun findByEmail(email: String): Flux<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("email").`is`(email)),
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots")
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Slot::class.java)
    }

    fun findById(id: ObjectId): Mono<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots"),
            Aggregation.match(Criteria.where("_id").`is`(id))
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Slot::class.java).next()
    }

    fun findByEmailAndDate(email: String, date: LocalDate): Flux<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("email").`is`(email)
                    .and("slots").elemMatch(
                        Criteria.where("period.date").`is`(date)
                    )
            ),
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots")
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Slot::class.java)
    }

    fun save(slot: Slot, email: String): Mono<Slot> {
        val query = Query(Criteria.where("email").`is`(email))
        val update = Update().push("slots", slot)

        return reactiveMongoTemplate.updateFirst(
            query,
            update,
            User::class.java
        ).thenReturn(slot)
    }

    fun update(slot: Slot, email: String): Mono<Slot> {
        val query = Query(
            Criteria.where("email").`is`(email)
                .and("slots._id").`is`(slot.id)
        )
        val update = Update()
            .set("slots.$.period", slot.period)
            .set("slots.$.bookings", slot.bookings)

        return reactiveMongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            User::class.java
        ).thenReturn(slot)
    }
}
