package com.vision.scantexter.android.di

import com.vision.scantexter.android.screen.ocr.OCRViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { OCRViewModel() }
}