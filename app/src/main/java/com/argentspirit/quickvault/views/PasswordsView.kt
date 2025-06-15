package com.argentspirit.quickvault.views

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.PersistableBundle
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.argentspirit.quickvault.entities.PasswordEntry
import com.argentspirit.quickvault.utility.BaseColorGenerator.getColorPaletteFromString
import com.argentspirit.quickvault.viewmodels.PasswordsViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsView(navController: NavHostController, viewModel: PasswordsViewModel = hiltViewModel()){
    val service by viewModel.service.collectAsState()
    var isNavigating by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val pallet = getColorPaletteFromString(service?.service?.serviceName ?: "")
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(pallet.gradientStartColor, pallet.gradientEndColor)
    )
    var editServiceName by remember { mutableStateOf(false) }
    var tempServiceName by remember { mutableStateOf(service?.service?.serviceName ?: "") }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .clip( // Apply the clip modifier
                        shape = MaterialTheme.shapes.large.copy(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(0.dp)
                        )
                    ).then(
                        Modifier.background(
                            brush = gradientBrush
                        )
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = pallet.textColor
                ),

                title = {
                    if (!editServiceName) {
                        Text(
                            service?.service?.serviceName ?: "Unknown",
                            color = pallet.textColor
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            tempServiceName = service?.service?.serviceName ?: ""
                        }
                        val underlineColor = if(tempServiceName.isBlank()) Color.Red else pallet.textColor

                        BasicTextField(
                            value = tempServiceName,
                            onValueChange = { tempServiceName = it },
                            textStyle = MaterialTheme.typography.titleLarge.copy(color = pallet.textColor),
                            modifier = Modifier.fillMaxWidth(),
                            cursorBrush = SolidColor(pallet.textColor),
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Spacer(modifier = Modifier.height(1.dp).background(underlineColor).fillMaxWidth()) // Add some space)
                                }
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        enabled = !isNavigating,
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                navController.popBackStack()
                            }
                        },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = pallet.textColor
                        )
                    }
                },
                actions = {
                    if(editServiceName){
                        IconButton(onClick = { //TODO: if name overlaps suggest merge
                            editServiceName = false
                            viewModel.updateServiceName(tempServiceName)
                        }, enabled = tempServiceName.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Save Service Name",
                                tint = pallet.textColor
                            )
                        }
                        IconButton(onClick = {
                            editServiceName = false
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = "Cancel Edit Service Name",
                                tint = pallet.textColor)
                        }
                    } else {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More options",
                                tint = pallet.textColor
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            androidx.compose.material3.DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Add Password") },
                                text = { Text("Rename") },
                                onClick = {
                                    editServiceName = true
                                    showMenu = false
                                }
                            )
                            androidx.compose.material3.DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Filled.Add, contentDescription = "Add Password") },
                                text = { Text("Add Password") },
                                onClick = {
                                    /* TODO: Implement add password */
                                    showMenu = false
                                }
                            )
                            androidx.compose.material3.DropdownMenuItem(
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Delete Service") },
                                text = { Text("Delete Service") },
                                onClick = {
                                    showDeleteConfirmDialog = true
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },

    ){ innerPadding ->
        if(service == null){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Service not found")
                return@Scaffold
            }
        }
        if(showDeleteConfirmDialog){
            DeleteServiceDialog(onDismiss = { showDeleteConfirmDialog = false }, onConfirm = {
                viewModel.deleteService()
                navController.popBackStack()
            })
        }
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            service?.let { service ->
                items(service.passwordEntries) { password ->
                    PasswordEntryView(
                        password,
                        onSaveChanges = {
                            viewModel.updatePasswordEntry(it)
                        },
                        onNavigateToHistory = {
        //                        navController.navigate("/passwords/${it}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordEntryView(
    password: PasswordEntry,
    onSaveChanges: (updatedEntry: PasswordEntry) -> Unit, // Callback to save changes
    onNavigateToHistory: (entryId: Long) -> Unit,
    onLongClick: () -> Unit = {},
) {
    var expand by remember { mutableStateOf(false) }
    var isEditing by rememberSaveable { mutableStateOf(false) }

    // State for editable fields during edit mode
    val clipboardManager = LocalClipboard.current
    var editableUsername by rememberSaveable(password.username, isEditing) { mutableStateOf(password.username ?: "") }
    var editablePassword by rememberSaveable(password.password, isEditing) { mutableStateOf(password.password) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { expand = !expand },
                    onLongClick = { onLongClick() }
                )

                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    OutlinedTextField( // Username field in edit mode
                        value = editableUsername,
                        onValueChange = { editableUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = if (password.username.isNullOrEmpty()) "Empty" else password.username,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        color = if (password.username.isNullOrEmpty())
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.onSurface
                    )
                }
                if(!isEditing) {
                    val iconRotation by animateFloatAsState(targetValue = if (expand) 180f else 0f, label = "arrowRotation")
                    IconButton(onClick = { expand = !expand }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown, // Arrow always points down, rotate it
                            contentDescription = if (!expand) "Expand password details" else "Collapse password details",
                            modifier = Modifier.rotate(iconRotation)
                        )
                    }
                }

            }
            if (expand) {
                Spacer(modifier = Modifier.height(8.dp))
                EditablePasswordTextField(editablePassword, { if(isEditing) editablePassword = it }, isEditable = isEditing)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditing) {
                        IconButton(onClick = {
                            onSaveChanges(
                                password.copy(
                                    username = editableUsername.takeIf { it.isNotBlank() },
                                    password = editablePassword
                                )
                            )
                            isEditing = false
                            expand = true
                        }) {
                            Icon(Icons.Filled.Save, contentDescription = "Save Changes")
                        }
                        IconButton(onClick = {
                            isEditing = false
                            editableUsername = password.username ?: ""
                            editablePassword = password.password
                        }) {
                            Icon(Icons.Filled.Cancel, contentDescription = "Cancel Edits")
                        }
                    } else {
                        IconButton(onClick = {
                            isEditing = true
                            expand = true
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Entry")
                        }
                        IconButton(onClick = {
                            onNavigateToHistory(password.id)
                        }) {
                            Icon(Icons.Filled.History, contentDescription = "View Password History")
                        }

                        IconButton(onClick = {
                            val clip = ClipData.newPlainText("password", password.password)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val extras = PersistableBundle().apply {
                                    putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                                }
                                val newDescription = ClipDescription(clip.description).apply {
                                    setExtras(extras)
                                }
                                val sensitiveClipData = ClipData(newDescription, clip.getItemAt(0))
                                clipboardManager.nativeClipboard.setPrimaryClip(
                                    sensitiveClipData
                                )
                            } else {
                                clipboardManager.nativeClipboard.setPrimaryClip(
                                    clip
                                )
                            }
                        }) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Password")
                        }

                        val currentDateTime = LocalDateTime.now() // TODO: Replace with last modified date
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy",
                            Locale.getDefault())
                        val formattedCurrentDateTime = currentDateTime.format(formatter)
                        Text( //TODO: Change how this is displayed
                            text = formattedCurrentDateTime,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun EditablePasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    if(isEditable){
        var passwordVisible by rememberSaveable { mutableStateOf(isEditable) }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
    } else{
        var passwordVisible by rememberSaveable { mutableStateOf(isEditable) }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = { Text("Password") },
            readOnly = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
    }

}

@Composable
fun DeleteServiceDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this service and all its passwords? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



