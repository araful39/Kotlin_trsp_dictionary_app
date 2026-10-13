# TRSP Scientific Dictionary App — Kotlin + Jetpack Compose (Full Code + YouTube Script বাংলায়)

> এক ভিডিওতে MVVM + Compose + Navigation + **Login API (Retrofit)** — Beginner to Intermediate

এই README পড়লেই তুমি প্রজেক্টটা **নিজে বানাতে পারবে + YouTube এ শেখাতে পারবে**। প্রতিটা ফাইলের Full Code + লাইন বাই লাইন বাংলা ব্যাখ্যা + মুখে কী বলবে সব দেওয়া আছে। **Section 11 এ Login API কীভাবে সম্পূর্ণ হলো — সেই পুরো গল্প আছে:** server কী পায়, app কী পাঠায়, error কোথায় আসে।

---
## 0. Demo — App এ কী কী আছে?

```
[App চালু] → Splash (লাল T logo) → ২ সেকেন্ড
        ↓
LOGIN SCREEN  ← Section 11 (Login API)
TRSP Scientific Dictionary
Welcome back 👋 / Login to continue learning
[ Email: you@example.com       ]
[ Password: ••••••••   Show    ]
Forgot password?
[ ============ Login =========== ]  ← চাপলে spinner → server call
New here? Create account
Skip for now →
        ↓ success (dialog OK)
TRSP Scientific
Dictionary (লাল রঙে)
Understand Science, One Term at a Time (gray subtitle)

[ Search "Photosynthesis"... ] [→ লাল Button]

QUICK CATEGORIES
[🚀 Physics] [⚗️ Chemistry] [🌿 Biology]
[🌙 Astronomy] [📐 Math] [💻 Comp.Sci]

RECENT SEARCHES              [Clear]
(Isotope) (Photosynthesis) (Entropy)
(Velocity)

[SCIENTIFIC TERM OF THE DAY - কালো কার্ড]
Entropy
A measure related to the number of possible arrangements...
[Learn More]

BottomBar: Home | Subjects | Saved | Learn | Profile
```

Reference Image: `app/src/main/res/drawable/home_screen.png`

---
## 1. Tech Stack + Version (মুখস্থ রাখো, ভিডিওতে বলতে হবে)

`gradle/libs.versions.toml` থেকে:

```toml
[versions]
agp = "9.2.1"
kotlin = "2.2.10"
composeBom = "2026.02.01"
navigationCompose = "2.9.3"
activityCompose = "1.13.0"
coreKtx = "1.19.1"
lifecycleRuntimeKtx = "2.11.0"
lifecycleViewmodelCompose = "2.11.0"
retrofit = "2.11.0"                 // Login API এর জন্য

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

`app/build.gradle.kts` — Full Code:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.kotlin_trsp_dictionary_app"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        applicationId = "com.example.kotlin_trsp_dictionary_app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { compose = true }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)                    // Login API
    implementation(libs.retrofit.converter.gson)     // JSON → Kotlin object
}
```

**ভিডিওতে এভাবে বলো:**
> "দেখো বন্ধুরা, Compose BOM `2026.02.01` use করছি — মানে সব Compose library version একসাথে মিলে যাবে, আলাদা version লিখতে হবে না। `navigation-compose 2.9.3` — এটা ছাড়া BottomBar হবে না। `viewmodel-compose` — এটা ছাড়া `viewModel()` function পাবে না। minSdk 24 মানে Android 7.0 থেকে চলবে, 95% ফোন কভার হবে। Java 11 must, না হলে Compose compile হবে না।"

---
## 2. Project Structure — MVVM সহজে

```
app/src/main/
├── AndroidManifest.xml                     // INTERNET + usesCleartextTraffic
├── java/com/example/kotlin_trsp_dictionary_app/
│   ├── MainActivity.kt
│   ├── data/model/HomeModels.kt            // MODEL (home)
│   ├── data/model/AuthModels.kt            // MODEL (login request/response)
│   ├── data/network/AuthApiService.kt      // API interface (@POST login)
│   ├── data/network/RetrofitClient.kt      // Retrofit singleton (BASE_URL)
│   ├── data/repository/HomeRepository.kt   // REPOSITORY (home)
│   ├── data/repository/AuthRepository.kt   // REPOSITORY (login → Result<>)
│   ├── ui/splash/SplashScreen.kt
│   ├── ui/auth/LoginScreen.kt              // VIEW (login)
│   ├── ui/auth/LoginViewModel.kt           // VIEWMODEL (validate + API call)
│   ├── ui/auth/RegisterScreen.kt
│   ├── ui/auth/RegisterViewModel.kt
│   ├── ui/home/HomeViewModel.kt            // VIEWMODEL (home)
│   ├── ui/home/HomeScreen.kt               // VIEW (home)
│   ├── ui/navigation/AppNav.kt             // NAVIGATION (splash→login→main→5 tabs)
│   ├── ui/tabs/TabScreens.kt
│   └── ui/theme/Color.kt, Theme.kt, Type.kt
└── res/
    ├── values/colors.xml
    ├── drawable/bg_*.xml (shape), ic_*.xml (vector)
```

মনে রাখার ট্রিক (ভিডিওতে বলো):
> "M = Model — শুধু data class, কোনো logic নাই। R = Repository — দোকানদার, data supply দেয়। VM = ViewModel — মস্তিষ্ক, সব state আর function। V = View — চোখ, শুধু দেখায়। UI কখনো সরাসরি data বানাবে না, সবসময় VM কে জিজ্ঞেস করবে।"

Login এও একই রাস্তা: **LoginScreen → LoginViewModel → AuthRepository → AuthApiService → Server** (Section 11 দেখো)।

---
## 3. FILE 1: colors.xml — Design এর প্রাণ

`app/src/main/res/values/colors.xml` Full Code:

```xml
<resources>
    <color name="trsp_red">#C8102E</color>
    <color name="trsp_red_dark">#A50D26</color>
    <color name="trsp_search_bg">#F3F4F6</color>
    <color name="trsp_search_stroke">#E5E7EB</color>
    <color name="trsp_chip_stroke">#E5E7EB</color>
    <color name="trsp_term_card">#121A2C</color>

    <color name="cat_physics_bg">#EAF2FF</color>
    <color name="cat_chemistry_bg">#FCE7F3</color>
    <color name="cat_biology_bg">#DCFCE7</color>
    <color name="cat_astronomy_bg">#EDE9FE</color>
    <color name="cat_math_bg">#FFEDD5</color>
    <color name="cat_compsci_bg">#CFFAFE</color>
</resources>
```

