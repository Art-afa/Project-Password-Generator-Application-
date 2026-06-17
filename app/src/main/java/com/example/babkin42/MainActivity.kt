package com.example.babkin42

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF4CAF50),
                    background = Color.Black
                )
            ) {
                AppNavigation()
            }
        }
    }
}

data class User(
    val username: String,
    val email: String,
    val password: String,
    val savedPasswords: MutableList<SavedPassword> = mutableListOf()
)

data class SavedPassword(
    val id: String,
    val name: String,
    val password: String,
    val date: String
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController) { user ->
                currentUser = user
                navController.navigate("generator")
            }
        }
        composable("register") {
            RegisterScreen(navController) { user ->
                currentUser = user
                navController.navigate("generator")
            }
        }
        composable("generator") {
            if (currentUser != null) {
                GeneratorScreen(navController, currentUser!!) { updatedUser ->
                    currentUser = updatedUser
                }
            }
        }
        composable("profile") {
            if (currentUser != null) {
                ProfileScreen(navController, currentUser!!)
            }
        }
    }
}

// ==================== ЭКРАН ВХОДА С КАРТИНКОЙ ====================
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (User) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var registeredUsers by remember { mutableStateOf(listOf<User>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновая картинка
        Image(
            painter = painterResource(id = R.drawable.ara1),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Затемнение фона для лучшей читаемости
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔐 Вход", fontSize = 32.sp, color = Color.White)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val user = registeredUsers.find { it.username == username && it.password == password }
                    if (user != null) {
                        onLoginSuccess(user)
                    } else {
                        errorMessage = "Неверное имя пользователя или пароль"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Войти", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Нет аккаунта? Зарегистрироваться", color = Color.White)
            }
        }
    }
}

// ==================== ЭКРАН РЕГИСТРАЦИИ С КАРТИНКОЙ ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: (User) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var registeredUsers by remember { mutableStateOf(listOf<User>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновая картинка
        Image(
            painter = painterResource(id = R.drawable.ara2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Затемнение фона
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("📝 Регистрация", fontSize = 32.sp, color = Color.White)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                ),
                trailingIcon = {
                    TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Text(if (isPasswordVisible) "🙈" else "👁️")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Подтвердите пароль") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4CAF50),
                    cursorColor = Color(0xFF4CAF50)
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = when {
                        username.isBlank() -> "Введите имя пользователя"
                        email.isBlank() -> "Введите Email"
                        !email.contains("@") -> "Email должен содержать @"
                        password.isBlank() -> "Введите пароль"
                        password.length < 6 -> "Пароль должен быть минимум 6 символов"
                        password != confirmPassword -> "Пароли не совпадают"
                        registeredUsers.any { it.username == username } -> "Пользователь уже существует"
                        else -> null
                    }

                    if (errorMessage == null) {
                        val newUser = User(username, email, password, mutableListOf())
                        registeredUsers = registeredUsers + newUser
                        onRegisterSuccess(newUser)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Зарегистрироваться", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Уже есть аккаунт? Войти", color = Color.White)
            }
        }
    }
}

