package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class BookingRepository(private val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun findById(id: ObjectId): Mono<Booking> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.unwind("slots"),
            Aggregation.replaceRoot("slots"),
            Aggregation.unwind("bookings"),
            Aggregation.replaceRoot("bookings"),
            Aggregation.match(Criteria.where("_id").`is`(id))
        )

        return reactiveMongoTemplate.aggregate(aggregation, User.COLLECTION_NAME, Booking::class.java).next()
    }

    fun delete(booking: Booking): Mono<Booking> {
        val query = Query(Criteria.where("slots.bookings._id").`is`(booking.id))

        val update = Update().pull("slots.$.bookings", Query.query(Criteria.where("_id").`is`(booking.id)))

        return reactiveMongoTemplate.updateMulti(query, update, "users")
            .thenReturn(booking)
    }

    fun save(booking: Booking): Mono<Booking> {
        val update = Update().push("slots.$.bookings", booking)
        val query = Query(
            Criteria.where("slots._id").`in`(booking.interviewerSlotId, booking.candidateSlotId)
        )

        return reactiveMongoTemplate.updateMulti(
            query,
            update,
            Slot::class.java,
            User.COLLECTION_NAME
        ).thenReturn(booking)
    }

    fun update(booking: Booking): Mono<Booking> {
        val query = Query(
            Criteria.where("slots.bookings._id").`is`(booking.id)
        )

        val update = Update()
            .set("slots.$.bookings.$[bookingIndex].subject", booking.subject)
            .set("slots.$.bookings.$[bookingIndex].description", booking.description)
            .set("slots.$.bookings.$[bookingIndex].interviewerSlotId", booking.interviewerSlotId)
            .set("slots.$.bookings.$[bookingIndex].candidateSlotId", booking.candidateSlotId)
            .set("slots.$.bookings.$[bookingIndex].period", booking.period)

        update.filterArray(
            Criteria.where("bookingIndex._id").`is`(booking.id)
        )

        return reactiveMongoTemplate.updateMulti(query, update, User::class.java, User.COLLECTION_NAME)
            .thenReturn(booking)
    }
}