ব্যাখ্যা (ভিডিওতে বলো):
- `trsp_red #C8102E` — Go button, Dictionary title, selected tab। এই একটা color brand বানায়।
- `trsp_search_bg #F3F4F6` — search box এর হালকা gray, যাতে white background থেকে আলাদা লাগে।
- `trsp_term_card #121A2C` — Term of the Day কার্ডের গাঢ় navy, white text ফুটে ওঠে।
- 6টা `cat_*_bg` — 6টা category তে 6টা প্যাস্টেল color, চোখে আরাম লাগে।

Compose এ ব্যবহার:
```kotlin
colorResource(R.color.trsp_red) // XML color কে Compose Color বানায়
```

---
## 4. FILE 2: drawable — shape vs vector

### 4.1 Search Bar Background — `bg_search_bar.xml`
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/trsp_search_bg" />
    <corners android:radius="16dp" />
    <stroke android:width="1dp" android:color="@color/trsp_search_stroke" />
</shape>
```
মানে: আয়তক্ষেত্র, ভেতরে হালকা gray, corner 16dp গোল, চারপাশে 1dp হালকা border।

### 4.2 Go Button — `bg_search_button.xml`
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/trsp_red" />
    <corners android:radius="14dp" />
</shape>
```
মানে: লাল ভরাট, 14dp গোল। Compose এ আমরা এটা `background(colorResource(R.color.trsp_red)) + clip(RoundedCornerShape(14.dp))` দিয়ে করেছি — একই দেখতে।

### 4.3 Term Card — `bg_term_card.xml`
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/trsp_term_card" />
    <corners android:radius="18dp" />
</shape>
```

### 4.4 Category — `bg_category_physics.xml` (বাকি 5টাও same, শুধু color আলাদা)
```xml
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/cat_physics_bg" />
    <corners android:radius="16dp" />
</shape>
```

### 4.5 Bottom Icon — `ic_nav_home.xml` (vector)
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FF000000"
        android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z" />
</vector>
```
`pathData` হলো বাড়ির icon এর drawing path। বাকিগুলো `ic_nav_subjects.xml`, `ic_nav_saved.xml`, `ic_nav_learn.xml`, `ic_nav_profile.xml` একইভাবে। Compose এ `ColorFilter.tint()` দিয়ে লাল/gray করি।

**ভিডিওতে বলো:** "XML shape মানে ডিজাইন কোড দিয়ে আঁকা — PNG লাগে না, সাইজ ছোট, সব স্ক্রিনে sharp থাকে।"

---
## 5. FILE 3: HomeModels.kt — MODEL (শুধু data)

Full Code `data/model/HomeModels.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.model
import com.example.kotlin_trsp_dictionary_app.R

data class Category(
    val id: String,      // "physics" — click করলে কোন page যাবে বুঝতে
    val title: String,   // "Physics" — UI তে দেখাবে
    val emoji: String    // "🚀" — beginner friendly icon, vector লাগে না
)

data class TermOfDay(
    val title: String = "Entropy",
    val description: String = "A measure related to the number of possible arrangements of a system."
)

enum class BottomTab(val route: String, val label: String, val iconRes: Int) {
    HOME("home", "Home", R.drawable.ic_nav_home),
    SUBJECTS("subjects", "Subjects", R.drawable.ic_nav_subjects),
    SAVED("saved", "Saved", R.drawable.ic_nav_saved),
    LEARN("learn", "Learn", R.drawable.ic_nav_learn),
    PROFILE("profile", "Profile", R.drawable.ic_nav_profile)
}
```

লাইন বাই লাইন:
1. `data class` — Kotlin এর special class, `equals/hashCode/toString` auto বানায়। শুধু data রাখার জন্য।
2. `Category` এ emoji String রেখেছি যাতে beginner vector icon এর ঝামেলা ছাড়াই সুন্দর UI পায়। পরে চাইলে `iconRes: Int` করতে পারো।
3. `TermOfDay` এ default value দিয়েছি — `TermOfDay()` লিখলেই Entropy পাবে, crash হবে না।
4. `enum class BottomTab` — 5টা fixed tab। `route` হলো navigation ঠিকানা ("home"), `label` হলো নিচে লেখা ("Home"), `iconRes` হলো drawable ID। `BottomTab.entries` দিয়ে loop করা যায় — 5 বার কোড লিখতে হয় না।

**ভিডিওতে বলো:** "Model এ কখনো function, API call, UI code লিখবে না। Model শুধু খাঁচা — কেমন data থাকবে সেটা বলে।"

---
## 6. FILE 4: HomeRepository.kt — REPOSITORY (দোকানদার)

Full Code `data/repository/HomeRepository.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.repository
import com.example.kotlin_trsp_dictionary_app.data.model.Category
import com.example.kotlin_trsp_dictionary_app.data.model.TermOfDay

class HomeRepository {
    fun getCategories(): List<Category> {
        return listOf(
            Category("physics", "Physics", "🚀"),
            Category("chemistry", "Chemistry", "⚗️"),
            Category("biology", "Biology", "🌿"),
            Category("astronomy", "Astronomy", "🌙"),
            Category("math", "Math", "📐"),
            Category("compsci", "Comp. Sci", "💻")
        )
    }
    fun getRecentSearches(): List<String> {
        return listOf("Isotope", "Photosynthesis", "Entropy", "Velocity")
    }
    fun getTermOfDay(): TermOfDay {
        return TermOfDay() // default Entropy
    }
}
```

ব্যাখ্যা:
- Repository হলো single source of truth। ViewModel সরাসরি list বানায় না, Repository কে জিজ্ঞেস করে।
- এখন static `listOf()` দিয়েছি। Part-2 তে এখানেই `Room DAO` বা `Retrofit API` বসবে — ViewModel / UI change লাগবে না।
- Interview তে জিজ্ঞেস করে "Repository কেন লাগে?" — উত্তর: "যাতে data কোথা থেকে আসছে (DB/API/static) UI না জানে, testing সহজ হয়।"

---
## 7. FILE 5: HomeViewModel.kt — মস্তিষ্ক (সবচেয়ে Important)

Full Code `ui/home/HomeViewModel.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.ui.home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kotlin_trsp_dictionary_app.data.model.Category
import com.example.kotlin_trsp_dictionary_app.data.model.TermOfDay
import com.example.kotlin_trsp_dictionary_app.data.repository.HomeRepository

class HomeViewModel : ViewModel() {
    private val repo = HomeRepository()

    var searchText by mutableStateOf("")
        private set
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set
    var recentList by mutableStateOf<List<String>>(emptyList())
        private set
    var termOfDay by mutableStateOf(TermOfDay())
        private set

    init {
        categories = repo.getCategories()
        recentList = repo.getRecentSearches()
        termOfDay = repo.getTermOfDay()
    }

    fun onSearchChange(newText: String) { searchText = newText }

    fun onSearchGo() {
        val q = searchText.trim()
        if (q.isEmpty()) return
        val newList = mutableListOf<String>()
        newList.add(q) // নতুনটা উপরে
        for (item in recentList) {
            if (item != q) newList.add(item) // duplicate বাদ
        }
        recentList = newList.take(8) // max 8টা
        searchText = ""
    }

    fun onClearRecent() { recentList = emptyList() }

    fun onRecentClick(word: String) { searchText = word }
}
```

