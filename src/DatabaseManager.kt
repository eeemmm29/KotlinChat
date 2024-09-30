import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DatabaseManager {
    private const val DATABASE_URL = "jdbc:sqlite:chat.db"
    private var connection: Connection? = null
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        connect()
        initialize()
        // This block will be executed when the singleton is first accessed
        println("DatabaseManager initialized")
    }

    // Connect to the SQLite database
    private fun connect() {
        connection = DriverManager.getConnection(DATABASE_URL)
    }

    // Create the tables if they don't exist
    private fun initialize() {
        val statement: Statement? = connection?.createStatement()

        // Create the users table
        statement?.executeUpdate(
            """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL
        )
        """
        )

        // Create the messages table
        statement?.executeUpdate(
            """
        CREATE TABLE IF NOT EXISTS messages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            sender_id INTEGER NOT NULL,
            content TEXT NOT NULL,
            timestamp TEXT NOT NULL,
            FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
        )
        """
        )

        statement?.close()
    }

    // Function to close the connection
    fun disconnect() {
        if (connection != null) {
            connection?.close()
            println("Database connection closed.")
        }
    }

    private fun userExists(username: String?): Boolean {
        if (username.isNullOrEmpty()) println("The username can't be empty.")

        val query = "SELECT COUNT(*) FROM users WHERE username = ?"
        val preparedStatement = connection?.prepareStatement(query)
        preparedStatement?.setString(1, username)

        val resultSet = preparedStatement?.executeQuery()
        var exists = false

        if (resultSet?.next() == true) {
            exists = resultSet.getInt(1) > 0
        }

        resultSet?.close()
        preparedStatement?.close()

        return exists
    }

    fun addUser(username: String?) {
        // Check if the user already exists
        if (userExists(username)) {
            println("User '$username' already exists. Choose a different username.")
            return
        }

        // Prepare the SQL insert statement
        val query = "INSERT INTO users (username) VALUES (?)"
        val preparedStatement = connection?.prepareStatement(query)

        // Set the parameter for the prepared statement
        preparedStatement?.setString(1, username)

        // Execute the update
        preparedStatement?.executeUpdate()

        // Close the prepared statement
        preparedStatement?.close()

        // Print a confirmation message
        println("User added: $username")
    }

    private fun getUserId(username: String): Int? {
        // Prepare the SQL select statement
        val query = "SELECT id FROM users WHERE username = ?"
        val preparedStatement = connection?.prepareStatement(query)

        // Set the parameter for the prepared statement
        preparedStatement?.setString(1, username)

        // Execute the query
        val resultSet = preparedStatement?.executeQuery()

        // Check if a user was found
        var userId: Int? = null
        if (resultSet?.next() == true) {
            userId = resultSet.getInt("id")  // Get the user ID
        } else {
            println("User '$username' not found.")
        }

        // Close resources
        resultSet?.close()
        preparedStatement?.close()

        return userId
    }

    fun getUser(username: String?) {
        // Prepare the SQL select statement
        val query = "SELECT * FROM users WHERE username = ?"
        val preparedStatement = connection?.prepareStatement(query)

        // Set the parameter for the prepared statement
        preparedStatement?.setString(1, username)

        // Execute the query
        val resultSet = preparedStatement?.executeQuery()

        // Check if a user was found
        if (resultSet?.next() == true) {
            // Iterate over the result set and print details
            val columnCount = resultSet.metaData.columnCount

            println("User details for '$username':")
            for (i in 1..columnCount) {
                val columnName = resultSet.metaData.getColumnName(i)
                val value = resultSet.getObject(i)  // Use getObject to handle any data type
                println("$columnName: $value")
            }
        } else {
            println("User '$username' not found.")
        }

        // Close resources
        resultSet?.close()
        preparedStatement?.close()
    }

    fun getAllUsers() {
        // Prepare the SQL select statement to get all users
        val query = "SELECT username FROM users"
        val statement = connection?.createStatement()

        // Execute the query
        val resultSet = statement?.executeQuery(query)

        // Check if any users were found
        if (resultSet?.next() != true) {
            println("No users found.")
        } else {
            println("Info of all users:")
            // Iterate over the result set and print user info
            do {
                val username = resultSet.getString("username")
                // Call the getUser function to print details for each user
                getUser(username)
                println("-----")
            } while (resultSet.next())
        }

        // Close resources
        resultSet?.close()
        statement?.close()
    }


    // Function to add a message to the database
    fun addMessage(senderUsername: String, content: String) {
        // Get sender ID from username
        val senderId = getUserId(senderUsername)

        if (senderId == null) return

        // Prepare the SQL insert statement
        val query = "INSERT INTO messages (sender_id, content, timestamp) VALUES (?, ?, ?)"
        val preparedStatement = connection?.prepareStatement(query)

        // Get the current timestamp formatted as a string
        val timestamp = LocalDateTime.now().format(formatter)

        // Set the parameters for the prepared statement
        preparedStatement?.setInt(1, senderId)
        preparedStatement?.setString(2, content)
        preparedStatement?.setString(3, timestamp)

        // Execute the update
        preparedStatement?.executeUpdate()

        // Close the prepared statement
        preparedStatement?.close()

        // Print a confirmation message
        println("Message added: [$timestamp] $senderUsername: $content")
    }

    // Function to retrieve all messages
    fun getMessages() {
        // Prepare the SQL select statement
        val query = """
        SELECT m.timestamp, u.username, m.content 
        FROM messages m
        JOIN users u ON m.sender_id = u.id
        ORDER BY m.timestamp ASC
    """
        val statement = connection?.createStatement()

        // Execute the query
        val resultSet = statement?.executeQuery(query)

        // Check if any messages were found
        if (resultSet?.next() != true) {
            println("No messages found.")
        } else {
            println("\nChat History:")
            // Iterate over the result set and print messages
            do {
                val timestamp = resultSet.getString("timestamp")
                val username = resultSet.getString("username")
                val content = resultSet.getString("content")
                println("[$timestamp] $username: $content")
            } while (resultSet.next())
        }

        // Close resources
        resultSet?.close()
        statement?.close()
    }
}