package com.ravixyz.taskmanager.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime

@Serializable
enum class TaskStatus{
    PENDING,
    DONE
}

@Serializable
data class Task(
    val id: UInt = 0u,
    val title: String,
    val description: String,
    @Serializable(with = LocalTimeSerializer::class)
    val fromTime: LocalTime,
    @Serializable(with = LocalTimeSerializer::class)
    val toTime: LocalTime,
    val status: TaskStatus = TaskStatus.PENDING,
    val report: String = "",
    val attachments: List<String> = emptyList()
)

object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor =
        PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString())
    }
}

fun getDefaultTask(): Task{
    return Task(
        title = "Title",
        description = "description",
        fromTime = LocalTime.now(),
        toTime = LocalTime.now().plusHours(1L)
    )
}
