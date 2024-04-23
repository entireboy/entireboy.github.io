import java.io.File
import kotlin.system.exitProcess


fun printUsage() {
    println("Usage: kotlinc -script generateTags.kts name [title] [site]")
    println("  kotlinc -script generateTags.kts kotlin")
    println("  kotlinc -script generateTags.kts kotlin Kotlin")
    println("  kotlinc -script generateTags.kts kotlin Kotlin https://kotlinlang.org/")
}

fun getArguments(args: Array<String>): List<String?> {
    return when (args.size) {
        1 -> listOf(args[0], args[0], null)
        2 -> listOf(args[0], args[1], null)
        3 -> listOf(args[0], args[1], args[2])
        else -> {
            printUsage()
            exitProcess(0)
        }
    }
}

fun createTagFile(name: String?): File {
    require(name != null) { "name should not be null" }
    val tagFile = File("./_tags/$name.md")
    if (tagFile.exists()) {
        println("Tag($name) is already exists")
        exitProcess(0)
    }
    return tagFile
}

fun File.fillWith(name: String?, title: String?, site: String?) {
    this.writer(Charsets.UTF_8).use { writer ->
        writer.appendLine("---")
        writer.appendLine("name: ${name ?: ""}")
        writer.appendLine("title: ${title ?: ""}")
        if (site != null) {
            writer.appendLine("site: $site")
        }
        writer.appendLine("---")
    }
    
    println("Tag($name) created")
}




if (args.isEmpty()) {
    printUsage()
    exitProcess(0)
}
val (name, title, site) = getArguments(args)
val tagFile = createTagFile(name)
tagFile.fillWith(name, title, site)
