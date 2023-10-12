package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class BookingRepository(private val mongoTemplate: MongoTemplate) {

    fun findById(id: ObjectId): Booking? {
//        val query = Query(Criteria.where("slots.bookings._id").`is`(id))
//        var find = mongoTemplate.find(query, Slot::class.java, User.COLLECTION_NAME)
//        return mongoTemplate.findOne(query, Booking::class.java, User.COLLECTION_NAME)

//        val aggregation = Aggregation.newAggregation(
//            Aggregation.match(Criteria.where("slots.bookings._id").`is`(id)),
//            Aggregation.unwind("slots.bookings"),
//            Aggregation.match(Criteria.where("_id").`is`(id)),
//            Aggregation.replaceRoot("bookings")
//        )

        val aggregation = Aggregation.newAggregation(
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots"),
            Aggregation.unwind("bookings"),
            Aggregation.replaceRoot("bookings"),
            Aggregation.match(Criteria.where("_id").`is`(id))
        )


        val results = mongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Booking::class.java)
        return results.mappedResults.find { it.id == id }
    }

    fun delete(booking: Booking) {
//        val aggregation = Aggregation.newAggregation(
//            Aggregation.unwind("slots"),
//            Aggregation.replaceRoot("slots"),
//            Aggregation.unwind("bookings"),
//            Aggregation.replaceRoot("bookings"),
//            Aggregation.match(Criteria.where("_id").`is`(booking.id))
//        )
//        var mappedResult = mongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Booking::class.java).mappedResults
//
////        val query = Query(Criteria.where("slots.bookings._id").`is`(booking.id))
//        val update = Update().pull("slots.$.bookings", Query(Criteria.where("_id").`is`(booking.id)))
//        var pullAll = Update().pullAll("slots.$.bookings", mappedResult.toTypedArray())
////        mongoTemplate.find(, Booking::class.java, User.COLLECTION_NAME)
//        mongoTemplate.updateMulti(query, pullAll, Booking::class.java)

        // Create a Query to find the documents matching the condition
        val query = Query(Criteria.where("slots.bookings._id").`is`(booking.id))

// Create an Update to specify the update operation
        val update = Update().pull("slots", Query.query(Criteria.where("bookings._id").`is`(booking.id)))

// Use the MongoTemplate to perform the update operation
        mongoTemplate.find(query, Booking::class.java, User.COLLECTION_NAME)
        mongoTemplate.updateMulti(query, update, "users")
    }

    fun save(booking: Booking): Booking {
        val update = Update().push("slots.$.bookings", booking)
        val query = Query(
//            Criteria.where("slots._id").`is`(booking.interviewerSlotId)
//                .orOperator(
//                    Criteria.where("slots._id").`is`(booking.candidateSlotId)
//                )
            Criteria.where("slots._id").`in`(booking.interviewerSlotId, booking.candidateSlotId)
        )
        var find = mongoTemplate.find(query, User::class.java, User.COLLECTION_NAME)
        mongoTemplate.updateMulti(
            query,
            update,
            Slot::class.java,
            User.COLLECTION_NAME
        )
        return booking
    }

    fun update(booking: Booking): Booking {
//        val query = Query(Criteria.where("slots.bookings.id").`is`(booking.id))
//        val update = Update()
//            .set("slots.$.bookings.$.subject", booking.subject)
//            .set("slots.$.bookings.$.description", booking.description)
//            .set("slots.$.bookings.$.interviewerSlotId", booking.interviewerSlotId)
//            .set("slots.$.bookings.$.candidateSlotId", booking.candidateSlotId)
//            .set("slots.$.bookings.$.period", booking.period)
//
//        mongoTemplate.updateMulti(query, update, Slot::class.java, User.COLLECTION_NAME)
//
//        return booking

        val query = Query(
            Criteria.where("slots.bookings._id").`is`(booking.id)
        )

        val update = Update()
            .set("slots.$.bookings.$[bookingIndex].subject", booking.subject)
            .set("slots.$.bookings.$[bookingIndex].description", booking.description)
            .set("slots.$.bookings.$[bookingIndex].interviewerSlotId", booking.interviewerSlotId)
            .set("slots.$.bookings.$[bookingIndex].candidateSlotId", booking.candidateSlotId)
            .set("slots.$.bookings.$[bookingIndex].period", booking.period)

//        update.filterArray(
//            Criteria.where("slotIndex._id").`is`(booking.interviewerSlotId)
//                .orOperator(
//                    Criteria.where("slotIndex._id").`is`(booking.candidateSlotId)
//                )
//        )
        update.filterArray(
            Criteria.where("bookingIndex._id").`is`(booking.id)
        )
        var find = mongoTemplate.find(query, User::class.java, User.COLLECTION_NAME)
        mongoTemplate.updateMulti(query, update, User::class.java, User.COLLECTION_NAME)

        return booking
    }
}
