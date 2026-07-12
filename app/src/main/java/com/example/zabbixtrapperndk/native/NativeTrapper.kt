package com.example.zabbixtrapperndk.native

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NativeTrapper {

    companion object {
        init {
            System.loadLibrary("zabbixtrapperndk")
        }
    }

    private var nativePtr: Long = 0
    private val lock = Any()
    private var isInitialized = false

    external fun createTrapper(ip: String): Long
    external fun destroyTrapper(ptr: Long)
    external fun dataSend(ptr: Long, host: String, key: String, data: String): Int

    fun init(ip: String) {
        synchronized(lock) {
            if (isInitialized) {
                destroyTrapper(nativePtr)
                isInitialized = false
            }
            nativePtr = createTrapper(ip)
            isInitialized = true
        }
    }

    fun cleanup() {
        synchronized(lock) {
            if (isInitialized) {
                destroyTrapper(nativePtr)
                isInitialized = false
                nativePtr = 0
            }
        }
    }

    fun send(host: String, key: String, data: String): Int {
        synchronized(lock) {
            if (!isInitialized) {
                return -1
            }
            return dataSend(nativePtr, host, key, data)
        }
    }
//    suspend fun send(host: String, key: String, data: String): Int = withContext(Dispatchers.IO) {
//        synchronized(lock) {
//            if (!isInitialized) {
//                return@synchronized -1
//            }
//            dataSend(nativePtr, host, key, data)
//        }
//    }
}