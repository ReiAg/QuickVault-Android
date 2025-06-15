package com.argentspirit.quickvault.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

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