import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DatabaseManager {
    // Define the formatter and chatFile as properties of the singleton
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val chatFile: File = File("chat_history.txt")

    init {
        // Check if file exists, if not, create it
        if (!chatFile.exists()) {
            chatFile.createNewFile()
        }
        // This block will be executed when the singleton is first accessed
        println("DatabaseManager initialized")
    }

    // Simulate a connection property
    private var isConnected: Boolean = false

    // Function to initialize database connection
    fun connect() {
        if (!isConnected) {
            println("Connecting to database...")
            isConnected = true
        } else {
            println("Already connected to the database.")
        }
    }

    // Function to close the connection
    fun disconnect() {
        if (isConnected) {
            println("Disconnecting from database...")
            isConnected = false
        } else {
            println("Database connection is already closed.")
        }
    }

//    // Example function to query the database
//    fun executeQuery(query: String) {
//        if (isConnected) {
//            println("Executing query: $query")
//            // Add your database query logic here
//        } else {
//            println("Cannot execute query. No database connection.")
//        }
//    }

    fun addMessage(sender: String, content: String) {
        // Get current timestamp
        val timestamp = LocalDateTime.now()

        // Create the message
        val message = Message(sender, content, timestamp)

        // Format the message for file saving
        val formattedMessage = "${message.sender} at ${message.timestamp.format(formatter)}: ${message.content}\n"

        // Save the message to the file
        chatFile.appendText(formattedMessage)

        // Print the updated chat history
        println("\nChat History:")
        println(chatFile.readText())  // Read and print the entire chat file content
        println("\n---\n")
    }
}