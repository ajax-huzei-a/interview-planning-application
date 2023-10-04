package intellistart.interviewplanning.model.user

import intellistart.interviewplanning.model.slot.Slot

sealed class User {

    var id: Long = 0

    var email: String = ""

    var role: Role = Role.CANDIDATE

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (role != other.role) return false

        return true
    }
}

class Coordinator : User()

class Interviewer : User() {
    var interviewerSlots: MutableSet<Slot> = HashSet()
}

class Candidate : User() {
    var candidateSlots: MutableSet<Slot> = HashSet()
}
