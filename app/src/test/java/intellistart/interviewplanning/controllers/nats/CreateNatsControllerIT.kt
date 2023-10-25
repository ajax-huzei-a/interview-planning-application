package intellistart.interviewplanning.controllers.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import com.google.protobuf.Timestamp
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.commonmodels.slot.SlotProto
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
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.remove
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CreateNatsControllerIT {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @AfterEach
    fun cleanDB() {
        mongoTemplate.remove<User>()
    }

    @ParameterizedTest
    @CsvSource(
        "5f4d92a129bda0e1921f78a0, 36000, 41400, 1981584000, test1@gmail.com",
        "5f4d92a129bda0e1921f78a1, 39600, 45000, 1785456000, test2Gmail.com",
        "5f4d92a129bda0e1921f78a2, 43200, 48600, 1700352000, test1@gmail.com",
        "5f4d92a129bda0e1921f78a3, 46800, 52200, 1836432000, test2@gmail.com"
    )
    fun `should return success response for create slot`(
        idTest: String,
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        dateTimestampSeconds: Long,
        emailTest: String
    ) {
        // GIVEN
        val fromDuration = com.google.protobuf.Duration.newBuilder().setSeconds(fromDurationSeconds).build()
        val toDuration = com.google.protobuf.Duration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateTimestamp = Timestamp.newBuilder().setSeconds(dateTimestampSeconds).build()

        val createSlotRequest = CreateSlotRequest.newBuilder().apply {
            slotProtoBuilder.apply {
                id = idTest
                from = fromDuration
                to = toDuration
                date = dateTimestamp
            }.build()
            email = emailTest
        }.build()

        val expectedSlotResponse = CreateSlotResponse.newBuilder().apply {
            successBuilder.setSlotProto(
                SlotProto.newBuilder().apply {
                    id = idTest
                    from = fromDuration
                    to = toDuration
                    date = dateTimestamp
                }.build()
            )
        }.build()

        // WHEN
        val actual = doRequest(
            createSlotRequest,
            CreateSlotResponse.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    @ParameterizedTest
    @CsvSource(
        "3600, 4200, 1685500800, test1@gmail.com",
        "3960, 4474, 1690406400, test2Gmail.com",
        "43200, 48600, 1666070483, test1@gmail.com",
        "46800, 52740, 168752000, test2@gmail.com"
    )
    fun `should return failure response for create slot`(
        fromDurationSeconds: Long,
        toDurationSeconds: Long,
        dateTimestampSeconds: Long,
        emailTest: String
    ) {
        // GIVEN
        val fromDuration = com.google.protobuf.Duration.newBuilder().setSeconds(fromDurationSeconds).build()
        val toDuration = com.google.protobuf.Duration.newBuilder().setSeconds(toDurationSeconds).build()
        val dateTimestamp = Timestamp.newBuilder().setSeconds(dateTimestampSeconds).build()

        val createSlotRequest = CreateSlotRequest.newBuilder().apply {
            slotProtoBuilder.apply {
                from = fromDuration
                to = toDuration
                date = dateTimestamp
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
            java.time.Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }

//    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
//        subject: String,
//        payload: RequestT,
//        parser: Parser<ResponseT>,
//    ): ResponseT {
//        val response = connection.requestWithTimeout(
//            subject,
//            payload.toByteArray(),
//            Duration.ofSeconds(10L)
//        )
//        return parser.parseFrom(response.get().data)
//    }
}