Deep ব্যাখ্যা (ভিডিওতে 6 মিনিট ধরে বলো):

```kotlin
var searchText by mutableStateOf("")
```
- `mutableStateOf` মানে Compose observable। value change হলেই যে Composable এটা পড়ছে সেটা recompose হবে।
- `by` হলো delegate — `searchText = "abc"` লিখলেই হয়, `.value` লিখতে হয় না।
- `private set` মানে বাইরে থেকে পড়া যাবে (`vm.searchText`) কিন্তু change করা যাবে না। Change শুধু function দিয়ে — এটাই encapsulation।

```kotlin
fun onSearchGo() { ... newList.take(8) }
```
- Step 1: `trim()` — আগে-পিছে space বাদ। খালি হলে return।
- Step 2: নতুন query উপরে add, পুরনো list থেকে duplicate বাদ দিয়ে নিচে বসাও। যেমন recent [Isotope, Entropy] + search "Entropy" → [Entropy, Isotope]।
- Step 3: `take(8)` — 8টার বেশি রাখো না, না হলে UI overflow হবে।
- Step 4: `searchText = ""` — box খালি করো।

Common mistake (ভিডিওতে বলো): "অনেকে `var recentList = mutableStateOf(listOf())` লিখে ভুলে `recentList.value` update করতে ভুলে যায়। `by` use করলে সহজ। আর ViewModel এ কখনো Context, Activity, Composable রাখবে না — memory leak হবে।"

---
## 8. FILE 6: HomeScreen.kt — VIEW (222 লাইন, 5 ভাগে)

Full Code Skeleton + প্রতিটা ভাগের ব্যাখ্যা:

```kotlin
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. Title, 2. Search, 3. Categories, 4. Recent, 5. Term Card
    }
}
```
- `vm: HomeViewModel = viewModel()` — Activity scope এ একটাই ViewModel instance পাবে, screen rotate করলেও data মুছবে না।
- `verticalScroll(rememberScrollState())` — ছোট ফোনে scroll হবে।
- `padding(16.dp)` — চারপাশে ফাঁকা, design breath নেয়।

### 8.1 Title (3 লাইন Text)
```kotlin
Text("TRSP Scientific", fontSize = 22.sp, fontWeight = FontWeight.Bold)
Text("Dictionary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.trsp_red))
Text("Understand Science, One Term at a Time", fontSize = 13.sp, color = Color.Gray)
```
বলো: "প্রথমটা কালো bold 22sp, দ্বিতীয়টা লাল 18sp — brand color, তৃতীয়টা gray 13sp subtitle।"

### 8.2 Search Bar (Row + Box + TextField + Go Button)
```kotlin
Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
        modifier = Modifier.weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.trsp_search_bg))
            .border(1.dp, colorResource(R.color.trsp_search_stroke), RoundedCornerShape(16.dp))
    ) {
        TextField(
            value = vm.searchText,
            onValueChange = { vm.onSearchChange(it) },
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
    Box(
        modifier = Modifier.size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colorResource(R.color.trsp_red))
            .clickable { vm.onSearchGo() },
        contentAlignment = Alignment.Center
    ) {
        Image(painterResource(R.drawable.ic_arrow_right), contentDescription = "Go", modifier = Modifier.size(24.dp))
    }
}
```
ব্যাখ্যা:
- `weight(1f)` — বাকি জায়গা TextField নেবে, button fixed 52dp।
- `clip + background + border` sequence important — আগে clip, তারপর background, না হলে corner কাটবে না।
- `TextFieldDefaults.colors(...Transparent)` — default নীল underline সরিয়ে custom gray box দেখাই।
- `onValueChange = { vm.onSearchChange(it) }` — প্রতিটা অক্ষর type হলেই ViewModel এ যায়, single source of truth।
- Go Button `clickable { vm.onSearchGo() }` — ViewModel এর logic call।

### 8.3 Categories (2 Row × 3 Box)
```kotlin
Text("QUICK CATEGORIES", fontWeight = FontWeight.Bold, fontSize = 12.sp)
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    for (i in 0 until 3) {
        CategoryBox(name = vm.categories[i].title, emoji = vm.categories[i].emoji, colorId = i, modifier = Modifier.weight(1f))
    }
}
Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    for (i in 3 until 6) {
        CategoryBox(name = vm.categories[i].title, emoji = vm.categories[i].emoji, colorId = i, modifier = Modifier.weight(1f))
    }
}

@Composable
fun CategoryBox(name: String, emoji: String, colorId: Int, modifier: Modifier = Modifier) {
    val bg = when (colorId) {
        0 -> colorResource(R.color.cat_physics_bg)
        1 -> colorResource(R.color.cat_chemistry_bg)
        2 -> colorResource(R.color.cat_biology_bg)
        3 -> colorResource(R.color.cat_astronomy_bg)
        4 -> colorResource(R.color.cat_math_bg)
        else -> colorResource(R.color.cat_compsci_bg)
    }
    Box(modifier = modifier.height(80.dp).clip(RoundedCornerShape(16.dp)).background(bg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Text(name, fontSize = 12.sp)
        }
    }
}
```
ব্যাখ্যা: `spacedBy(8.dp)` — box এর মাঝে gap। `weight(1f)` — 3টা সমান ভাগ। `when(colorId)` — 6টা আলাদা pastel। ভিডিওতে বলো "LazyVerticalGrid দিয়েও করা যেত, কিন্তু beginner দের Row সহজ, পরে Grid এ migrate করবো।"

### 8.4 Recent Searches (Chip + Clear)
```kotlin
Row(verticalAlignment = Alignment.CenterVertically) {
    Text("RECENT SEARCHES", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
    TextButton(onClick = { vm.onClearRecent() }) { Text("Clear", color = Color.Gray, fontSize = 12.sp) }
}
if (vm.recentList.isEmpty()) {
    Text("No recent searches yet.", color = Color.Gray, fontSize = 13.sp)
} else {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (word in vm.recentList.take(3)) { RecentChip(word = word, onClick = { vm.onRecentClick(word) }) }
    }
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (word in vm.recentList.drop(3).take(3)) { RecentChip(word = word, onClick = { vm.onRecentClick(word) }) }
    }
}

@Composable
fun RecentChip(word: String, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(Color.White)
        .border(1.dp, colorResource(R.color.trsp_chip_stroke), RoundedCornerShape(50.dp))
        .clickable { onClick() }.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(word, fontSize = 13.sp)
    }
}
```
`take(3)` প্রথম 3টা, `drop(3).take(3)` পরের 3টা — 2 লাইনে 6টা দেখায়। `RoundedCornerShape(50.dp)` মানে pill (পুরো গোল)।

