package intellistart.interviewplanning.model.week

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.FetchType

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
    var interviewerSlots: MutableSet<InterviewerSlot> = HashSet()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Week) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
