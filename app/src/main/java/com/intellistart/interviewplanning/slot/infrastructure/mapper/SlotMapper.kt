package com.intellistart.interviewplanning.slot.infrastructure.mapper

import com.intellistart.interviewplanning.infrastructure.adapters.reopository.entity.toDomain
import com.intellistart.interviewplanning.infrastructure.adapters.reopository.entity.toEntity
import com.intellistart.interviewplanning.slot.domain.model.Slot
import com.intellistart.interviewplanning.slot.infrastructure.adapters.reopository.entity.SlotEntity
import org.bson.types.ObjectId

fun Slot.toEntity(): SlotEntity {
    return SlotEntity(
        id = ObjectId(this.id),
        period = this.period.toEntity(),
        bookings = this.bookings
    )
}

fun SlotEntity.toDomain(): Slot {
    return Slot(
        id = this.id.toHexString(),
        period = this.period.toDomain(),
        bookings = this.bookings
    )
}
