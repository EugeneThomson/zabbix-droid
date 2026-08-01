package com.example.zabbixtrapperndk.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object HostKeyPairStorage {
    private lateinit var appContext: Context
    private const val FILENAME = "host_key_pairs.json"
    private val _items = mutableStateListOf<HostKeyPair>()
    val items: List<HostKeyPair> = _items

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

    private fun save() {
        val file = File(appContext.filesDir, FILENAME)
        val json = Json.encodeToString(items)
        file.writeText(json)
    }

    private fun load() {
        val file = File(appContext.filesDir, FILENAME)
        if (file.exists()) {
            val json = file.readText()
            try {
                val decodedItems = Json.decodeFromString<List<HostKeyPair>>(json)
                _items.clear()
                _items.addAll(decodedItems)
            } catch (e: Exception) {
                _items.clear()
            }

        }
    }
}