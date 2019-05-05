package com.rubyhuntersky.indexrebellion.data.common

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.math.BigDecimal

@Serializer(forClass = BigDecimal::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {

    override val descriptor: SerialDescriptor = StringDescriptor.withName("java.math.BigDecimal")

    override fun serialize(encoder: Encoder, obj: BigDecimal) {
        encoder.encodeString(obj.toDouble().toString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}
