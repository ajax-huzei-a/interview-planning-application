package com.intellistart.interviewplanning.slot.application.port

import com.intellistart.interviewplanning.slot.domain.model.Slot

interface SlotKafkaProducerOutPort {

    fun produceNotification(slot: Slot, id: String)
}
