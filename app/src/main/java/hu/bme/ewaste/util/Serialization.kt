package hu.bme.ewaste.util

import hu.bme.ewaste.data.model.TrashCanType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object TrashCanTypeSerializer : KSerializer<TrashCanType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("type", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TrashCanType {
        return TrashCanType.valueOf(decoder.decodeString().uppercase())
    }

    override fun serialize(encoder: Encoder, value: TrashCanType) {
        encoder.encodeString(value.toString())
    }
}