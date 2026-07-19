package com.example.zabbixtrapperndk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zabbixtrapperndk.data.HostKeyPair
import com.example.zabbixtrapperndk.data.HostKeyPairStorage
import com.example.zabbixtrapperndk.data.TrapperHolder
import kotlinx.coroutines.launch

@Composable
fun SenderView(modifier: Modifier = Modifier, navController: NavController) {

    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(HostKeyPair("", "")) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {  }) {
                Icon(Icons.Default.Info, contentDescription = "Информация")
            }
            IconButton(onClick = {
                navController.navigate("settings")
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Настройки")
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = selectedItem?.let { "${it.host} ${it.key}" } ?: "",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                HostKeyPairStorage.items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.host} ${item.key}") },
                        onClick = {
                            selectedItem = item
                            expanded = false
                        }
                    )
                }
            }

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        TrapperHolder.trapper.send(selectedItem.host, selectedItem.key,  text)
                    }
                }
            ) {
                Text("Send")
            }

            Button(
                onClick = {
                    scope.launch {
                        TrapperHolder.trapper.send("Motorola-Host", "Motorola-Key", text)
                    }
                }
            ) {
                Text("Trash Button")
            }
        }
    }
}