package com.thomaskioko.tvmaniac

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}