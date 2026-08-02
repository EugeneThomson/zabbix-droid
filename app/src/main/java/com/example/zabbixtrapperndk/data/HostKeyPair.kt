package com.example.zabbixtrapperndk.data

import kotlinx.serialization.Serializable

@Serializable
data class HostKeyPair(val host: String, val key: String)

@Serializable
data class ConfigData(
    val ip: String,
    val items: List<HostKeyPair>
)