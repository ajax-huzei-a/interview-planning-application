package intellistart.interviewplanning.controllers.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.google.protobuf.Timestamp
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.commonmodels.slot.SlotProto
import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.slot.SlotService
import intellistart.interviewplanning.model.user.Role
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.request.slot.update.proto.UpdateSlotRequest
import intellistart.interviewplanning.request.slot.update.proto.UpdateSlotResponse
import io.nats.client.Connection
import org.assertj.core.api.Assertions
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findAllAndRemove
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset

@SpringBootTest
@ActiveProfiles("test")
class UpdateNatsControllerIT {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var slotService: SlotService

    @Autowired
    lateinit var userService: UserService

    @AfterEach
    fun cleanDB() {
        mongoTemplate.findAllAndRemove<User>(Query())
    }

    @ParameterizedTest
    @CsvSource(
        "5f4d92a129bda0e1921f78a0, 36000, 66600, 1981584000, test1@gmail.com, 39600",
        "5f4d92a129bda0e1921f78a1, 39600, 66600, 1785456000, test2Gmail.com, 43200",
        "5f4d92a129bda0e1921f78a2, 43200, 66600, 1700352000, test3@gmail.com, 46800",
        "5f4d92a129bda0e1921f78a3, 46800, 66600, 1836432000, test4@gmail.com, 50400"
    )
    @Suppress("LongParameterList")
    fun `should return success response for update slot`(
        idTest: String,
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        dateTimestampSeconds: Long,
        emailTest: String,
        newDataOfSecondsForFrom: Long
    ) {
        // GIVEN
        val newFromDuration = com.google.protobuf.Duration.newBuilder().setSeconds(newDataOfSecondsForFrom).build()
        val toDuration = com.google.protobuf.Duration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateTimestamp = Timestamp.newBuilder().setSeconds(dateTimestampSeconds).build()

        userService.grantRoleByEmail(emailTest, Role.CANDIDATE)

        slotService.create(
            Slot(
                id = ObjectId(idTest),
                period = intellistart.interviewplanning.model.period.Period(
                    date = Instant.ofEpochSecond(dateTimestampSeconds).atZone(ZoneOffset.UTC).toLocalDate(),
                    from = LocalTime.ofSecondOfDay(fromDurationSeconds),
                    to = LocalTime.ofSecondOfDay(toDurationSeconds)
                ),
                bookings = listOf()
            ),
            emailTest
        )

        val updateSlotRequest = UpdateSlotRequest.newBuilder().apply {
            slotId = idTest
            slotProtoBuilder.apply {
                id = idTest
                from = newFromDuration
                to = toDuration
                date = dateTimestamp
            }.build()
            email = emailTest
        }.build()

        val expectedSlotResponse = UpdateSlotResponse.newBuilder().apply {
            successBuilder.setSlotProto(
                SlotProto.newBuilder().apply {
                    id = idTest
                    from = newFromDuration
                    to = toDuration
                    date = dateTimestamp
                }.build()
            )
        }.build()

        // WHEN
        val actual = doRequest(
            updateSlotRequest,
            UpdateSlotResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    @ParameterizedTest
    @CsvSource(
        "5f4d92a129bda0e1921f78a0, 36000, 66600, 1981584000, test1@gmail.com, 39720",
        "5f4d92a129bda0e1921f78a1, 39600, 66600, 1785456000, test2Gmail.com, 43500",
        "5f4d92a129bda0e1921f78a2, 43200, 66600, 1700352000, test3@gmail.com, 4700",
        "5f4d92a129bda0e1921f78a3, 46800, 66600, 1836432000, test4@gmail.com, 50460"
    )
    @Suppress("LongParameterList")
    fun `should return failure response for update slot`(
        idTest: String,
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        dateTimestampSeconds: Long,
        emailTest: String,
        newDataOfSecondsForFrom: Long
    ) {
        // GIVEN
        val newFromDuration = com.google.protobuf.Duration.newBuilder().setSeconds(newDataOfSecondsForFrom).build()
        val toDuration = com.google.protobuf.Duration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateTimestamp = Timestamp.newBuilder().setSeconds(dateTimestampSeconds).build()

        userService.grantRoleByEmail(emailTest, Role.CANDIDATE)

        slotService.create(
            Slot(
                id = ObjectId(idTest),
                period = intellistart.interviewplanning.model.period.Period(
                    date = Instant.ofEpochSecond(dateTimestampSeconds).atZone(ZoneOffset.UTC).toLocalDate(),
                    from = LocalTime.ofSecondOfDay(fromDurationSeconds),
                    to = LocalTime.ofSecondOfDay(toDurationSeconds)
                ),
                bookings = listOf()
            ),
            emailTest
        )

        val updateSlotRequest = UpdateSlotRequest.newBuilder().apply {
            slotId = idTest
            slotProtoBuilder.apply {
                id = idTest
                from = newFromDuration
                to = toDuration
                date = dateTimestamp
            }
            email = emailTest
        }.build()

        val expectedSlotResponse = UpdateSlotResponse.newBuilder().apply {
            failureBuilder.apply {
                failureBuilder.message = "Time boundaries of slot or booking are invalid."
                failureBuilder.invalidBoundariesBuilder
            }
        }.build()

        // WHEN
        val actual = doRequest(
            updateSlotRequest,
            UpdateSlotResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.requestWithTimeout(
            NatsSubject.Slot.UPDATE,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }
}