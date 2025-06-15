package com.argentspirit.quickvault.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.argentspirit.quickvault.components.AddPasswordSheet
import com.argentspirit.quickvault.entities.Service
import com.argentspirit.quickvault.utility.BaseColorGenerator.getColorPaletteFromString
import com.argentspirit.quickvault.viewmodels.ServicesViewModel


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
    val pallet = getColorPaletteFromString(alt)
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(pallet.gradientStartColor, pallet.gradientEndColor)
    )

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
                text = alt.firstOrNull()?.toString() ?: "#" ,
                color = pallet.textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}



