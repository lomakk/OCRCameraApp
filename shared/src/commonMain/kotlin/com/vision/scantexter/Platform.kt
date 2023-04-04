package com.vision.scantexter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform