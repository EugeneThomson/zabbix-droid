package com.example.zabbixtrapperndk

import androidx.compose.runtime.mutableStateListOf

object HostKeyPairHolder {
    private val _items = mutableStateListOf<HostKeyPair>()
    val items: List<HostKeyPair> = _items

    fun add(host: String, key: String) {
        _items.add(HostKeyPair(host, key))
    }

    fun remove(item: HostKeyPair) {
        _items.remove(item)
    }
}