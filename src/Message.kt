import java.time.LocalDateTime

data class Message (
    val sender: String,
    val content: String,
    val timestamp: LocalDateTime
)