package intellistart.interviewplanning.model.period

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document
data class Period(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate = LocalDate.now(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val from: LocalTime = LocalTime.now(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val to: LocalTime = LocalTime.now()

)
