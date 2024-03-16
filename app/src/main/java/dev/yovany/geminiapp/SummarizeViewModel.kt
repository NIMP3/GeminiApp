package dev.yovany.geminiapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SummarizeViewModel(
        private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<SummarizeUiState> =
            MutableStateFlow(SummarizeUiState.Initial)
    val uiState: StateFlow<SummarizeUiState> =
            _uiState.asStateFlow()

    fun summarize(inputText: String) {
        _uiState.value = SummarizeUiState.Loading

        val prompt = """Asume el papel de un API y necesito que retornes la respuesta utilizando un formato JSON de la siguiente manera:
            [
                {
                    'question': '',
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
            el lenguaje a utilizar es Ingles, el numero de respuestas debe estar entre 3 a 5 respuestas por pregunta, 
            si por alguna razón decides utilizar comillas dobles o sencillas dentro
            de otras, ejemplo "present continouns from "to read"" cambia las comillas internas para evitar errores de
            lectura de la siguiente manera "present continouns from 'to read'" o viceversa 'present continouns from "to read"'
            """.trimMargin()

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = SummarizeUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = SummarizeUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}