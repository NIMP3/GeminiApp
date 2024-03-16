package dev.yovany.geminiapp.ui.question

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import dev.yovany.geminiapp.model.Answer
import dev.yovany.geminiapp.model.Question

@Composable
fun QuestionView() {
    val questions = Question.getListFromAssets(LocalContext.current)
    var responses by rememberSaveable { mutableIntStateOf(2) }

    Column(Modifier.fillMaxSize()) {
        QuestionHeader()
        QuestionProgress(responses, questions.size)
        QuestionBody(questions[responses])
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
            progress = progress
        )
    }
}

@Composable
fun QuestionBody(question: Question) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp)
    ) {
        Text(
            text = question.question,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp)
        )

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(question.answers) { answer ->
                AnswerCard(answer)
            }
        }
    }
}

@Composable
fun AnswerCard(answer: Answer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(15),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        )
    ) {
        Box(Modifier.fillMaxSize().padding(14.dp), contentAlignment = Alignment.Center) {
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