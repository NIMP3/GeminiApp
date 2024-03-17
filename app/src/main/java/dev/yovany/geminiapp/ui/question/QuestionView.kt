package dev.yovany.geminiapp.ui.question

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.yovany.geminiapp.SummarizeViewModel
import dev.yovany.geminiapp.model.Answer
import dev.yovany.geminiapp.model.Question
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yovany.geminiapp.model.Message
import dev.yovany.geminiapp.model.MessageType
import dev.yovany.geminiapp.model.Resource

@Composable
fun QuestionView(viewModel: SummarizeViewModel = viewModel()) {
    val data by viewModel.questions.collectAsState()
    var haveProcessedData by rememberSaveable { mutableStateOf(false) }
    var responses by rememberSaveable { mutableIntStateOf(0) }
    var currentQuestion by rememberSaveable { mutableStateOf(0) }
    var isResetButton by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (haveProcessedData) return@LaunchedEffect

        viewModel.summarize()
        haveProcessedData = true
    }

    when (data) {
        is Resource.Loading -> {
            MessageView(
                message = Message(
                    "LOADING",
                    "Processing prompt, please wait...",
                    MessageType.LOADING
                )
            )
        }

        is Resource.Error -> {
            MessageView(
                message = Message(
                    "ERROR",
                    "Error obtaining menu data: ${(data as Resource.Error).message}",
                    MessageType.ERROR
                )
            )
        }

        is Resource.Success -> {
            val questions = (data as Resource.Success).data!!

            Column(Modifier.fillMaxSize()) {
                QuestionHeader()
                QuestionProgress(responses, questions.size)
                QuestionBody(questions[currentQuestion], Modifier.weight(1f))
                Options(isResetButton) {
                    if (!isResetButton) {
                        responses++
                        if (currentQuestion < questions.size - 1) currentQuestion++
                        else isResetButton = true
                    } else {
                        viewModel.summarize()
                        haveProcessedData = true
                        responses = 0
                        currentQuestion = 0
                        isResetButton = false
                    }
                }
            }
        }

        else -> {}
    }

}

@Composable
fun QuestionHeader() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier.align(Alignment.TopStart)
        )

        Text(
            text = "Questions",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 24.dp)
        )

    }
}

@Composable
fun QuestionProgress(responses: Int, questions: Int) {
    val progress = responses.toFloat() / questions.toFloat()

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(text = "$responses / $questions", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(50)),
            progress = { progress }
        )
    }
}

@Composable
fun QuestionBody(question: Question, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = question.question,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp)
        )

        if (question.type == "SHORT")
            AnswersGrid(question.answers, Modifier.weight(1f))
        else
            AnswersList(question.answers, modifier = Modifier.weight(1f))
    }
}

@Composable
fun AnswersGrid(answers: List<Answer>, modifier: Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        items(answers) { answer ->
            AnswerCard(answer, Modifier.height(140.dp))
        }
    }
}

@Composable
fun AnswersList(answers: List<Answer>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        items(answers) { answer ->
            AnswerCard(answer)
        }
    }
}

@Composable
fun Options(isResetButton: Boolean, onClick: () -> Unit) {
    val text = if (!isResetButton) "CONTINUAR" else "REINICIAR"
    val color = if (!isResetButton) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Button(
            onClick = { onClick() },
            shape = RoundedCornerShape(20),
            colors = ButtonColors(
                containerColor = color,
                contentColor = Color.White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun AnswerCard(answer: Answer, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(15),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.tertiary)
                .padding(14.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = answer.answer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuestionViewPreview() {
    QuestionView()
}