package com.example.zabbixtrapperndk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zabbixtrapperndk.data.ConfigStorage
import com.example.zabbixtrapperndk.R
import com.example.zabbixtrapperndk.data.TrapperHolder

@Composable
fun SettingsView(modifier: Modifier = Modifier, navController: NavController) {

    var zabbixIp by remember { mutableStateOf("") }
    var zabbixHost by remember { mutableStateOf("") }
    var zabbixKey by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.return_button))
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp
                )
            ) {
                OutlinedTextField(
                    value = zabbixIp,
                    onValueChange = { zabbixIp = it },
                    modifier = Modifier.weight(3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        TrapperHolder.trapper.init(zabbixIp) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.init_button))
                }
            }

            OutlinedTextField(
                value = zabbixHost,
                onValueChange = { zabbixHost = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.host_input_placeholder)
                    )
                },
            )

            OutlinedTextField(
                value = zabbixKey,
                onValueChange = { zabbixKey = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.key_input_placeholder),
                    )
                },
            )

            Button(
                onClick = {
                    if (zabbixHost.isNotBlank() || zabbixKey.isNotBlank()) {
                        ConfigStorage.add(zabbixHost, zabbixKey)
                        zabbixHost = ""
                        zabbixKey = ""
                    }
                }
            ) {
                Text("+")
            }

            LazyColumn {
                items(
                    items = ConfigStorage.items,
                    key = { it.hashCode() }
                ) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.host}  ${item.key}")
                        IconButton(onClick = { ConfigStorage.remove(item) }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete_button))
                        }
                    }
                }
            }
        }

    }
}