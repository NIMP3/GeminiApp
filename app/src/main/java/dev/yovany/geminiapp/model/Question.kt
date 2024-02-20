package dev.yovany.geminiapp.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Question(
    val question: String,
    val answers: List<Answer>
) {
    companion object {
        fun getList(text: String): List<Question> {
            val gson = Gson()
            val listType = object : TypeToken<List<Question>>() {}.type
            val questions: List<Question> = gson.fromJson(text, listType)
            return questions
        }
    }
}

data class Answer (
    val answer: String,
    val isCorrect: Boolean = false
)
