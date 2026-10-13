# TRSP Scientific Dictionary App — Kotlin + Jetpack Compose

> MVVM + Compose + Navigation + Retrofit Login/Register API — Beginner to Intermediate

## About This Project

**TRSP Scientific Dictionary** — Science term শেখার Android app. User Splash → Login/Register → Home (5 Tab) flow তে চলে.

**Features:**
- Splash (2 sec, লাল T logo) → Auto Login page এ যায়
- Login API (`POST api/auth/login`) — email + password, validation + spinner + dialog + `popUpTo` navigation
- Register API (`POST api/auth/register`) — name/email/phone/password/confirm + Class dropdown (`GET api/classes`, fallback list সহ)
- Home — Search + Quick Categories (6টা) + Recent Searches (max 8, duplicate বাদ) + Term of the Day কার্ড
- Bottom 5 Tab — Home | Subjects | Saved | Learn | Profile (`TabScreens.kt` এখন placeholder)
- MVVM + DI (`AppContainer` + `AppViewModelFactory`) + StateFlow + Channel Effect (one-shot navigation)

Reference UI: `app/src/main/res/drawable/home_screen.png`

## App Structure

```
app/src/main/
├── AndroidManifest.xml                      // INTERNET + usesCleartextTraffic=true
├── java/com/example/kotlin_trsp_dictionary_app/
│   ├── MainActivity.kt                      // setContent { AppRootNav() }
│   ├── data/model/
│   │   ├── HomeModels.kt                    // Category, TermOfDay, BottomTab enum
│   │   ├── AuthModels.kt                    // LoginRequest/Response, RegisterRequest, ClassOption + CLASS_OPTIONS fallback
│   │   └── ClassModels.kt                   // ClassesResponse, ClassItem (GET api/classes)
│   ├── data/network/
│   │   ├── RetrofitClient.kt                // object singleton, BASE_URL=http://10.10.11.156:5000/, auth + class service
│   │   ├── AuthApiService.kt                // @POST login, @POST register
│   │   └── ClassApiService.kt               // @GET api/classes
│   ├── data/repository/
│   │   ├── HomeRepository.kt                // static categories/recent/term
│   │   ├── AuthRepository.kt                // login()+register() → Dispatchers.IO + Result<>
│   │   └── ClassRepository.kt               // getClasses() → filter published + sort by order → List<ClassOption>
│   ├── di/
│   │   ├── AppContainer.kt                  // object: auth/home/class repository singleton
│   │   └── AppViewModelFactory.kt           // viewModelFactory { Login/Register/Home ViewModel }
│   ├── ui/
│   │   ├── splash/SplashScreen.kt           // LaunchedEffect delay(2000) → onSplashFinished
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt               // StateFlow collect + Effect navigate + dialog + Skip
│   │   │   ├── LoginViewModel.kt            // LoginUiState + LoginEffect(NavigateToMain)
│   │   │   ├── RegisterScreen.kt            // StateFlow + dropdown + confirm password
│   │   │   └── RegisterViewModel.kt         // RegisterUiState + RegisterEffect(NavigateToMain/Login) + loadClasses()
│   │   ├── home/
│   │   │   ├── HomeScreen.kt                // Title + Search + Categories + Recent + Term Card
│   │   │   └── HomeViewModel.kt             // HomeUiState(StateFlow) + onSearchGo/take(8)
│   │   ├── navigation/AppNav.kt             // AppRootNav(splash→login↔register→main) + DictionaryApp(5 tab) + BottomBar
│   │   ├── tabs/TabScreens.kt               // Subjects/Saved/Learn/Profile placeholder (SimpleTab)
│   │   ├── components/CommonComponents.kt   // AppTextField + AppDropdown + PrimaryButton
│   │   └── theme/Color.kt, Theme.kt, Type.kt // dynamicColor=false (brand লাল fix)
└── res/
    ├── values/colors.xml                    // trsp_red #C8102E, search_bg, term_card #121A2C, 6 cat colors
    ├── values/strings.xml, themes.xml
    └── drawable/bg_*.xml, ic_*.xml, home_screen.png
```

**MVVM Flow:**

```
View (Screen) → ViewModel (StateFlow + Effect) → Repository (IO + Result) → ApiService → Server
DI: AppContainer (repo) → AppViewModelFactory → viewModel(factory=...)
```

- View শুধু state দেখায়, event পাঠায়
- ViewModel validation + `viewModelScope.launch` + `Result.fold`
- Repository `withContext(Dispatchers.IO)` + `try/catch` + `Result`
- Navigation ViewModel এ নেই — Effect (`Channel`) → Screen `collect` করে `NavController.navigate`

## API

| Method | Endpoint | Request | Response |
|---|---|---|---|
| POST | `/api/auth/login` | `{identifier, password}` | `{success, token, message, user, error}` |
| POST | `/api/auth/register` | `{name, email, phone, password, classId}` | same as login |
| GET | `/api/classes` | — | `{success, message, data:[{id,name,slug,order,isPublished}]}` |

> BASE_URL = `http://10.10.11.156:5000/` (LAN IP, শেষে `/` বাধ্যতামূলক)। Emulator/Phone থেকে `localhost` কাজ করে না। Production এ `https` ব্যবহার করো।

**Tech Stack:** Kotlin 2.2.10, AGP 9.2.1, Compose BOM 2026.02.01, Navigation 2.9.3, Activity-Compose 1.13.0, Retrofit 2.11.0 + Gson, minSdk 24, targetSdk 37, Java 11.

## Run

1. Android Studio Ladybug+ → Open → Sync
2. `local.properties`: `sdk.dir=C\:\\Users\\...\\AppData\\Local\\Android\\Sdk`
3. Laptop server (`:5000`) চালাও + ফোন/এমুলেটর একই WiFi তে
4. Emulator Pixel 6 API 34+ → Run
5. Test: ভুল password → Error dialog, সঠিক → Main (5 Tab)

## TODO

- [ ] Token persist (DataStore) + Splash auto-login
- [ ] Room: RecentWord persist
- [ ] Subjects detail page (`detail/{id}`)
- [ ] Saved bookmark + Learn Quiz + Search API
- [ ] Dark Mode + Bangla font
