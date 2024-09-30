import java.io.File

fun showHelp() {
    val filePath = "help.txt"
    val helpFile = File(filePath)
    if (!helpFile.exists()) {
        helpFile.createNewFile()
        println("File not found: $filePath")
        return
    }
    println(helpFile.readText())
}

fun addUser() {
    print("Enter the username: ")
    val username = readLine()
    DatabaseManager.addUser(username)
}

fun getUser() {
    print("Enter the username: ")
    val username = readLine()
    DatabaseManager.getUser(username)
}

fun getAllUsers() {
    DatabaseManager.getAllUsers()
}

fun addMessage() {
    print("Enter your name: ")
    val senderUsername = readlnOrNull() ?: "Unknown"

    print("Enter your message: ")
    val content = readLine() ?: ""

    DatabaseManager.addMessage(senderUsername, content)
}

fun getMessages() {
    // Retrieve and print all messages
    DatabaseManager.getMessages()
}

fun exit() {
    // Disconnect from the database when done
    DatabaseManager.disconnect()
}

fun main() {
    while (true) {
        println("What would you like to do?")
        val command = readLine()

        when (command) {
            "" -> continue
            "help" -> showHelp()
            "adduser" -> addUser()
            "getuser" -> getUser()
            "getallusers" -> getAllUsers()
            "add" -> addMessage()
            "getmessages" -> getMessages()
            "exit" -> {
                exit()
                break
            }
            else -> println("Invalid command")
        }
    }
}