### 8.5 Term of the Day (কালো কার্ড)
```kotlin
Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
    .background(colorResource(R.color.trsp_term_card)).padding(16.dp)) {
    Column {
        Text("SCIENTIFIC TERM OF THE DAY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(vm.termOfDay.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(vm.termOfDay.description, color = Color(0xFFD1D5DB), fontSize = 13.sp)
        Button(onClick = { vm.onRecentClick(vm.termOfDay.title) },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.trsp_red))) {
            Text("Learn More")
        }
    }
}
```
ব্যাখ্যা: গাঢ় background `#121A2C` + সাদা title 22sp + হালকা gray description + লাল button — contrast বেশি, premium লাগে।

---
## 9. FILE 7: AppNav.kt — Navigation (5 Tab)

Full Code `ui/navigation/AppNav.kt`:
```kotlin
@Composable
fun DictionaryApp() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomBar(navController) }, containerColor = Color.White) { padding ->
        NavHost(navController = navController, startDestination = BottomTab.HOME.route,
            modifier = Modifier.padding(padding)) {
            composable(BottomTab.HOME.route) { HomeScreen() }
            composable(BottomTab.SUBJECTS.route) { SubjectsScreen() }
            composable(BottomTab.SAVED.route) { SavedScreen() }
            composable(BottomTab.LEARN.route) { LearnScreen() }
            composable(BottomTab.PROFILE.route) { ProfileScreen() }
        }
    }
}

@Composable
fun BottomBar(nav: NavController) {
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    Column {
        HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.trsp_search_stroke))
        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            for (tab in BottomTab.entries) {
                val selected = (tab.route == currentRoute)
                val tint = if (selected) colorResource(R.color.trsp_red) else Color.Gray
                Column(modifier = Modifier.clickable {
                    nav.navigate(tab.route) {
                        popUpTo(BottomTab.HOME.route)
                        launchSingleTop = true
                    }
                }.padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(tab.iconRes), contentDescription = tab.label,
                        modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(tint))
                    Text(tab.label, fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = tint)
                }
            }
        }
    }
}
```

ব্যাখ্যা (ভিডিওতে 6 মিনিট):
1. `rememberNavController()` — navigation এর খাতা, কোন screen এ আছি মনে রাখে।
2. `Scaffold(bottomBar=...)` — Material3 layout, নিচে bar fixed, মাঝে content। `padding` টা NavHost এ দিতে হবে, না হলে content bar এর নিচে ঢুকে যাবে — এটা সবচেয়ে common bug।
3. `NavHost + composable(route){Screen}` — route string মিললেই ওই Composable দেখায়।
4. `currentBackStackEntryAsState()` — route change হলে auto recompose হয়ে selected color বদলায়।
5. `popUpTo(HOME)` — back stack পরিষ্কার রাখে, Back চাপলে App থেকে বের হয়, অদ্ভুত loop হয় না।
6. `launchSingleTop = true` — একই tab 2বার চাপলে 2টা copy খোলে না।
7. `ColorFilter.tint(tint)` — একই কালো icon কে লাল/gray বানায়, আলাদা icon লাগে না।

---
## 10. FILE 8: TabScreens.kt + MainActivity.kt + Theme

### TabScreens.kt Full Code:
```kotlin
@Composable
fun SimpleTab(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("$title\n$subtitle", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
@Composable fun SubjectsScreen(modifier: Modifier = Modifier) { SimpleTab("Subjects", "6 ta subject ekhane asbe", modifier) }
@Composable fun SavedScreen(modifier: Modifier = Modifier) { SimpleTab("Saved", "Save kora word gulo ekhane asbe", modifier) }
@Composable fun LearnScreen(modifier: Modifier = Modifier) { SimpleTab("Learn", "Quiz / Learn ekhane asbe", modifier) }
@Composable fun ProfileScreen(modifier: Modifier = Modifier) { SimpleTab("Profile", "User profile ekhane asbe", modifier) }
```
বলো: "এখন placeholder, Part-2 তে আসল UI বসাবো। `SimpleTab` helper দিয়ে কোড 4 গুণ কমেছে — DRY principle।"

### MainActivity.kt Full Code:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Kotlin_trsp_dictionary_appTheme(dynamicColor = false) {
                DictionaryApp()
            }
        }
    }
}
```
- `enableEdgeToEdge()` — status bar পর্যন্ত full screen।
- `dynamicColor = false` — Android 12+ এ wallpaper থেকে color বদলে যাবে না, আমাদের লাল brand color fix থাকবে। এটা না করলে Samsung ফোনে লাল সবুজ হয়ে যেতে পারে!

### Theme.kt (সংক্ষেপে):
```kotlin
@Composable
fun Kotlin_trsp_dictionary_appTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
```

### AndroidManifest.xml:
```xml
<activity android:name=".MainActivity" android:exported="true"
    android:theme="@style/Theme.Kotlin_trsp_dictionary_app">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
`exported=true` + LAUNCHER — App icon এ click করলে MainActivity খোলে।

---
## 11. LOGIN API — Retrofit দিয়ে সম্পূর্ণ (কীভাবে কাজ করে)

> এক নজরে Flow: **View → ViewModel → Repository → API → Server** — MVVM এর ৪টা layer একসাথে কাজ করে।

### 11.0 Flow Diagram (আগে এটা বুঝো, তারপর কোড পড়ো)

```
LoginScreen (VIEW - চোখ)
   │  Login button চাপলে → onLoginClick()
   ▼
LoginViewModel (VIEWMODEL - মস্তিষ্ক)
   │  1. validate()          — email/password ঠিক আছে কিনা (client side)
   │  2. isLoading = true    — button এ spinner দেখাও, double click বন্ধ
   │  3. authRepository.login(email, password)
   ▼
AuthRepository (REPOSITORY - দোকানদার)
   │  withContext(Dispatchers.IO) — network call main thread এ হবে না (ANR আসবে)
   │  try/catch → Result.success(response) / Result.failure(exception)
   ▼
AuthApiService (INTERFACE - চুক্তি)
   │  @POST("api/auth/login") + suspend fun
   ▼
RetrofitClient → POST http://10.10.11.156:5000/api/auth/login
   │  পাঠাও: { "identifier": "a@b.com", "password": "123456" }
   │  পাও:   { "success": true, "token": "...", "message": "...", "user": {...} }
   ▼
GsonConverterFactory — JSON কে Kotlin object বানিয়ে দেয়
   ▼
ViewModel ← Result ফেরত → dialog দেখাও → Success হলে Main (5 Tab) page
```

