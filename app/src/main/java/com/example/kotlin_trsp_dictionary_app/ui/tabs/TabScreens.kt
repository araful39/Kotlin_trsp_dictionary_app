package com.example.kotlin_trsp_dictionary_app.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Beginner: baki 4 ta tab ekhon simple text. Pore ekhane asol UI bosbe.

// 1 line er helper - majhkhane text dekhay
@Composable
fun SimpleTab(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("$title\n$subtitle", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SubjectsScreen(modifier: Modifier = Modifier) {
    SimpleTab("Subjects", "6 ta subject ekhane asbe", modifier)
}

@Composable
fun SavedScreen(modifier: Modifier = Modifier) {
    SimpleTab("Saved", "Save kora word gulo ekhane asbe", modifier)
}

@Composable
fun LearnScreen(modifier: Modifier = Modifier) {
    SimpleTab("Learn", "Quiz / Learn ekhane asbe", modifier)
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    SimpleTab("Profile", "User profile ekhane asbe", modifier)
}
