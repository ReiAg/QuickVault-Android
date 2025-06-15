package com.argentspirit.quickvault.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.argentspirit.quickvault.viewmodels.ServicesViewModel
import java.security.SecureRandom

private const val DEFAULT_PASSWORD_LENGTH = 16
private const val ALLOWED_CHARS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
private const val ALLOWED_CHARS_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
private const val ALLOWED_CHARS_NUMBERS = "0123456789"
private const val ALLOWED_CHARS_SYMBOLS = "!@#$%^&*()_+-=[]{}|;':,.<>?/~"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddPasswordSheet(
    sheetState: SheetState,
    viewModel: ServicesViewModel,
    onDismiss: () -> Unit
){
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        var serviceName by remember { mutableStateOf("") }
        var userName by remember { mutableStateOf("") }
        var userPassword by remember { mutableStateOf("") }
        var serviceNameError by remember { mutableStateOf<String?>(null) }
        var userPasswordError by remember { mutableStateOf<String?>(null) }
        val serviceNames = viewModel.services.collectAsState().value.map { it.serviceName }
        val userNames by viewModel.usernames.collectAsState()

        var passwordLength by remember { mutableStateOf(DEFAULT_PASSWORD_LENGTH) }
        var includeUppercase by remember { mutableStateOf(true) }
        var includeLowercase by remember { mutableStateOf(true) }
        var includeNumbers by remember { mutableStateOf(true) }
        var includeSymbols by remember { mutableStateOf(true) }

        val finishAction = {
            var hasError = false
            if (serviceName.isBlank()) {
                serviceNameError = "Service name cannot be empty"
                hasError = true
            } else {
                serviceNameError = null
            }
            if (userPassword.isBlank()) {
                userPasswordError = "Password cannot be empty"
                hasError = true
            } else {
                userPasswordError = null
            }
            if (!hasError) {
                viewModel.AddPassword(serviceName, userName, userPassword)
                onDismiss()
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            val focusManager = LocalFocusManager.current
            Text(
                "New password",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
            AutocompleteTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = "Service Name",
                suggestionsList = serviceNames,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)}),
                onSuggestionSelected = {
                    serviceName = it
                    focusManager.moveFocus(FocusDirection.Next)
                },
                isError = serviceNameError != null
            )
            serviceNameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                )
            }


            AutocompleteTextField(
                value = userName,
                onValueChange = { userName = it },
                suggestionsList = userNames,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)}),
                onSuggestionSelected = {
                    userName = it
                    focusManager.moveFocus(FocusDirection.Next)
                },
                label = "Username (Optional)"
            )

            var passwordOptionsVisible by remember { mutableStateOf(false) }

            // Password Generation Options


            OutlinedTextField(
                value = userPassword,
                onValueChange = { userPassword = it },
                label = {
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = "Password"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    finishAction()
                })
                , isError = userPasswordError != null)
            userPasswordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                )
            }
            //TODO: add easy to type option
            val allowedChars = buildString {
                if (includeLowercase) append(ALLOWED_CHARS_LOWERCASE)
                if (includeUppercase) append(ALLOWED_CHARS_UPPERCASE)
                if (includeNumbers) append(ALLOWED_CHARS_NUMBERS)
                if (includeSymbols) append(ALLOWED_CHARS_SYMBOLS)

            }
            Button( //TODO: change button look
                enabled = allowedChars.isNotEmpty(),
                onClick = {
                    userPassword = GeneratePassword(passwordLength, allowedChars)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Secure Password")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Password Generation Options", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { passwordOptionsVisible = !passwordOptionsVisible }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (passwordOptionsVisible) "Hide Options" else "Show Options"
                    )
                }
            }
            AnimatedVisibility(
                visible = passwordOptionsVisible,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = passwordLength.toString(),
                            onValueChange = {
                                passwordLength = it.toIntOrNull() ?: DEFAULT_PASSWORD_LENGTH
                            },
                            label = { Text("Password Length") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = includeUppercase,
                            onCheckedChange = { includeUppercase = it }
                        )
                        Text("Include Uppercase (A-Z)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = includeLowercase,
                            onCheckedChange = { includeLowercase = it }
                        )
                        Text("Include Lowercase (a-z)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = includeNumbers,
                            onCheckedChange = { includeNumbers = it }
                        )
                        Text("Include Numbers (0-9)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = includeSymbols,
                            onCheckedChange = { includeSymbols = it }
                        )
                        Text("Include Symbols (!@#...)")
                    }
                }
            }


            Button(onClick = {
                finishAction()
            }) {
                Text("Save")
            }
        }
    }
}

private fun GeneratePassword(length: Int, allowedCharSet: String): String {
    if (length <= 0 || allowedCharSet.isEmpty()) {
        return "" // Or throw an IllegalArgumentException
    }
    val random = SecureRandom()
    val password = StringBuilder(length)
    for (i in 0 until length) {
        val randomIndex = random.nextInt(allowedCharSet.length)
        password.append(allowedCharSet[randomIndex])
    }

    return password.toString()
}