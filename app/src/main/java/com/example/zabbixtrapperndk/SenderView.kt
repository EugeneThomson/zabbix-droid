package com.example.zabbixtrapperndk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.random.Random

@Composable
fun SenderView(modifier: Modifier = Modifier, navController: NavController) {

//    var zabbixIp by remember { mutableStateOf("") }
    var zabbixHost by remember { mutableStateOf("") }
    var zabbixKey by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

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

//            TextField(
//                value = zabbixHost,
//                onValueChange = { zabbixHost = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            TextField(
//                value = zabbixKey,
//                onValueChange = { zabbixKey = it },
//                modifier = Modifier.fillMaxWidth()
//            )

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { TrapperHolder.trapper.send(zabbixHost, zabbixKey, text) }
            ) {
                Text("Send")
            }

            Button(
                onClick = {
                    TrapperHolder.trapper.send("Motorola-Host", "Motorola-Key", text)
                }
            ) {
                Text("Trash Button")
            }
        }
    }
}