package dev.yovany.geminiapp

import android.content.Context
import java.io.IOException

class Utility {
    companion object {
        fun getJsonDataFromAsset(context: Context, fileName: String) : String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                return  null
            }

            return jsonString
        }
    }
}

fun String.removeMarkdown(): String {
    val pattern = Regex("""^```[a-zA-Z]*\n|```$""")
    return this.replace(pattern, "")
}