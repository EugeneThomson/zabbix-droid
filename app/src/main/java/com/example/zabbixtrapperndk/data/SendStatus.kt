package com.example.zabbixtrapperndk.data

import com.example.zabbixtrapperndk.R

enum class SendStatus {
    SUCCESS,
    FAILED_CONNECT,
    FAILED_SEND,
    TOO_LONG_MESSAGE,
    UNKNOWN
}

fun Int.toSendStatus(): SendStatus =
    when (this) {
        1 -> SendStatus.SUCCESS
        2 -> SendStatus.FAILED_CONNECT
        3 -> SendStatus.FAILED_SEND
        4 -> SendStatus.TOO_LONG_MESSAGE
        else -> SendStatus.UNKNOWN
    }

fun SendStatus.toMessageResId(): Int =
    when (this) {
        SendStatus.SUCCESS -> R.string.send_success
        SendStatus.FAILED_CONNECT -> R.string.send_failed_connect
        SendStatus.FAILED_SEND -> R.string.send_failed_send
        SendStatus.TOO_LONG_MESSAGE -> R.string.send_too_long_message
        SendStatus.UNKNOWN -> R.string.send_unknown_error
    }