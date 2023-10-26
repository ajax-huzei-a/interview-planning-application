package intellistart.interviewplanning

object NatsSubject {
    private const val REQUEST_PREFIX = "intellistart.interviewplanning"

    object Slot {
        const val CREATE = "$REQUEST_PREFIX.slot.create"
        const val UPDATE = "$REQUEST_PREFIX.slot.update"
        const val GET_ALL = "$REQUEST_PREFIX.slot.get_all"
    }
}
