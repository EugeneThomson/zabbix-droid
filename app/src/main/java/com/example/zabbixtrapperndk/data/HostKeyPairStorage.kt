package com.example.zabbixtrapperndk.data

import androidx.compose.runtime.mutableStateListOf

object HostKeyPairStorage {
    private val _items = mutableStateListOf<HostKeyPair>()
    val items: List<HostKeyPair> = _items

    fun add(host: String, key: String) {
        _items.add(HostKeyPair(host, key))
    }

    fun remove(item: HostKeyPair) {
        _items.remove(item)
    }
}