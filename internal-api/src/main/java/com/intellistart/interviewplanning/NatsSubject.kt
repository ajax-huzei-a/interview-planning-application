package com.intellistart.interviewplanning

object NatsSubject {

    private const val REQUEST_PREFIX = "intellistart.interviewplanning"

    fun createSlotEventNatsSubject(slotId: String, eventType: String): String =
        "$REQUEST_PREFIX.slot.$slotId.$eventType"

    object Slot {
        const val CREATE = "$REQUEST_PREFIX.slot.create"
        const val UPDATE = "$REQUEST_PREFIX.slot.update"
        const val GET_ALL = "$REQUEST_PREFIX.slot.get_all"
    }
}
