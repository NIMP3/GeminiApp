package dev.yovany.geminiapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dev.yovany.geminiapp.model.Question
import dev.yovany.geminiapp.model.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummarizeViewModel(
        private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _questions = MutableStateFlow<Resource<List<Question>>>(Resource.Empty(null))
    val questions: StateFlow<Resource<List<Question>>> = _questions

    fun summarize() {
        _questions.value = Resource.Loading()

        val prompt = """Asume el papel de un API y necesito que retornes la respuesta utilizando un formato JSON de la siguiente manera:
            [
                {
                    'question': '',
                    'type': '',
                    'answers' : [
                        {
                            'answer', '',
                            'isCorrect', false
                        },
                        {
                            'answer', '',
                            'isCorrect', true
                        },
                    ]
                } 
            ]
            
            siguiendo el anterior formato necesito que generes un listado de preguntas acerca del
            presente continuo en Ingles con sus respectivas respuestas y solo una que es correcta marcada como true.
            el type depende si el texto de las respuestas supera los 20 caracteres indica el tipo 'LONG' y si es menor 'SHORT'
            el lenguaje a utilizar es Ingles, el numero de respuestas debe estar entre 3 a 5 respuestas por pregunta, 
            si por alguna razón decides utilizar comillas dobles o sencillas dentro
            de otras, ejemplo "present continouns from "to read"" cambia las comillas internas para evitar errores de
            lectura de la siguiente manera "present continouns from 'to read'" o viceversa 'present continouns from "to read"'
            """.trimMargin()

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    Log.i("RESULT", outputContent)
                    val list = Question.getList(outputContent)
                    if (list.isNotEmpty()) _questions.value = Resource.Success(list)
                    else _questions.value= Resource.Error(null, "Error parsing data")
                }
            } catch (e: Exception) {
                _questions.value = Resource.Error(null, e.localizedMessage ?: "Error processing prompt")
            }
        }
    }
}