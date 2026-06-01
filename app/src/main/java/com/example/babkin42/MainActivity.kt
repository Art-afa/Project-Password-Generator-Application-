package com.example.babkin42

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (User) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Список зарегистрированных пользователей (в реальном приложении это было бы в БД)
    var registeredUsers by remember { mutableStateOf(listOf<User>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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
                // Поиск пользователя
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var registrationSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ar),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // Контент по центру
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Уменьшенная ПРОЗРАЧНАЯ карточка регистрации
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Регистрация",
                        fontSize = 28.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Имя пользователя", color = Color.White) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage?.contains("имя") == true,
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),  // Зеленая рамка при фокусе
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF4CAF50)  // Зеленый курсор
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage?.contains("Email") == true,
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),  // Зеленая рамка при фокусе
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF4CAF50)  // Зеленый курсор
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль", color = Color.White) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage?.contains("Пароль") == true,
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),  // Зеленая рамка при фокусе
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF4CAF50)  // Зеленый курсор
                        ),
                        trailingIcon = {
                            TextButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Text(if (isPasswordVisible) "🙈" else "👁️", color = Color.White)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Подтвердите пароль", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage?.contains("совпадают") == true,
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4CAF50),  // Зеленая рамка при фокусе
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF4CAF50)  // Зеленый курсор
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 11.sp
                        )
                    }

                    if (registrationSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "✅ Регистрация успешна!\nИмя: $username\nEmail: $email",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            errorMessage = when {
                                username.isBlank() -> "Введите имя пользователя"
                                email.isBlank() -> "Введите Email"
                                !email.contains("@") -> "Email должен содержать @"
                                !email.contains(".") -> "Email должен содержать ."
                                password.isBlank() -> "Введите пароль"
                                password.length < 6 -> "Пароль должен быть не менее 6 символов"
                                password != confirmPassword -> "Пароли не совпадают"
                                else -> null
                            }

                            if (errorMessage == null) {
                                registrationSuccess = true
                            } else {
                                registrationSuccess = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)  // Зеленая кнопка
                        )
                    ) {
                        Text("Зарегистрироваться", fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            username = ""
                            email = ""
                            password = ""
                            confirmPassword = ""
                            errorMessage = null
                            registrationSuccess = false
                        }
                    ) {
                        Text("Очистить форму", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}