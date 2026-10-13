package com.example.kotlin_trsp_dictionary_app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_trsp_dictionary_app.R
import com.example.kotlin_trsp_dictionary_app.data.model.Category
import com.example.kotlin_trsp_dictionary_app.data.model.TermOfDay
import com.example.kotlin_trsp_dictionary_app.di.AppViewModelFactory
import com.example.kotlin_trsp_dictionary_app.ui.theme.Kotlin_trsp_dictionary_appTheme

// 4. VIEW (V in MVVM) - sudhu UI dekhabe, logic ViewModel e.
// Jetpack Compose + MVVM. Bottom bar Navigation e thake, tai ekhane Scaffold nai.

@Composable
fun HomeScreen(
    vm: HomeViewModel = viewModel(factory = AppViewModelFactory), // ViewModel connect (DI)
    modifier: Modifier = Modifier
) {
    val state by vm.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onSearchChange = vm::onSearchChange,
        onSearchGo = vm::onSearchGo,
        onClearRecent = vm::onClearRecent,
        onRecentClick = vm::onRecentClick,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onSearchChange: (String) -> Unit,
    onSearchGo: () -> Unit,
    onClearRecent: () -> Unit,
    onRecentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State theke 4 ta value local variable e nilam - niche same nam e use hobe.
    val searchText = state.searchText
    val categories = state.categories
    val recentList = state.recentList
    val termOfDay = state.termOfDay

    // Scrollable full page
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // ---- 1. Title ----
        Text("TRSP Scientific", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Dictionary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.trsp_red))
        Text("Understand Science, One Term at a Time", fontSize = 13.sp, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        // ---- 2. Search box ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Left: text field (bg_search_bar.xml er moto gol box)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colorResource(R.color.trsp_search_bg))
                    .border(1.dp, colorResource(R.color.trsp_search_stroke), RoundedCornerShape(16.dp))
            ) {
                TextField(
                    value = searchText,
                    onValueChange = onSearchChange, // type korle ViewModel e jabe
                    placeholder = { Text("Search \"Photosynthesis\"...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.width(8.dp))

            // Right: red go button (bg_search_button.xml)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorResource(R.color.trsp_red))
                    .clickable { onSearchGo() }, // click -> ViewModel
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.ic_arrow_right), contentDescription = "Go", modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- 3. Categories (6 ta, 2 line e 3 ta kore) ----
        Text("QUICK CATEGORIES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        // Prothom 3 ta
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until 3) {
                if (i < categories.size) {
                    CategoryBox(name = categories[i].title, emoji = categories[i].emoji, colorId = i, modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Porer 3 ta
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 3 until 6) {
                if (i < categories.size) {
                    CategoryBox(name = categories[i].title, emoji = categories[i].emoji, colorId = i, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- 4. Recent searches ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("RECENT SEARCHES", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = { onClearRecent() }) {
                Text("Clear", color = Color.Gray, fontSize = 12.sp)
            }
        }

        // Chip gulo simple Row e (2 line)
        if (recentList.isEmpty()) {
            Text("No recent searches yet.", color = Color.Gray, fontSize = 13.sp)
        } else {
            // Line 1: prothom 3 ta
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (word in recentList.take(3)) {
                    RecentChip(word = word, onClick = { onRecentClick(word) })
                }
            }
            Spacer(Modifier.height(8.dp))
            // Line 2: baki gulo
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (word in recentList.drop(3).take(3)) {
                    RecentChip(word = word, onClick = { onRecentClick(word) })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- 5. Term of the day (bg_term_card.xml) ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(colorResource(R.color.trsp_term_card))
                .padding(16.dp)
        ) {
            Column {
                Text("SCIENTIFIC TERM OF THE DAY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(termOfDay.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(termOfDay.description, color = Color(0xFFD1D5DB), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onRecentClick(termOfDay.title) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.trsp_red))
                ) {
                    Text("Learn More")
                }
            }
        }
    }
}

// Choto box: emoji + name
@Composable
fun CategoryBox(name: String, emoji: String, colorId: Int, modifier: Modifier = Modifier) {
    // 6 ta alada halka color (drawable bg_category_* er sathe milano)
    val bg = when (colorId) {
        0 -> colorResource(R.color.cat_physics_bg)
        1 -> colorResource(R.color.cat_chemistry_bg)
        2 -> colorResource(R.color.cat_biology_bg)
        3 -> colorResource(R.color.cat_astronomy_bg)
        4 -> colorResource(R.color.cat_math_bg)
        else -> colorResource(R.color.cat_compsci_bg)
    }
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Text(name, fontSize = 12.sp)
        }
    }
}

// Choto pill button (bg_recent_chip.xml er moto)
@Composable
fun RecentChip(word: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .border(1.dp, colorResource(R.color.trsp_chip_stroke), RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(word, fontSize = 13.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Kotlin_trsp_dictionary_appTheme {
        HomeScreenContent(
            state = HomeUiState(
                searchText = "",
                categories = listOf(
                    Category("physics", "Physics", "🚀"),
                    Category("chemistry", "Chemistry", "⚗️"),
                    Category("biology", "Biology", "🌿"),
                    Category("astronomy", "Astronomy", "🌙"),
                    Category("math", "Math", "📐"),
                    Category("compsci", "Comp. Sci", "💻")
                ),
                recentList = listOf("Isotope", "Photosynthesis", "Entropy", "Velocity"),
                termOfDay = TermOfDay("Entropy", "A measure related to the number of possible arrangements of a system.")
            ),
            onSearchChange = {},
            onSearchGo = {},
            onClearRecent = {},
            onRecentClick = {}
        )
    }
}