**ভিডিওতে বলো:** "Login API মানে শুধু Retrofit বসানো না — View শুধু দেখায়, ViewModel ভাবে, Repository ডাকে, API যায়। একটাও layer বাদ পড়লে login কাজ করবে না।"

---

### 11.1 যা যা লাগবে — Dependency + Permission

`gradle/libs.versions.toml`:
```toml
[versions]
retrofit = "2.11.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
```

`app/build.gradle.kts` — dependencies এ ২ লাইন:
```kotlin
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)
```

`AndroidManifest.xml` — এই ২টা লাইন ছাড়া login চলবে না:
```xml
<uses-permission android:name="android.permission.INTERNET" />
...
<application
    android:usesCleartextTraffic="true"
    ... >
```

ব্যাখ্যা:
- `INTERNET` permission — না থাকলে retrofit সাথে সাথে `Unable to resolve host` / `Failed to connect` দেবে।
- `usesCleartextTraffic="true"` — আমাদের BASE_URL `http://` (SSL নেই)। Android 9 (API 28) থেকে http block করে — এটা না দিলে `CLEARTEXT HTTP requests to ... not permitted` error আসবে। **Production এ https ব্যবহার করো, তখন এটা লাগবে না।**
- `gson-converter` ছাড়া retrofit JSON বোঝবে না — `LoginResponse` সবসময় null আসবে।

---

### 11.2 FILE 9: AuthModels.kt — REQUEST + RESPONSE (শুধু data)

Full Code `data/model/AuthModels.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier")
    val identifier: String,     // email OR username — server যেকোনো একটা চায়
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    val success: Boolean? = null,
    val token: String? = null,      // JWT token — পরের API call এ পাঠাবে
    val message: String? = null,    // "Login successful"
    val user: UserData? = null,
    val error: String? = null       // "Invalid credentials"
)

data class UserData(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null
)
```

লাইন বাই লাইন:
1. `@SerializedName("identifier")` — Kotlin এর নাম `identifier`, JSON এর keyও `identifier`। নাম মিললে এটা লাগে না, কিন্তু server এর key আলাদা হলে (যেমন `user_email`) এখানে লিখে দিলেই Gson বুঝে নেবে — **এটাই ৯০% API bug এর সমাধান।**
2. **সব field `String? = null` (nullable + default)** — কেন? server কখনো `error` পাঠায়, কখনো `token`। সব non-null করলে একটা field এসে না গেলেই **crash** হবে (JsonSyntaxException)। nullable রাখলে Gson null বসাবে, আমরা check করে decision নেব।
3. `LoginRequest` হলো যে JSON **পাঠাবো**, `LoginResponse` হলো যে JSON **পাবো** — দুটো আলাদা data class, একটাতে করবে না।
4. Model এ কোনো function, API call, if-else নেই — শুধু খাঁচা।

**ভিডিওতে বলো:** "শুধু `success=true` দেখে খুশি হয়ো না — দেখো `error` আসছে কিনা। Server 200 status code দিয়েও error message পাঠাতে পারে, তাই body check করতে হবে।"

---

### 11.3 FILE 10: RetrofitClient.kt — object মানে Single Instance

Full Code `data/network/RetrofitClient.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.10.11.156:5000/"

    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
```

ব্যাখ্যা:
- `object` — Kotlin এর singleton। পুরো app এ `RetrofitClient` একটাই instance হবে, প্রতিবার নতুন Retrofit বানানো হবে না (connection reuse + memory বাঁচে)।
- `BASE_URL` শেষে **`/` থাকা বাধ্যতামূলক** — না থাকলে retrofit চুপচাপ crash করে: `baseUrl must end in /`।
- `by lazy` — প্রথমবার `authApiService` ডাকা হলে তখনই build হবে, বাকি সময় idle থাকবে। এটাই Kotlin এর lazy initialization।
- `BASE_URL` তে তোমার **ল্যাপটপের LAN IP** (`10.10.11.156`) বসানো আছে, localhost নয় — কারণ এমুলেটর/ফোন থেকে `localhost` মানে ওই device নিজে, server পাবে না। প্রতিটা developer এর IP আলাদা, তাই এটা **hardcode না করে `local.properties` থেকে BuildConfig এ নেওয়া ভালো** (পরের কাজ)।
- IP বদলালে `BASE_URL` বদলাও, তারপর app **restart** করো — retrofit lazy, hot-reload এ ধরবে না।

---

### 11.4 FILE 11: AuthApiService.kt — INTERFACE = চুক্তি

Full Code `data/network/AuthApiService.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.network

import com.example.kotlin_trsp_dictionary_app.data.model.LoginRequest
import com.example.kotlin_trsp_dictionary_app.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
```

লাইন বাই লাইন:
1. `interface` — এর ভেতরে body থাকে না, শুধু **চুক্তি**। Retrofit নিজেই এর implementation বানিয়ে দেয় (Proxy pattern) — তুমি কিছুই implement করো না।
2. `@POST("api/auth/login")` — relative path, BASE_URL এর সাথে জুড়ে হবে: `http://10.10.11.156:5000/` + `api/auth/login`। **শুরুতে `/` দেবে না**, না হলে পুরো path কেটে যাবে।
3. `suspend` — এটা ছাড়া call করতে হলে `runBlocking`/thread লাগবে। suspend থাকলে `viewModelScope.launch` এ সরাসরি call করা যায়, main thread ব্লক হয় না।
4. `@Body` — JSON body হিসেবে `LoginRequest` object যাবে। Retrofit Gson converter এটাকে `{"identifier":...,"password":...}` বানাবে।
5. `Response<LoginResponse>` — raw body না দিয়ে **পুরো Response object** দিই, যাতে `response.isSuccessful` (status 200-299) আর `response.code()` দেখা যায়। শুধু `LoginResponse` দিলে retrofit error status হলেই exception ফেলে — error body পড়তে পারবে না।

---

### 11.5 FILE 12: AuthRepository.kt — Result<> দিয়ে ফেরত

Full Code `data/repository/AuthRepository.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.data.repository

import com.example.kotlin_trsp_dictionary_app.data.model.LoginRequest
import com.example.kotlin_trsp_dictionary_app.data.model.LoginResponse
import com.example.kotlin_trsp_dictionary_app.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    private val api = RetrofitClient.authApiService

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(identifier = email, password = password))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login failed (${response.code()})"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
```

