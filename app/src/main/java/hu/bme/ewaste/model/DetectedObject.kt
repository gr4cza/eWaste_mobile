package hu.bme.ewaste.model

import java.util.*

data class DetectedObject(
    var trackingId: Long,
    var type: TrashCanType,
    var precision: Float
)

enum class TrashCanType {
    TRASH,
    PLASTIC,
    PAPER;

    override fun toString(): String {
        return super.toString().lowercase(Locale.getDefault())
    }
}