// ==================== ЭКРАН ГЕНЕРАТОРА С КАРТИНКОЙ ====================
@Composable
fun GeneratorScreen(
    navController: NavController,
    user: User,
    onUserUpdate: (User) -> Unit
) {
    var passwordLength by remember { mutableStateOf(12) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }
    var passwordName by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    fun generatePassword(): String {
        val uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lowercase = "abcdefghijkmnpqrstuvwxyz"
        val numbers = "23456789"
        val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        var charSet = ""
        if (includeUppercase) charSet += uppercase
        if (includeLowercase) charSet += lowercase
        if (includeNumbers) charSet += numbers
        if (includeSymbols) charSet += symbols

        if (charSet.isEmpty()) return "Выберите параметры"

        return (1..passwordLength).map { charSet.random() }.joinToString("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновая картинка
        Image(
            painter = painterResource(id = R.drawable.ara3),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Затемнение фона
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== ВЕРХНИЙ РЯД С КНОПКАМИ =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Уменьшил отступ снизу
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка "Выйти" - СЛЕВА
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("🚪 Выйти")
                }

                // Пустое место для баланса
                Spacer(modifier = Modifier.weight(1f))

                // Кнопка "Профиль" - СПРАВА
                Button(
                    onClick = { navController.navigate("profile") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("👤 Профиль")
                }
            }

            // ===== ЗАГОЛОВОК "ГЕНЕРАТОР" =====
            Text(
                "🔐 Генератор паролей",
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp) // Отступ сверху и снизу
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Сгенерированный пароль:", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (generatedPassword.isNotEmpty()) generatedPassword else "Нажмите кнопку",
                        fontSize = 20.sp,
                        color = Color(0xFF4CAF50),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { generatedPassword = generatePassword() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Сгенерировать")
                        }
                        Button(
                            onClick = {
                                if (generatedPassword.isNotEmpty()) {
                                    clipboardManager.setText(AnnotatedString(generatedPassword))
                                    snackbarMessage = "Пароль скопирован!"
                                    showSnackbar = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Копировать")
                        }
                    }

                    if (generatedPassword.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("💾 Сохранить пароль")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Настройки пароля", color = Color.White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Длина: $passwordLength", color = Color.White)
                    Slider(
                        value = passwordLength.toFloat(),
                        onValueChange = { passwordLength = it.toInt() },
                        valueRange = 4f..32f,
                        steps = 28,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4CAF50),
                            activeTrackColor = Color(0xFF4CAF50)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = includeUppercase, onCheckedChange = { includeUppercase = it })
                            Text("A-Z", color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = includeLowercase, onCheckedChange = { includeLowercase = it })
                            Text("a-z", color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = includeNumbers, onCheckedChange = { includeNumbers = it })
                            Text("0-9", color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = includeSymbols, onCheckedChange = { includeSymbols = it })
                            Text("!@#", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Сохранить пароль") },
            text = {
                Column {
                    Text("Введите название для пароля:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordName,
                        onValueChange = { passwordName = it },
                        placeholder = { Text("Например: ВКонтакте") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (passwordName.isNotBlank()) {
                        val newPassword = SavedPassword(
                            id = System.currentTimeMillis().toString(),
                            name = passwordName,
                            password = generatedPassword,
                            date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                        )
                        user.savedPasswords.add(newPassword)
                        onUserUpdate(user)
                        passwordName = ""
                        showSaveDialog = false
                        snackbarMessage = "Пароль сохранен!"
                        showSnackbar = true
                    }
                }) {
                    Text("Сохранить", color = Color(0xFF4CAF50))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Отмена", color = Color.Red)
                }
            }
        )
    }

    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showSnackbar = false }) {
                    Text("OK", color = Color.White)
                }
            }
        ) {
            Text(snackbarMessage)
        }
    }
}

// ==================== ЭКРАН ПРОФИЛЯ С КАРТИНКОЙ ====================
@Composable
fun ProfileScreen(
    navController: NavController,
    user: User
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновая картинка
        Image(
            painter = painterResource(id = R.drawable.ara4),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Затемнение фона
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👤 Профиль", fontSize = 28.sp, color = Color.White, modifier = Modifier.padding(bottom = 20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Информация о пользователе", color = Color(0xFF4CAF50), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("📝 Имя: ${user.username}", color = Color.White, fontSize = 16.sp)
                    Text("📧 Email: ${user.email}", color = Color.White, fontSize = 16.sp)
                    Text("🔐 Всего паролей: ${user.savedPasswords.size}", color = Color(0xFF4CAF50), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💾 История паролей", color = Color(0xFF4CAF50), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (user.savedPasswords.isEmpty()) {
                        Text("Нет сохраненных паролей", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.height(350.dp)) {
                            items(user.savedPasswords.reversed()) { savedPassword ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E).copy(alpha = 0.9f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(savedPassword.name, color = Color(0xFF4CAF50), fontSize = 16.sp)
                                        Text("Пароль: ${savedPassword.password}", color = Color.White, fontSize = 14.sp)
                                        Text(savedPassword.date, color = Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("generator") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("← Назад к генератору")
            }
        }
    }
}