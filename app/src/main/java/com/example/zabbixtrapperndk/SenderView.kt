package com.example.zabbixtrapperndk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import kotlin.random.Random

@Composable
fun SenderView(modifier: Modifier = Modifier) {
    val trapper = remember { NativeTrapper() }

    DisposableEffect(Unit) {
        onDispose {
            trapper.cleanup()
        }
    }
    
    var zabbixIp by remember { mutableStateOf("") }
    var zabbixHost by remember { mutableStateOf("") }
    var zabbixKey by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = zabbixIp,
                onValueChange = { zabbixIp = it },
                modifier = Modifier.weight(3f)
            )

            Button(
                onClick = { trapper.init(zabbixIp) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Init Server")
            }
        }

        TextField(
            value = zabbixHost,
            onValueChange = { zabbixHost = it },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = zabbixKey,
            onValueChange = { zabbixKey = it },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { trapper.send(zabbixHost, zabbixKey, text) }
        ) {
            Text("Send")
        }

        Button(
            onClick = {
                trapper.send("Motorola-Host", "Motorola-Key", text)
            }
        ) {
            Text("Trash Button")
        }
    }
}