package com.example.kotlin_trsp_dictionary_app.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_trsp_dictionary_app.R
import com.example.kotlin_trsp_dictionary_app.ui.theme.Kotlin_trsp_dictionary_appTheme
import kotlinx.coroutines.delay

// Splash screen: app logo + name, 2 sec pore login e jabe.
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 2 sec wait kore next screen e pathao
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    SplashContent(modifier)
}

@Composable
fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.trsp_red)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo circle: "T" letter
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("T", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.trsp_red))
            }

            Spacer(Modifier.height(16.dp))

            Text("TRSP Scientific", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Dictionary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))
            Text(
                "Understand Science, One Term at a Time",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(32.dp))

            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Kotlin_trsp_dictionary_appTheme {
        SplashContent()
    }
}
