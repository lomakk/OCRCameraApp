package com.vision.scantexter.android.camera.extensions

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val vuBuffer = planes[2].buffer // VU

    val ySize = yBuffer.remaining()
    val vuSize = vuBuffer.remaining()

    val nv21 = ByteArray(ySize + vuSize)

    yBuffer.get(nv21, 0, ySize)
    vuBuffer.get(nv21, ySize, vuSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

@SuppressLint("UnsafeOptInUsageError")
fun ImageProxy.getByteArray(): ByteArray? {
    this.image?.let {
        val nv21Buffer = ImageProxyUtils.yuv420ThreePlanesToNV21(
            it.planes, this.width, this.height
        )

        return ByteArray(nv21Buffer.remaining()).apply {
            nv21Buffer.get(this)
        }
    }

    return null
}

fun ImageProxy.imageProxyToByteArray(): ByteArray {
    val yuvBytes = ByteArray(this.width * (this.height + this.height / 2))
    val yPlane = this.planes[0].buffer
    val uPlane = this.planes[1].buffer
    val vPlane = this.planes[2].buffer

    yPlane.get(yuvBytes, 0, this.width * this.height)

    val chromaRowStride = this.planes[1].rowStride
    val chromaRowPadding = chromaRowStride - this.width / 2

    var offset = this.width * this.height
    if (chromaRowPadding == 0) {

        uPlane.get(yuvBytes, offset, this.width * this.height / 4)
        offset += this.width * this.height / 4
        vPlane.get(yuvBytes, offset, this.width * this.height / 4)
    } else {
        for (i in 0 until this.height / 2) {
            uPlane.get(yuvBytes, offset, this.width / 2)
            offset += this.width / 2
            if (i < this.height / 2 - 2) {
                uPlane.position(uPlane.position() + chromaRowPadding)
            }
        }
        for (i in 0 until this.height / 2) {
            vPlane.get(yuvBytes, offset, this.width / 2)
            offset += this.width / 2
            if (i < this.height / 2 - 1) {
                vPlane.position(vPlane.position() + chromaRowPadding)
            }
        }
    }

    return yuvBytes
}

object ImageProxyUtils {
    fun yuv420ThreePlanesToNV21(
        yuv420888planes: Array<Image.Plane>,
        width: Int,
        height: Int
    ): ByteBuffer {
        val imageSize = width * height
        val out = ByteArray(imageSize + 2 * (imageSize / 4))
        if (areUVPlanesNV21(yuv420888planes, width, height)) {

            yuv420888planes[0].buffer[out, 0, imageSize]
            val uBuffer = yuv420888planes[1].buffer
            val vBuffer = yuv420888planes[2].buffer
            vBuffer[out, imageSize, 1]
            uBuffer[out, imageSize + 1, 2 * imageSize / 4 - 1]
        } else {
            unpackPlane(yuv420888planes[0], width, height, out, 0, 1)
            unpackPlane(yuv420888planes[1], width, height, out, imageSize + 1, 2)
            unpackPlane(yuv420888planes[2], width, height, out, imageSize, 2)
        }
        return ByteBuffer.wrap(out)
    }

    private fun areUVPlanesNV21(planes: Array<Image.Plane>, width: Int, height: Int): Boolean {
        val imageSize = width * height
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val vBufferPosition = vBuffer.position()
        val uBufferLimit = uBuffer.limit()

        vBuffer.position(vBufferPosition + 1)
        uBuffer.limit(uBufferLimit - 1)

        val areNV21 =
            vBuffer.remaining() == 2 * imageSize / 4 - 2 && vBuffer.compareTo(uBuffer) == 0

        vBuffer.position(vBufferPosition)
        uBuffer.limit(uBufferLimit)
        return areNV21
    }

    private fun unpackPlane(
        plane: Image.Plane,
        width: Int,
        height: Int,
        out: ByteArray,
        offset: Int,
        pixelStride: Int
    ) {
        val buffer = plane.buffer
        buffer.rewind()
        val numRow = (buffer.limit() + plane.rowStride - 1) / plane.rowStride
        if (numRow == 0) {
            return
        }
        val scaleFactor = height / numRow
        val numCol = width / scaleFactor

        var outputPos = offset
        var rowStart = 0
        for (row in 0 until numRow) {
            var inputPos = rowStart
            for (col in 0 until numCol) {
                out[outputPos] = buffer[inputPos]
                outputPos += pixelStride
                inputPos += plane.pixelStride
            }
            rowStart += plane.rowStride
        }
    }
}