Deep ব্যাখ্যা:
- `withContext(Dispatchers.IO)` — network/disk কাজ IO thread এ। **এটা না দিলে** Android 11+ এ `NetworkOnMainThreadException`, আগের version এ app freeze (ANR)।
- `response.isSuccessful` — status code 200-299 কিনা। true হলে body আছে, নাহলে 401/404/500 ইত্যাদি।
- `response.errorBody()?.string()` — 401 হলে server এর JSON error message এখানে আসে (`{"error":"Invalid password"}`), তাই ইউজারকে "Login failed (401)" না দেখিয়ে **আসল কারণ** দেখানো যায়।
- `?: "Login failed (${response.code()})"` — error body খালি হলে status code সহ fallback message।
- `try/catch` — internet নেই, server বন্ধ, wrong IP → `UnknownHostException`/`ConnectException`। ধরা না হলে ViewModel এ crash চলে যাবে।
- `Result<T>` — Kotlin standard library এর sealed class। `Result.success` / `Result.failure` — ViewModel এ `fold()` দিয়ে দুইভাগ পার করা যায়, try/catch লাগে না।
- Repository কখনো UI (Toast/Dialog) বানায় না — শুধু data বা error **ফেরত** দেয়। UI সিদ্ধান্ত ViewModel নেয়।

---

### 11.6 FILE 13: LoginViewModel.kt — Validate + API Call (মস্তিষ্ক)

Full Code `ui/auth/LoginViewModel.kt`:
```kotlin
package com.example.kotlin_trsp_dictionary_app.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlin_trsp_dictionary_app.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var dialogTitle by mutableStateOf<String?>(null)
        private set
    var dialogMessage by mutableStateOf<String?>(null)
        private set

    private var isSuccessLogin = false

    fun onEmailChange(v: String) {
        email = v
        emailError = null      // লিখতে শুরু করলেই পুরনো লাল error মুছে যায়
    }

    fun onPasswordChange(v: String) {
        password = v
        passwordError = null
    }

    fun onTogglePassword() {
        passwordVisible = !passwordVisible   // Show / Hide
    }

    fun clearDialog(onSuccess: (() -> Unit)? = null) {
        val wasSuccess = isSuccessLogin
        dialogTitle = null
        dialogMessage = null
        if (wasSuccess && onSuccess != null) {
            isSuccessLogin = false
            onSuccess()      // dialog বন্ধ + সাথে সাথে Main page এ যাও
        }
    }

    fun validate(): Boolean {
        var ok = true
        emailError = when {
            email.isBlank() -> "Email required".also { ok = false }
            "@" !in email -> "Enter a valid email".also { ok = false }
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "Password required".also { ok = false }
            password.length < 6 -> "Minimum 6 characters".also { ok = false }
            else -> null
        }
        return ok
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        if (!validate()) return        // ১. client side validation — না হলে API call ই হবে না

        isLoading = true               // ২. spinner চালু, button বন্ধ

        viewModelScope.launch {        // ৩. ViewModel নিজের scope — Activity ধ্বংস হলে auto cancel
            val result = authRepository.login(email.trim(), password)
            isLoading = false
            result.fold(
                onSuccess = { response ->
                    if (response.error != null) {
                        isSuccessLogin = false
                        dialogTitle = "Login Failed"
                        dialogMessage = response.error      // server এর আসল error
                    } else {
                        isSuccessLogin = true
                        dialogTitle = "Success"
                        dialogMessage = response.message ?: "Login successful!"
                    }
                },
                onFailure = { throwable ->
                    isSuccessLogin = false
                    dialogTitle = "Error"
                    dialogMessage = throwable.localizedMessage ?: "Network error. Please try again."
                }
            )
        }
    }
}
```

Deep ব্যাখ্যা (ভিডিওতে ৫ মিনিট):
1. **২ ধাপে ২ জায়গায় validation** — `validate()` হলো client side (খালি email, `@` নেই, password ছোট), API call এর **আগেই**। পরের ধাপ server side — password ভুল হলে `response.error` ধরবে। দুটোই লাগে, একটাও বাদ দেবে না।
2. `emailError`/`passwordError` type `String?` — null হলে `isError = false` (লাল কিনা না), value থাকলে `OutlinedTextField` লাল + supportingText এ message দেখাবে। **UI নিজে কিছু বলে না, শুধু VM এর error পড়ে।**
3. `onEmailChange` এ `emailError = null` — typing শুরু করলেই পুরনো error মুছে যায়, না হলে ইউজার correct লিখেও লাল text দেখতে থাকবে।
4. `isLoading` একটাই state, দুই কাজ করে — (ক) button `enabled = !isLoading` (double login prevent), (খ) button এর ভেতরে `CircularProgressIndicator` দেখাবে।
5. `viewModelScope.launch` — ViewModel clear হলে (user back চাপলে) coroutine auto cancel, রয়েলে response এলেও UI update করবে না → crash/lazy bug থাকে না।
6. `result.fold(onSuccess, onFailure)` — Kotlin এর expression। `try/catch` না লিখেই দুই পথ পার করা যায় — এটাই idiomatic Kotlin।
7. **দরকারি বাগ ফিক্স:** সফল login হলেও সাথে সাথে navigate করা যায় না — কারণ আগে dialog দেখাতে হয়। তাই `isSuccessLogin` flag রাখা হয়েছে। `clearDialog(onSuccess)` — dialog OK চাপলে তখনই `onSuccess()` ডাকবে (Main page), আর fail হলে শুধু dialog বন্ধ হবে। **ভুল হলে ইউজার login page এই থাকবে — এটাই আসল কাজ।**

**ভিডিওতে বলো:** "অনেকের কোডে দেখেছি `if (response.isSuccessful) navController.navigate(...)` — ViewModel এর ভেতরে navigation! এটা বড় mistake। ViewModel শুধু **state** দেবে (dialog/message), navigate করবে **View**, কারণ navigation হলো UI এর কাজ।"

---

### 11.7 FILE 14: LoginScreen.kt — View (শুধু দেখায়, কিছু জানে না)

Full Code (গুরুত্বপূর্ণ অংশ) `ui/auth/LoginScreen.kt`:
```kotlin
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,          // ViewModel না, NAV এর callback!
    onNavigateToRegister: () -> Unit,
    vm: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    LoginContent(
        email = vm.email,
        password = vm.password,
        passwordVisible = vm.passwordVisible,
        isLoading = vm.isLoading,
        emailError = vm.emailError,
        passwordError = vm.passwordError,
        dialogTitle = vm.dialogTitle,
        dialogMessage = vm.dialogMessage,
        onEmailChange = vm::onEmailChange,          // :: হলো method reference
        onPasswordChange = vm::onPasswordChange,
        onTogglePassword = vm::onTogglePassword,
        onDismissDialog = { vm.clearDialog(onLoginSuccess) },
        onLoginClick = { vm.onLoginClick(onLoginSuccess) },
        onRegisterClick = onNavigateToRegister,
        modifier = modifier
    )
}
```

