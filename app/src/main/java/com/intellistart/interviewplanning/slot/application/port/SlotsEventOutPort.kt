package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.slot.domain.model.Slot

interface SlotsEventOutPort {

    fun publishEvent(slot: Slot, id: String)
}
