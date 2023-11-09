package intellistart.interviewplanning.controllers.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.commonmodels.slot.Slot
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotRequest
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotResponse
import io.nats.client.Connection
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import com.google.protobuf.Duration as ProtobufDuration
import com.google.type.Date as ProtobufDate

@SpringBootTest
@ActiveProfiles("test")
class CreateNatsControllerIT {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @AfterEach
    fun cleanDB() {
        reactiveMongoTemplate.dropCollection<User>().block()
    }

    @ParameterizedTest
    @CsvSource(
        "5f4d92a129bda0e1921f78a0, 36000, 41400, 2084, 10, 15, test1@gmail.com",
        "5f4d92a129bda0e1921f78a1, 39600, 45000, 2094, 10, 22, test2Gmail.com",
        "5f4d92a129bda0e1921f78a2, 43200, 48600, 2084, 10, 5, test1@gmail.com",
        "5f4d92a129bda0e1921f78a3, 46800, 52200, 2074, 10, 4, test2@gmail.com"
    )
    @Suppress("LongParameterList")
    fun `should return success response for create slot`(
        idTest: String,
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        yearInput: Int,
        monthInput: Int,
        dayInput: Int,
        emailTest: String,
    ) {
        // GIVEN
        val fromDuration = ProtobufDuration.newBuilder().setSeconds(fromDurationSeconds).build()
        val toDuration = ProtobufDuration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateInput = ProtobufDate.newBuilder().apply {
            year = yearInput
            month = monthInput
            day = dayInput
        }.build()

        val createSlotRequest = CreateSlotRequest.newBuilder().apply {
            slotBuilder.apply {
                id = idTest
                from = fromDuration
                to = toDuration
                date = dateInput
            }.build()
            email = emailTest
        }.build()

        val expectedSlotResponse = CreateSlotResponse.newBuilder().apply {
            successBuilder.setSlot(
                Slot.newBuilder().apply {
                    id = idTest
                    from = fromDuration
                    to = toDuration
                    date = dateInput
                }.build()
            )
        }.build()

        // WHEN
        val actual: CreateSlotResponse = doRequest(
            createSlotRequest,
            CreateSlotResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    @ParameterizedTest
    @CsvSource(
        "3600, 4200, 2014, 10, 15, test1@gmail.com",
        "3960, 4474, 1094, 10, 22, test2Gmail.com",
        "43200, 48600, 2008, 10, 5, test1@gmail.com",
        "46800, 52740, 1999, 10, 4, test2@gmail.com"
    )
    @Suppress("LongParameterList")
    fun `should return failure response for create slot`(
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        yearInput: Int,
        monthInput: Int,
        dayInput: Int,
        emailTest: String
    ) {
        // GIVEN
        val fromDuration = ProtobufDuration.newBuilder().setSeconds(fromDurationSeconds).build()
        val toDuration = ProtobufDuration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateInput = ProtobufDate.newBuilder().apply {
            year = yearInput
            month = monthInput
            day = dayInput
        }.build()

        val createSlotRequest = CreateSlotRequest.newBuilder().apply {
            slotBuilder.apply {
                from = fromDuration
                to = toDuration
                date = dateInput
            }
            email = emailTest
        }.build()

        val expectedSlotResponse = CreateSlotResponse.newBuilder().apply {
            failureBuilder.apply {
                failureBuilder.message = "New date for this slot is in the past."
                failureBuilder.slotIsInThePastBuilder
            }
        }.build()

        // WHEN
        val actual = doRequest(
            createSlotRequest,
            CreateSlotResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.requestWithTimeout(
            NatsSubject.Slot.CREATE,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }
}
