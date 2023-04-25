package com.vision.scantexter.android.tools

import android.content.Context
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

const val TMP_FILE_PREFIX = "tmp_image_file"
const val TMP_FILE_EXT = ".jpg"
const val PROVIDER = ".file_provider"

suspend fun createTempPhotoFile(context: Context): File {
    val photoFile = withContext(Dispatchers.IO) {
        runCatching {
            val tmpFile = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_EXT, context.cacheDir)
                .apply {
                    createNewFile()
                    deleteOnExit()
                }
            tmpFile
        }.getOrElse { ex ->
            Timber.e(ex, "Failed to create temporary file")
            File("/dev/null")
        }
    }
    return photoFile
}