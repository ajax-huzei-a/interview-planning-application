package java.intellistart.interviewplanning.controllers.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import intellistart.interviewplanning.NatsSubject
import intellistart.interviewplanning.commonmodels.slot.SlotProto
import intellistart.interviewplanning.config.Configuration
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotRequest
import intellistart.interviewplanning.request.slot.create.proto.CreateSlotResponse
import io.nats.client.Connection
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration

@SpringBootTest(classes = [Configuration::class])
@ActiveProfiles("test")
class CreateNatsControllerTest {

    @Autowired
    lateinit var connection: Connection

    @ParameterizedTest
    @CsvSource(
        "10:00, 11:30, 2034-10-12, test1@gmail.com",
        "11:00, 12:30, 2034-11-24, test2Gmail.com",
        "12:00, 13:30, 2034-12-12, test1@gmail.com",
        "13:00, 14:30, 2034-09-11, test2@gmail.com"
    )
    fun `should return success response for create slot`(
        fromTest: String,
        toTest: String,
        dateTest: String,
        emailTest: String
    ) {
        // GIVEN
        val createSlotRequest = CreateSlotRequest.newBuilder().apply {
            slotProtoBuilder.apply {
                from = fromTest
                to = toTest
                date = dateTest
            }
            email = emailTest
        }.build()

        val expectedSlotResponse = CreateSlotResponse.newBuilder().apply {
            successBuilder.setSlotProto(
                SlotProto.newBuilder().apply {
                    from = fromTest
                    to = toTest
                    date = dateTest
                }.build()
            )
        }.build()

        // WHEN
        val actual = doRequest(
            NatsSubject.Slot.CREATE,
            createSlotRequest,
            CreateSlotRequest.parser()
        )

        // THEN
        Assertions.assertThat(actual).isEqualTo(expectedSlotResponse)
    }

    @ParameterizedTest
    @CsvSource(
        "10:00, 11:30, 2034-10-12, test1@gmail.com",
        "11:00, 12:30, 2034-11-24, test2Gmail.com",
        "12:00, 13:30, 2034-12-12, test1@gmail.com",
        "13:00, 14:30, 2034-09-11, test2@gmail.com"
    )
    fun `should return failure response for create slot`(
        fromTest: String,
        toTest: String,
        dateTest: String,
        emailTest: String
    ) {
        // GIVEN
        val createSlotRequest = CreateSlotRequest.newBuilder().apply {

            val createSlotRequest = CreateSlotRequest.newBuilder().apply {
                slotProtoBuilder.apply {
                    from = fromTest
                    to = toTest
                    date = dateTest
                }
                email = emailTest
            }.build()
        }

        val expectedSlotResponse = CreateSlotResponse.newBuilder().apply {
            failureBuilder.apply {
                errorMessage = "unrecognized error"
                errorCode = "error"
            }
        }.build()

        // WHEN
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }
}
