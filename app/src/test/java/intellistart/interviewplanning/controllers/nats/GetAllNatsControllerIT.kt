package intellistart.interviewplanning.controllers.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.controllers.dto.toProto
import intellistart.interviewplanning.model.period.Period
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsRequest
import intellistart.interviewplanning.request.slot.get_all.proto.GetAllSlotsResponse
import io.nats.client.Connection
import org.assertj.core.api.Assertions
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset

@SpringBootTest
@ActiveProfiles("test")
class GetAllNatsControllerIT {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Autowired
    lateinit var slotService: SlotService

    @Autowired
    lateinit var userService: UserService

    @AfterEach
    fun cleanDB() {
        reactiveMongoTemplate.dropCollection<User>().block()
    }

    @ParameterizedTest
    @CsvSource(
        "5f4d92a129bda0e1921f78a0, 36000, 66600, 1981584000, test1@gmail.com",
        "5f4d92a129bda0e1921f78a1, 39600, 66600, 1785456000, test2Gmail.com",
        "5f4d92a129bda0e1921f78a2, 43200, 66600, 1700352000, test3@gmail.com",
        "5f4d92a129bda0e1921f78a3, 46800, 66600, 1836432000, test4@gmail.com"
    )
    fun `should return success response for get all slots`(
        idTest: String,
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        dateTimestampSeconds: Long,
        emailTest: String
    ) {
        // GIVEN
        userService.grantRoleByEmail(emailTest, Role.CANDIDATE).block()

        slotService.create(
            Slot(
                id = ObjectId(idTest),
                period = Period(
                    date = Instant.ofEpochSecond(dateTimestampSeconds).atZone(ZoneOffset.UTC).toLocalDate(),
                    from = LocalTime.ofSecondOfDay(fromDurationSeconds),
                    to = LocalTime.ofSecondOfDay(toDurationSeconds)
                ),
                bookings = listOf()
            ),
            emailTest
        ).block()

        slotService.create(
            Slot(
                id = ObjectId(idTest),
                period = Period(
                    date = Instant.ofEpochSecond(dateTimestampSeconds + SECONDS_IN_DAY)
                        .atZone(ZoneOffset.UTC).toLocalDate(),
                    from = LocalTime.ofSecondOfDay(fromDurationSeconds),
                    to = LocalTime.ofSecondOfDay(toDurationSeconds)
                ),
                bookings = listOf()
            ),
            emailTest
        ).block()

        val getAllSlotRequest = GetAllSlotsRequest.newBuilder().apply {
            email = emailTest
        }.build()

        val expectedResponse = GetAllSlotsResponse.newBuilder().apply {
            successBuilder.slotsBuilder.addAllSlotProto(
                slotService.getAllSlotsByEmail(emailTest).map { it.toProto() }.toIterable()
            )
        }.build()

        // WHEN
        val actual = doRequest(
            getAllSlotRequest,
            GetAllSlotsResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedResponse)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.requestWithTimeout(
            NatsSubject.Slot.GET_ALL,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }

    companion object {
        private const val SECONDS_IN_DAY = 86400
    }
}
