package com.example.zabbixtrapperndk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zabbixtrapperndk.R
import com.example.zabbixtrapperndk.data.HostKeyPair
import com.example.zabbixtrapperndk.data.ConfigStorage
import com.example.zabbixtrapperndk.data.TrapperHolder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SenderView(modifier: Modifier = Modifier, navController: NavController) {

    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf<HostKeyPair?>(null)
    }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Info, contentDescription = stringResource(R.string.information_button))
                }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItem?.let {
                            "${it.host} ${it.key}"
                        } ?: stringResource(R.string.host_key_placeholder),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ConfigStorage.items.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.host} ${item.key}") },
                                onClick = {
                                    selectedItem = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = {
                    navController.navigate("settings")
                }) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_button))
                }
            }
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.data_field_placeholder),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 35.sp
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 35.sp
                    )
                )
                Button(
                    onClick = {
                        scope.launch {
                            selectedItem?.let {
                                TrapperHolder.trapper.send(it.host, it.key, text)
                            }
                        }
                    },
                    modifier = Modifier
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.send_button))
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = stringResource(R.string.about_app_title),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.about_app_text).trimIndent(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.ok_button))
                        }
                    }
                )
            }

    }
}