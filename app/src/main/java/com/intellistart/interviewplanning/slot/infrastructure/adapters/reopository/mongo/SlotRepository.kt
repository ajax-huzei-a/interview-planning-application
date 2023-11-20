package com.intellistart.interviewplanning.slot.infrastructure.adapters.reopository.mongo

import com.intellistart.interviewplanning.model.user.User
import com.intellistart.interviewplanning.slot.port.SlotRepositoryOutPort
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.adapters.reopository.mongo.entity.SlotEntity
import com.intellistart.interviewplanning.slot.infrastructure.mapper.toDomain
import com.intellistart.interviewplanning.slot.infrastructure.mapper.toEntity
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
class SlotRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : SlotRepositoryOutPort {

    override fun findByEmail(email: String): Flux<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("email").`is`(email)),
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots")
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, SlotEntity::class.java)
            .map { it.toDomain() }
    }

    override fun findById(id: String): Mono<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots"),
            Aggregation.match(Criteria.where("_id").`is`(ObjectId(id)))
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, SlotEntity::class.java).next()
            .map { it.toDomain() }
    }

    override fun findByEmailAndDate(email: String, date: LocalDate): Flux<Slot> {
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

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, SlotEntity::class.java)
            .map { it.toDomain() }
    }

    override fun save(slot: Slot, email: String): Mono<Slot> {
        val query = Query(Criteria.where("email").`is`(email))
        val update = Update().push("slots", slot.toEntity())

        return reactiveMongoTemplate.updateFirst(
            query,
            update,
            User::class.java
        ).thenReturn(slot)
    }

    override fun update(slot: Slot, email: String): Mono<Slot> {
        val slotEntity = slot.toEntity()
        val query = Query(
            Criteria.where("email").`is`(email)
                .and("slots._id").`is`(slotEntity.id)
        )
        val update = Update()
            .set("slots.$.period", slotEntity.period)
            .set("slots.$.bookings", slotEntity.bookings)

        return reactiveMongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            User::class.java
        ).thenReturn(slot)
    }
}
