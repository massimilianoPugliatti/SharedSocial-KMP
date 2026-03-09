package com.example.sharedsocial_kmp.service

interface IosCameraControllerBridge {
    fun start()
    fun stop()
    fun switchCamera()
    fun capturePhoto()
    fun startRecording()
    fun stopRecording()
}