- **State hoisting** — `LoginContent` এ `vm` নেই, শুধু value + callback আছে। এতে Preview বানানো সহজ (`LoginScreenPreview` এ হাতে লিখে dummy data দিয়েছি) এবং Composable **stateless + reusable** থাকে।
- `vm::onEmailChange` — Kotlin method reference, হাতে `{ vm.onEmailChange(it) }` লিখলেও হয়।

UI এর বাকি অংশ:
```kotlin
// ১. Error → লাল + message (supportingText)
OutlinedTextField(
    value = email,
    onValueChange = onEmailChange,
    enabled = !isLoading,                      // চলাকালীন edit বন্ধ
    isError = emailError != null,
    supportingText = { if (emailError != null) Text(emailError) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    ...
)

// ২. Password → Show/Hide toggle
visualTransformation = if (passwordVisible) VisualTransformation.None
                       else PasswordVisualTransformation(),
trailingIcon = { Text(if (passwordVisible) "Hide" else "Show", ...) }

// ৩. Login button → loading হলে spinner
Button(onClick = onLoginClick, enabled = !isLoading, ...) {
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
    } else {
        Text("Login", fontWeight = FontWeight.Bold)
    }
}
```

ব্যাখ্যা:
- `enabled = !isLoading` — একসাথে ২বার API call হবে না, inputও বদলাবে না (double submit এর common fix)।
- `isError + supportingText` — Material3 এ error দেখানোর standard উপায়, red border + নিচে message। আলাদা Toast লাগে না।
- `KeyboardType.Email/Password` — mobile এ `@` key থাকা keyboard, password এ hide থাকা keyboard।
- **Dialog** — `dialogMessage != null` হলেই `AlertDialog` দেখাবে, OK চাপলে `clearDialog(onLoginSuccess)` → success হলে navigate।
- **Skip for now** — দরকার না হলে login ছাড়াই demo চালানো যায় (testing এ উপকারী)।

---

### 11.8 Navigation — splash → login ↔ register → main

Full Code অংশ `ui/navigation/AppNav.kt`:
```kotlin
object RootRoute {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun AppRootNav() {
    val rootNav = rememberNavController()
    NavHost(navController = rootNav, startDestination = RootRoute.SPLASH) {
        composable(RootRoute.SPLASH) {
            SplashScreen(onSplashFinished = {
                rootNav.navigate(RootRoute.LOGIN) {
                    popUpTo(RootRoute.SPLASH) { inclusive = true }  // back চাপলে splash আসবে না
                }
            })
        }
        composable(RootRoute.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    rootNav.navigate(RootRoute.MAIN) {
                        popUpTo(RootRoute.LOGIN) { inclusive = true } // back চাপলে login আসবে না!
                    }
                },
                onNavigateToRegister = { rootNav.navigate(RootRoute.REGISTER) }
            )
        }
        composable(RootRoute.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { rootNav.navigate(RootRoute.MAIN) { popUpTo(RootRoute.LOGIN) { inclusive = true } } },
                onNavigateToLogin = { rootNav.popBackStack() }
            )
        }
        composable(RootRoute.MAIN) { DictionaryApp() }   // 5 Tab wala main app
    }
}
```

ব্যাখ্যা:
- **২টা NavController** — `rootNav` (splash/login/main level) আর `DictionaryApp` এর ভেতরের `navController` (5 tab level)। একটাতে সব করলে back stack জটিল হয়ে যায়।
- `popUpTo(LOGIN) { inclusive = true }` — login successful হলে login screen **back stack থেকে মুছে যায়**, তাই Back চাপলে login page আসবে না, সরাসরি app বন্ধ হবে। **এটা না করলে ইউজার login করে main এ গিয়ে back চাপলে আবার login ফর্মে ফিরে যাবে — classic bug।**
- `onLoginSuccess: () -> Unit` — navigation logic ViewModel এ না রেখে View/Nav এ, তাই LoginViewModel test করা যায় navController ছাড়াই।
- Register থেকেও একই `MAIN` এ যায়, আর "Already have account" চাপলে `popBackStack()` — পিছনের screen (login) এ ফিরে যায়।

---

### 11.9 Server এ কী যায়, কী আসে (JSON)

Request — যেটা app পাঠায়:
```http
POST http://10.10.11.156:5000/api/auth/login
Content-Type: application/json

{
  "identifier": "you@example.com",
  "password": "123456"
}
```

