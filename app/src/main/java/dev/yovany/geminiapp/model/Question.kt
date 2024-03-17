package dev.yovany.geminiapp.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.yovany.geminiapp.Utility
import dev.yovany.geminiapp.removeMarkdown
import java.lang.Exception

data class Question(
    val question: String,
    val type: String,
    val answers: List<Answer>
) {
    companion object {
        fun getList(text: String): List<Question> {
            val jsonString = text.removeMarkdown()
            val gson = Gson()
            try {
                val listType = object : TypeToken<List<Question>>() {}.type
                val questions: List<Question> = gson.fromJson(jsonString, listType)
                return questions
            } catch (e: Exception) {
                return emptyList()
            }
        }

        fun getListFromAssets(context: Context): List<Question> {
            val jsonFileString = Utility.getJsonDataFromAsset(context,"questions.json")
            return try {
                val listType = object : TypeToken<List<Question>>() {}.type
                val questions: List<Question> = Gson().fromJson(jsonFileString, listType)
                questions
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

data class Answer (
    val answer: String,
    val isCorrect: Boolean = false
)