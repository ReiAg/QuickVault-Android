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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.argentspirit.quickvault.entities.Service
import com.argentspirit.quickvault.viewmodels.PasswordsViewModel
import com.argentspirit.quickvault.viewmodels.ServicesViewModel
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesView(navController: NavHostController, viewModel: ServicesViewModel = hiltViewModel()){
    val services by viewModel.services.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // If you don't want a partially expanded state
    )
//    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .clip( // Apply the clip modifier
                        shape = MaterialTheme.shapes.large.copy(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(0.dp)
                        )
                    ),
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
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()){
            if(services.isEmpty()){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(
                        "No saved passwords",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.surfaceTint,
                    )
                }
            } else {
                val groupedServices = services.sortedBy { it.serviceName }.groupBy { it.serviceName.first().uppercaseChar() }
                LazyColumn {
                    groupedServices.forEach { (initial, servicesInGroup) ->
                        item {
                            Text(
                                text = initial.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(servicesInGroup) { service ->
                            ServiceWidget(service, modifier = Modifier.fillMaxWidth()) {
                                navController.navigate("/services/${service.id}")
                            }
                        }

                    }
                }
            }
            if (showBottomSheet){
                AddPasswordSheet(sheetState, viewModel){
                    showBottomSheet = false
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = true
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Password")
                }
            }
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddPasswordSheet(
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

            Button(onClick = {
                finishAction()
            }) {
                Text("Save")
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
    modifier: Modifier = Modifier
){
    val firstTwoLetters = alt.take(2).uppercase()
    val baseColor = remember(firstTwoLetters) {
        // Generate a color based on the first letter
        val random = Random(firstTwoLetters.hashCode()+16)
        Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            baseColor,
            baseColor.copy(alpha = 0.7f) // Lighter shade for gradient
        )
    )
    val textColor = if (baseColor.luminance() > 0.5) Color.Black else Color.White

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
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(gradientBrush)
        ) {
            Text(
                text = firstTwoLetters.firstOrNull()?.toString() ?: "#" ,
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suggestionsList: List<String>,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    isError: Boolean = false,
    onSuggestionSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredServices = remember(value, suggestionsList) {
        if (value.isEmpty()) {
            suggestionsList // Show all if input is empty
        } else {
            suggestionsList.filter { it.contains(value, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded && filteredServices.isNotEmpty(), // Only expand if there are suggestions
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true // Keep dropdown open while typing if there are matches
            },
            label = { Text(label) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable, expanded) // Important for positioning the dropdown
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError
        )

        ExposedDropdownMenu(
            expanded = expanded && filteredServices.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filteredServices.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        expanded = false
                        onSuggestionSelected(selectionOption)
                    }
                )
            }
        }
    }
}
