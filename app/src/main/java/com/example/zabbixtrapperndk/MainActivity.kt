package com.example.zabbixtrapperndk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zabbixtrapperndk.data.ConfigRepository
import com.example.zabbixtrapperndk.ui.SenderView
import com.example.zabbixtrapperndk.ui.SettingsView
import com.example.zabbixtrapperndk.ui.theme.ZabbixTrapperNDKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConfigRepository.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            ZabbixTrapperNDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "sender") {
                        composable("sender") {
                            SenderView(
                                modifier = Modifier.padding(innerPadding),
                                navController
                            )
                        }
                        composable("settings") {
                            SettingsView(
                                modifier = Modifier.padding(innerPadding),
                                navController
                            )
                        }
                    }
                }
            }
        }
    }
}