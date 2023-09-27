package intellistart.interviewplanning.model.week

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Entity for week.
 */
@Entity
@Table(name = "weeks")
data class Week(
    @Id
    @Column(name = "week_id")
    @JsonProperty("weekNum")
    var id: Long = 0,

    @OneToMany(mappedBy = "week", fetch = FetchType.EAGER)
    @JsonIgnore
    var interviewerSlots: MutableSet<InterviewerSlot> = HashSet(),
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Week

        if (id != other.id) return false
        if (interviewerSlots != other.interviewerSlots) return false

        return true
    }
}
