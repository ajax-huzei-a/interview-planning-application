package intellistart.interviewplanning.model.user

import intellistart.interviewplanning.model.slot.Slot
import intellistart.interviewplanning.model.user.User.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = COLLECTION_NAME)
sealed class User(var role: Role) {

    @Id
    var id: ObjectId = ObjectId()

    var email: String = ""

    companion object {
        const val COLLECTION_NAME = "users"
    }
}

@Document(value = COLLECTION_NAME)
class Coordinator : User(Role.COORDINATOR)

@Document(value = COLLECTION_NAME)
class Interviewer : User(Role.INTERVIEWER) {
    var slots: List<Slot> = listOf()
}

@Document(value = COLLECTION_NAME)
class Candidate : User(Role.CANDIDATE) {
    var slots: List<Slot> = listOf()
}
