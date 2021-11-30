package hu.bme.ewaste.data.model

import java.util.*

data class DetectedObject(
    var detectionId: Long,
    var type: TrashCanType,
    var precision: Float
)

data class TrackedObject(
    var localId: UUID,
    var type: TrashCanType,
    var precision: Float,
    var detectionCount: Int
) {
    constructor(detectedObject: DetectedObject) : this(
        localId = UUID.randomUUID(),
        type = detectedObject.type,
        precision = detectedObject.precision,
        detectionCount = 0
    )
}

enum class TrashCanType {
    TRASH,
    PLASTIC,
    PAPER;

    override fun toString(): String {
        return super.toString().lowercase(Locale.getDefault())
    }
}
