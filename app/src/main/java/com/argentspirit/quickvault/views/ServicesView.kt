package com.argentspirit.quickvault.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.argentspirit.quickvault.entities.Service
import com.argentspirit.quickvault.viewmodels.ServicesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesView(viewModel: ServicesViewModel = hiltViewModel()){
    val services by viewModel.services.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // If you don't want a partially expanded state
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),

                title = {
                    Text("Passwords")
                }
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            if(services.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(
                        "No saved passwords",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.surfaceTint,
                    )
                }
            } else {
                LazyColumn() {
                    items(services) { service ->
                        ServiceWidget(service, modifier = Modifier.fillMaxWidth()) {

                        }
                    }
                }
            }
            if (showBottomSheet){
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    var serviceName by remember { mutableStateOf("") }
                    var userName by remember { mutableStateOf("") }
                    var userPassword by remember { mutableStateOf("") }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(
                            rememberScrollState()
                        )
                    ) {
                        val focusManager = LocalFocusManager.current
                        Text("New password", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = serviceName,
                            onValueChange = { serviceName = it },
                            label = {
                                Text(
                                    modifier = Modifier.padding(0.dp),
                                    text = "Service"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)})
                        )
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = {
                                Text(
                                    modifier = Modifier.padding(0.dp),
                                    text = "Username"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)})
                        )
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
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Next)})
                        )
                        Button(onClick = {
                            showBottomSheet = false
                            viewModel.AddPassword(serviceName, userName, userPassword)
                        }) {
                            Text("Save")
                        }
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Password")
                }
            }
        }

    }
}

@Composable
fun ServiceWidget(service: Service, modifier: Modifier = Modifier, onClick: () -> Unit){
    Box(modifier = modifier.clickable(onClick = onClick)){
        Row (
            modifier = modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            ProfilePicture(null, service.serviceName)
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(service.serviceName)
            }
        }
    }

}

@Composable
fun ProfilePicture(
    imageBitmap: ImageBitmap?,
    alt: String,
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
){
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Service Image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
    } else {
        val firstLetter = alt.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(bgColor)
        ) {
            Text(
                text = firstLetter,
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}