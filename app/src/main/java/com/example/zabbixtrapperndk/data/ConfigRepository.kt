package com.example.zabbixtrapperndk.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.zabbixtrapperndk.native.NativeTrapper
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object ConfigRepository {
    val trapper = NativeTrapper()

    private lateinit var appContext: Context
    private const val FILENAME = "host_key_pairs.json"
    private val _items = mutableStateListOf<HostKeyPair>()
    val items: List<HostKeyPair> = _items
    var ip by mutableStateOf<String?>(null)
        private set

    fun init(context: Context) {
        if (::appContext.isInitialized) return
        appContext = context.applicationContext
        load()
    }

    fun add(host: String, key: String) {
        _items.add(HostKeyPair(host, key))
        save()
    }

    fun remove(item: HostKeyPair) {
        _items.remove(item)
        save()
    }

    fun updateIp(ip: String) {
        trapper.init(ip)
        this.ip = ip
        save()
    }

    suspend fun sendData(host: String, key: String, data: String): Int {
        return trapper.send(host, key, data)
    }

    private fun save() {
        val file = File(appContext.filesDir, FILENAME)
        val configData = ConfigData(
            ip = this.ip,
            items = _items.toList()
        )
        val json = Json.encodeToString(configData)
        file.writeText(json)
    }

    private fun load() {
        val file = File(appContext.filesDir, FILENAME)
        if (file.exists()) {
            val json = file.readText()
            try {
                val decodedConfig = Json.decodeFromString<ConfigData>(json)
                _items.clear()
                _items.addAll(decodedConfig.items)
                decodedConfig.ip?.let { savedIp ->
                    trapper.init(savedIp)
                    this.ip = savedIp
                }
            } catch (e: Exception) {
                _items.clear()
            }
        }
    }
}