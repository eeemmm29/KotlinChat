import java.io.File
import java.time.LocalDateTime

fun main() {

    DatabaseManager.connect()

    while (true) {
        print("Enter your name: ")
        val sender = readLine() ?: "Unknown"

        print("Enter your message: ")
        val content = readLine() ?: ""

        DatabaseManager.addMessage(sender, content)
    }
}