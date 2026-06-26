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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context.applicationContext as Application)
    )

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()

    NavHost(navController = navController, startDestination = if (isLoggedIn) "generator" else "login") {
        composable("login") {
            LoginScreen(navController, viewModel)
        }
        composable("register") {
            RegisterScreen(navController, viewModel)
        }
        composable("generator") {
            GeneratorScreen(navController, viewModel)
        }
        composable("profile") {
            ProfileScreen(navController, viewModel)
        }
    }
}

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    val username by viewModel::username
    val password by viewModel::password
    val errorMessage by viewModel::errorMessage
    val isLoading by viewModel::isLoading

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ara1),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
            Text("Вход", fontSize = 32.sp, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username = it },
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
                onValueChange = { viewModel.password = it },
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

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isLoading
            ) {
                Text("Войти", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Нет аккаунта? Зарегистрироваться", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    val username by viewModel::username
    val email by viewModel::email
    val password by viewModel::password
    val confirmPassword by viewModel::confirmPassword
    val errorMessage by viewModel::errorMessage
    val isLoading by viewModel::isLoading
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.ara2),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
            Text("Регистрация", fontSize = 32.sp, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username = it },
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
                onValueChange = { viewModel.email = it },
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
                onValueChange = { viewModel.password = it },
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
                onValueChange = { viewModel.confirmPassword = it },
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

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                enabled = !isLoading
            ) {
                Text("Зарегистрироваться", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Уже есть аккаунт? Войти", color = Color.White)
            }
        }
    }
}

@Composable
fun GeneratorScreen(
    navController: NavController,
    viewModel: UserViewModel
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
        Image(
            painter = painterResource(id = R.drawable.ara3),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.logout()
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Выйти", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navController.navigate("profile") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("Профиль", color = Color.White)
                }
            }

            Text("Генератор паролей", fontSize = 28.sp, color = Color.White, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))

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

                    if (generatedPassword.isNotEmpty()) {
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) {
                            Text("Сохранить пароль", color = Color.White)
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { generatedPassword = generatePassword() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Сгенерировать", color = Color.White)
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
                    Text("📋 Копировать", color = Color.White)
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
                        placeholder = { Text("Например: Егор") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (passwordName.isNotBlank()) {
                        viewModel.savePassword(passwordName, generatedPassword)
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

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: UserViewModel
) {
    val currentUsername by viewModel.currentUsername.collectAsState()
    val passwords by viewModel.getPasswords().collectAsState(initial = emptyList())
    val clipboardManager = LocalClipboardManager.current
    var snackbarMessage by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновая картинка
        Image(
            painter = painterResource(id = R.drawable.ara4),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("generator") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("← Назад", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Image(
                painter = painterResource(id = R.drawable.ara43),
                contentDescription = "Аватар пользователя",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF4CAF50), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUsername ?: "Пользователь",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Профиль", fontSize = 28.sp, color = Color.White, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Информация о пользователе", color = Color(0xFF4CAF50), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("📝 Имя: ${currentUsername ?: "Неизвестно"}", color = Color.White, fontSize = 16.sp)
                    Text("🔐 Всего паролей: ${passwords.size}", color = Color(0xFF4CAF50), fontSize = 14.sp)
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

                    if (passwords.isEmpty()) {
                        Text("Нет сохраненных паролей", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.height(350.dp)) {
                            items(passwords) { savedPassword ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E).copy(alpha = 0.9f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(savedPassword.passwordName, color = Color(0xFF4CAF50), fontSize = 16.sp)
                                            Text("Пароль: ${savedPassword.password}", color = Color.White, fontSize = 14.sp)
                                            Text(savedPassword.date, color = Color.Gray, fontSize = 10.sp)
                                        }

                                        TextButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(savedPassword.password))
                                                snackbarMessage = "Пароль скопирован!"
                                                showSnackbar = true
                                            }
                                        ) {


                                            Text(" Копировать", fontSize = 12.sp, color = Color(0xFF4CAF50))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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