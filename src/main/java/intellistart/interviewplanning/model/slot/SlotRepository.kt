package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.model.user.Candidate
import intellistart.interviewplanning.model.user.Interviewer
import intellistart.interviewplanning.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class SlotRepository(private val mongoTemplate: MongoTemplate) {
    fun findByEmail(email: String): List<Slot> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("email").`is`(email)),
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots")
        )

        val results = mongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Slot::class.java)
        return results.mappedResults
    }

    fun findById(id: ObjectId): Slot? {
        val query = Query(Criteria.where("slots._id").`is`(id))
        return when (val user = mongoTemplate.findOne(query, User::class.java)) {
            is Candidate -> user.slots.find { it.id == id }
            is Interviewer -> user.slots.find { it.id == id }
            else -> null
        }
    }

    fun findByEmailAndDate(email: String, date: LocalDate): List<Slot> {
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

        val results = mongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Slot::class.java)
        return results.mappedResults
    }

    fun save(slot: Slot, email: String): Slot {
        val query = Query(Criteria.where("email").`is`(email))
        val update = Update().push("slots", slot)
        mongoTemplate.updateFirst(
            query,
            update,
            User::class.java
        )
        return slot
    }

    fun update(slot: Slot, email: String): Slot {
        val query = Query(
            Criteria.where("email").`is`(email)
                .and("slots._id").`is`(slot.id)
        )
        val update = Update()
            .set("slots.$.period", slot.period)
            .set("slots.$.bookings", slot.bookings)
        mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            User::class.java
        )
        return slot
    }
}