Response — সফল হলে:
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Login successful",
  "user": { "id": "6521...", "name": "Rakib", "email": "you@example.com" }
}
```

Response — ভুল password হলে (HTTP 401):
```json
{
  "success": false,
  "error": "Invalid password"
}
```

ভিডিওতে Postman বা `curl` দিয়ে আগে API টেস্ট করে দেখাও — **আগে server ঠিক আছে কিনা প্রমাণ করো, তারপর app এর দোষ খুঁজো।** অনেকে ভাবে app bug, আসলে server ই বন্ধ থাকে!

---

### 11.10 Common Error Fix (Login API)

| Error | কারণ | সমাধান |
|---|---|---|
| `CLEARTEXT HTTP requests not permitted` | http:// URL, Android 9+ block করে | `android:usesCleartextTraffic="true"` |
| `Failed to connect to /10.10.11.156` | IP ভুল / server বন্ধ / একই WiFi নেই | BASE_URL IP মিলাও, laptop-server একই network এ |
| `localhost` ধরছে না | ফোন/এমুলেটরে localhost = ওই device | সবসময় LAN IP বসাও (10.10.11.156) |
| `baseUrl must end in /` | BASE_URL এ slash নেই | `"http://ip:5000/"` — শেষে `/` |
| `JsonSyntaxException` / response null | JSON key মেলেনি | `@SerializedName("...")` দিয়ে key মিলাও |
| `Expected BEGIN_OBJECT but was STRING` | server HTML/text পাঠাচ্ছে (404 page) | path ঠিক আছে কিনা Postman এ check করো |
| 401 এলেও token নেই | server error body আলাদা key এ | `errorBody().string()` + `response.code()` log করো |
| Login হলে back চাপলে login আসে | `popUpTo` missing | `navigate(MAIN) { popUpTo(LOGIN) { inclusive = true } }` |
| Double API call | button disabled করা নেই | `enabled = !isLoading` |

**ভিডিওতে বলো:** "Login test করার সোজা উপায় — Logcat এ `Log.d("LOGIN", response.body().toString())` দাও, JSON আসছে কিনা দেখো। JSON আসছে কিন্ছু UI এ না দেখালে সমস্যা ViewModel/View এ, JSON ই আসছে না হলে সমস্যা network/permission/IP এ। দুইটা আলাদা করে ধরো, না হলে সারাদিন ঘাটাঘাটি করবে।"

---

### ভিডিওতে Login API বলার Script (এই অংশটা যোগ করো)

**Login Intro (45:00):** "এবার আসল কাজ — Login API। এতক্ষণ static data ছিল, এবার server থেকে data আনবো Retrofit দিয়ে।"

**46:00 Dependency:** "Retrofit 2.11.0 + Gson converter — একটা JSON parser, এটা ছাড়া Kotlin object বোঝবে না। আর Manifest এ দুইটা line — INTERNET permission আর usesCleartextTraffic।"

**48:00 AuthModels.kt:** "যেটা পাঠাবো LoginRequest, যেটা পাবো LoginResponse। সব field null রাখলে কেন — server কখনো token পাঠায়, কখনো error, একটা missing হলেই crash হবে।"

**50:00 RetrofitClient + ApiService:** "object = singleton, lazy = দরকার হলে বানাবে। interface এর ভেতরে body নেই, শুধু চুক্তি — @POST আর @Body দিয়ে retrofit বুঝে নেয়। BASE_URL এর শেষে slash মনে রাখো, না হলে crash।"

**53:00 AuthRepository:** "Dispatchers.IO — network main thread এ চালালে app freeze হবে। Result.success/failure — ধরা গেল আর না-ধরা গেল দুটোই ধরতে হবে। try/catch না থাকলে internet না থাকলেই app বন্ধ।"

**56:00 LoginViewModel:** "validate আগে — খালি email দিয়ে server এ call করলে server অপেক্ষা করবে, bandwidth নষ্ট। তারপর isLoading, আর fold দিয়ে দুই পথ। মনে রাখো — ViewModel এ navigation করবে না, শুধু state দেবে।"

**60:00 LoginScreen:** "isError + supportingText = Material3 এর দামি error design, Toast লাগে না। Loading হলে button এ spinner, enabled=false — double submit বন্ধ। আর dialog OK চাপলেই navigate — এটাই flow।"

**63:00 Navigation:** "popUpTo inclusive — না করলে back চাপলে ইউজার আবার login ফর্মে ফিরবে। এটাই login এর সবচেয়ে common bug।"

**65:00 Run + Test:** "Postman এ আগে API check, তারপর app এ wrong password দিয়ে error দেখাও, তারপর correct দিয়ে main page এ যাও। Logcat এ response print করো।"

---

## 12. YOUTUBE বলার স্ক্রিপ্ট — Time সহ (হুবহু পড়ো)

**0:00 Intro:** "Assalamu Alaikum বন্ধুরা! আজ MVVM + Compose + Navigation এক প্রজেক্টে শিখবো। TRSP Dictionary App বানাবো — Search, Category, Recent, Term Card, Bottom 5 Tab।"

**2:00 Setup দেখাও:** Android Studio New Project → `build.gradle.kts` + `libs.versions.toml` খুলে version বলো।

**5:00 colors.xml + drawable:** প্রতিটা color copy করে বলো কীসের জন্য। `bg_search_bar.xml` খুলে shape/corner/stroke বুঝাও।

**9:00 HomeModels.kt:** `data class Category` লিখে live টাইপ করো, enum BottomTab বুঝাও।

**14:00 HomeRepository.kt:** "দোকানদার" গল্প বলো, static list থেকে Room এ যাওয়ার প্ল্যান বলো।

**17:00 HomeViewModel.kt:** `mutableStateOf + by + private set` বোর্ডে লিখে বুঝাও। `onSearchGo()` debug করে দেখাও — breakpoint দিয়ে newList কীভাবে বাড়ে।

**25:00 HomeScreen.kt:** 5 ভাগে ভাগ করে দেখাও। TextField transparent trick + Modifier order (clip→background→border) ভুল করলে কী হয় live দেখাও।

**35:00 AppNav.kt:** `Scaffold padding` bug দেখাও (padding না দিলে bar এর নিচে content ঢুকে)। `popUpTo + launchSingleTop` ছাড়া back press loop দেখাও।

**40:00 Login API (Part-2):** Retrofit dependency + Manifest permission → AuthModels (Request/Response) → RetrofitClient + AuthApiService → AuthRepository (IO + Result) → LoginViewModel (validate + fold) → LoginScreen (error + spinner + dialog) → popUpTo navigation।

**45:00 Run + Outro:** Emulator এ run করো (Pixel 6 API 34), ভুল password দিয়ে error dialog, সঠিক দিয়ে main page, search করে recent add দেখাও, tab ঘুরাও। "Homework: Saved এ bookmark, Learn এ Quiz। Next video: Room DB। Like-Subscribe!"

**Description Box এর জন্য:**
```
TRSP Dictionary App | Kotlin Compose MVVM Bangla Tutorial
GitHub: (link)
Timestamps: 0:00 Intro, 2:00 Setup, 5:00 Colors, 9:00 Model, 14:00 Repo, 17:00 ViewModel, 25:00 UI, 35:00 Navigation, 40:00 Login API, 45:00 Run
#Kotlin #JetpackCompose #BanglaTutorial #MVVM #Android #Retrofit
```

---
## 13. Run + Error Fix

1. Android Studio Ladybug+ → Open project → Sync
2. `local.properties`: `sdk.dir=C\:\\Users\\...\\AppData\\Local\\Android\\Sdk`
3. Emulator: Pixel 6, API 34+ → Run ▶️
4. Common error:
   - `Unresolved reference viewModel` → `lifecycle-viewmodel-compose` dependency add করোনি
   - `Unresolved reference NavHost` → `navigation-compose` missing
   - Preview blank → `dynamicColor=false` দাও, rebuild করো
   - BottomBar overlap → `Modifier.padding(padding)` ভুলে গেছো
   - Login API error ৯টা → **Section 11.10 এর table** দেখো (cleartext, IP, slash, JSON key...)

## 14. Next Video Homework

- [x] ~~Login API: Retrofit + MVVM~~ — **সম্পূর্ণ (Section 11)**
- [ ] Register API: `@POST("api/auth/register")` — একই `AuthApiService` এ নতুন function, `AuthModels` এ `RegisterRequest`
- [ ] Token persist: login এর `token` DataStore/SharedPreferences এ রাখো + Splash থেকে check → **auto login** (আগে আগে login করতে হবে না)
- [ ] Room: `@Entity RecentWord`, `@Dao`, recent persist (App বন্ধ হলেও থাকবে)
- [ ] Subjects detail page: `navController.navigate("detail/{id}")` + argument
- [ ] Saved bookmark: DataStore + heart icon toggle
- [ ] Learn Quiz: MCQ + score state + progress bar
- [ ] Search API: Retrofit + free dictionary API
- [ ] Dark Mode test + Bangla font (Hind Siliguri)

---
Made with ❤️ Kotlin + Compose — TRSP | Script ভাষা: বাংলা | Target: 45-50 min detailed video + Login API Part
