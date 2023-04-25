package com.vision.scantexter.android.model

import android.graphics.Point
import android.graphics.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.google.mlkit.vision.text.Text
import com.vision.scantexter.android.theme.ColorTextRecognition
import com.vision.scantexter.android.theme.ColorTextSelection

interface OCRObjectInterface {
    val text: String
    val rect: Rect
    val topLeftCoordinate: Point
    val topRightCoordinate: Point
    val bottomRightCoordinate: Point
    val bottomLeftCoordinate: Point
    val path: Path
    val scaleFactorX: Float
    val scaleFactorY: Float
}

data class OCRText(
    val text: String,
    val blocks: List<OCRBlock>
)

data class OCRBlock(
    override val text: String,
    override val rect: Rect,
    override val topLeftCoordinate: Point,
    override val topRightCoordinate: Point,
    override val bottomRightCoordinate: Point,
    override val bottomLeftCoordinate: Point,
    override val path: Path,
    override val scaleFactorX: Float,
    override val scaleFactorY: Float,
    val recognizedTextLang: String,
    val lines: List<OCRLine>,
    val linesCount: Int,
    val selectedText: String
) : OCRObjectInterface

data class OCRLine(
    override val text: String,
    override val rect: Rect,
    override val topLeftCoordinate: Point,
    override val topRightCoordinate: Point,
    override val bottomRightCoordinate: Point,
    override val bottomLeftCoordinate: Point,
    override val path: Path,
    override val scaleFactorX: Float,
    override val scaleFactorY: Float,
    val recognizedTextLang: String,
    val elements: List<OCRElement>,
    val confidenceScore: Float,
    val rotationDegree: Float,
    val elementCount: Int,
    val selected: Boolean = false
) : OCRObjectInterface {
    fun getLineColor(): Color {
        return if (selected) {
            ColorTextSelection.copy(alpha = 0.4f)
        } else {
            ColorTextRecognition.copy(alpha = 0.4f)
        }
    }
}

data class OCRElement(
    override val text: String,
    override val rect: Rect,
    override val topLeftCoordinate: Point,
    override val topRightCoordinate: Point,
    override val bottomRightCoordinate: Point,
    override val bottomLeftCoordinate: Point,
    override val path: Path,
    override val scaleFactorX: Float,
    override val scaleFactorY: Float,
    val recognizedTextLang: String,
    val symbols: List<OCRSymbol>,
    val confidenceScore: Float,
    val rotationDegree: Float,
    val symbolCount: Int
) : OCRObjectInterface

data class OCRSymbol(
    override val text: String,
    override val rect: Rect,
    override val topLeftCoordinate: Point,
    override val topRightCoordinate: Point,
    override val bottomRightCoordinate: Point,
    override val bottomLeftCoordinate: Point,
    override val scaleFactorX: Float,
    override val scaleFactorY: Float,
    override val path: Path,
    val confidenceScore: Float,
    val rotationDegree: Float
) : OCRObjectInterface

fun Text.Symbol.mapToUi(
    scaleFactorX: Float,
    scaleFactorY: Float
): OCRSymbol? {
    with(this) {
        val rect = boundingBox
        val corners = cornerPoints?.getUpdatedCorners(
            scaleFactorX = scaleFactorX,
            scaleFactorY = scaleFactorY
        )
        val path = corners?.createOCRRectPath(rotationDegree = angle)
        if (rect != null && corners != null && path != null) {
            return OCRSymbol(
                text = text,
                rect = getUpdatedRectangle(
                    rect = rect,
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                ),
                topLeftCoordinate = corners[0],
                topRightCoordinate = corners[1],
                bottomRightCoordinate = corners[2],
                bottomLeftCoordinate = corners[3],
                path = path,
                confidenceScore = confidence,
                rotationDegree = angle,
                scaleFactorX = scaleFactorX,
                scaleFactorY = scaleFactorY
            )
        }
    }
    return null
}

fun Text.Element.mapToUi(
    scaleFactorX: Float,
    scaleFactorY: Float
): OCRElement? {
    with(this) {
        val rect = boundingBox
        val corners = cornerPoints?.getUpdatedCorners(
            scaleFactorX = scaleFactorX,
            scaleFactorY = scaleFactorY
        )
        val path = corners?.createOCRRectPath(rotationDegree = angle)
        if (rect != null && corners != null && path != null) {
            return OCRElement(
                text = text,
                rect = getUpdatedRectangle(
                    rect = rect,
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                ),
                topLeftCoordinate = corners[0],
                topRightCoordinate = corners[1],
                bottomRightCoordinate = corners[2],
                bottomLeftCoordinate = corners[3],
                path = path,
                confidenceScore = confidence,
                rotationDegree = angle,
                symbolCount = symbols.count(),
                symbols = symbols.mapNotNull {
                    it.mapToUi(
                        scaleFactorX = scaleFactorX,
                        scaleFactorY = scaleFactorY
                    )
                },
                recognizedTextLang = recognizedLanguage,
                scaleFactorX = scaleFactorX,
                scaleFactorY = scaleFactorY
            )
        }
    }
    return null
}

fun Text.Line.mapToUi(
    scaleFactorX: Float,
    scaleFactorY: Float
): OCRLine? {
    with(this) {
        val rect = boundingBox
        val corners = cornerPoints?.getUpdatedCorners(
            scaleFactorX = scaleFactorX,
            scaleFactorY = scaleFactorY
        )
        val path = corners?.createOCRRectPath(rotationDegree = angle)
        if (rect != null && corners != null && path != null) {
            return OCRLine(
                text = text,
                rect = getUpdatedRectangle(
                    rect = rect,
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                ),
                topLeftCoordinate = corners[0],
                topRightCoordinate = corners[1],
                bottomRightCoordinate = corners[2],
                bottomLeftCoordinate = corners[3],
                path = path,
                confidenceScore = confidence,
                rotationDegree = angle,
                elementCount = elements.count(),
                elements = elements.mapNotNull {
                    it.mapToUi(
                        scaleFactorX = scaleFactorX,
                        scaleFactorY = scaleFactorY
                    )
                },
                recognizedTextLang = recognizedLanguage,
                scaleFactorX = scaleFactorX,
                scaleFactorY = scaleFactorY,
                selected = true
            )
        }
    }
    return null
}

fun Text.TextBlock.mapToUi(
    scaleFactorX: Float,
    scaleFactorY: Float
): OCRBlock? {
    with(this) {
        val rect = boundingBox
        val corners = cornerPoints?.getUpdatedCorners(
            scaleFactorX = scaleFactorX,
            scaleFactorY = scaleFactorY
        )
        val path = corners?.createOCRRectPath(rotationDegree = 0f)
        if (rect != null && corners != null && path != null) {
            val convertedLines = this.lines.mapNotNull {
                it.mapToUi(
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                )
            }
            return OCRBlock(
                text = text,
                rect = getUpdatedRectangle(
                    rect = rect,
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                ),
                topLeftCoordinate = corners[0],
                topRightCoordinate = corners[1],
                bottomRightCoordinate = corners[2],
                bottomLeftCoordinate = corners[3],
                path = path,
                linesCount = lines.count(),
                lines = convertedLines,
                recognizedTextLang = recognizedLanguage,
                scaleFactorX = scaleFactorX,
                scaleFactorY = scaleFactorY,
                selectedText = convertedLines.filter { it.selected }.joinToString("") { it.text }
            )
        }
    }
    return null
}

fun Text.mapToUi(
    scaleFactorX: Float,
    scaleFactorY: Float
): OCRText {
    with(this) {
        return OCRText(
            text = text,
            blocks = this.textBlocks.mapNotNull {
                it.mapToUi(
                    scaleFactorX = scaleFactorX,
                    scaleFactorY = scaleFactorY
                )
            }
        )
    }
}

fun Array<Point>.getUpdatedCorners(
    scaleFactorX: Float,
    scaleFactorY: Float
): Array<Point>? {
    with(this) {
        if (size == 4) {
            val topLeft = get(0)
            val topRight = get(1)
            val bottomRight = get(2)
            val bottomLeft = get(3)
            return arrayOf(
                Point((topLeft.x / scaleFactorX).toInt(), (topLeft.y / scaleFactorY).toInt()),
                Point((topRight.x / scaleFactorX).toInt(), (topRight.y / scaleFactorY).toInt()),
                Point(
                    (bottomRight.x / scaleFactorX).toInt(),
                    (bottomRight.y / scaleFactorY).toInt()
                ),
                Point((bottomLeft.x / scaleFactorX).toInt(), (bottomLeft.y / scaleFactorY).toInt())
            )
        }
    }
    return null
}

fun getUpdatedRectangle(
    rect: Rect,
    scaleFactorX: Float,
    scaleFactorY: Float
): Rect {
    return Rect(
        (rect.left / scaleFactorX).toInt(),
        (rect.top / scaleFactorY).toInt(),
        (rect.right / scaleFactorX).toInt(),
        (rect.bottom / scaleFactorY).toInt()
    )
}

internal fun Array<Point>.createOCRRectPath(rotationDegree: Float): Path {
    with(this) {
        val topLeftCoordinate = get(0)
        val topRightCoordinate = get(1)
        val bottomRightCoordinate = get(2)
        val bottomLeftCoordinate = get(3)
        val height = bottomLeftCoordinate.y - topLeftCoordinate.y

        return createPath(
            topLeftCoordinate = topLeftCoordinate,
            topRightCoordinate = topRightCoordinate,
            bottomRightCoordinate = bottomRightCoordinate,
            bottomLeftCoordinate = bottomLeftCoordinate,
            height = height
        )
    }
}

internal fun createPath(
    topLeftCoordinate: Point,
    topRightCoordinate: Point,
    bottomRightCoordinate: Point,
    bottomLeftCoordinate: Point,
    height: Int
): Path {
    val padding = height / 5
    val path = Path().apply {
        moveTo(
            topLeftCoordinate.x.toFloat() - padding,
            topLeftCoordinate.y.toFloat() - padding
        )
        lineTo(
            topRightCoordinate.x.toFloat() + padding,
            topRightCoordinate.y.toFloat() - padding
        )
        quadraticBezierTo(
            topRightCoordinate.x.toFloat() + padding + height,
            topRightCoordinate.y.toFloat() - padding + height / 2,
            bottomRightCoordinate.x.toFloat() + padding,
            bottomRightCoordinate.y.toFloat() + padding,
        )
        lineTo(
            bottomLeftCoordinate.x.toFloat() - padding,
            bottomLeftCoordinate.y.toFloat() + padding
        )
        quadraticBezierTo(
            bottomLeftCoordinate.x.toFloat() - padding - height,
            bottomLeftCoordinate.y.toFloat() + padding - height / 2,
            topLeftCoordinate.x.toFloat() - padding,
            topLeftCoordinate.y.toFloat() - padding
        )
        close()
    }
    return path
}

//internal fun createPath1(
//    topLeftCoordinate: Point,
//    topRightCoordinate: Point,
//    bottomRightCoordinate: Point,
//    bottomLeftCoordinate: Point,
//    rotationAngle: Float,
//    height: Int
//): Path {
////    val padding = height / 5
//    val arcDiameter = height
//
//    val path = Path().apply {
//        moveTo(
//            topLeftCoordinate.x.toFloat(),
//            topLeftCoordinate.y.toFloat()
//        )
//        lineTo(
//            x = topRightCoordinate.x.toFloat(),
//            y = topRightCoordinate.y.toFloat()
//        )
//        arcTo(
//            androidx.compose.ui.geometry.Rect(
//                topLeft = Offset(x = topRightCoordinate.x.toFloat() - arcDiameter / 2, y = topRightCoordinate.y.toFloat()),
//                bottomRight = Offset(x = topRightCoordinate.x.toFloat() + arcDiameter / 2, y = bottomRightCoordinate.y.toFloat())
//            ),
//            startAngleDegrees = -90f - rotationAngle,
//            sweepAngleDegrees = 180f - rotationAngle,
//            forceMoveTo = false
//        )
//        lineTo(
//            bottomLeftCoordinate.x.toFloat(),
//            bottomLeftCoordinate.y.toFloat()
//        )
//        arcTo(
//            androidx.compose.ui.geometry.Rect(
//                topLeft = Offset(x = topLeftCoordinate.x.toFloat() - arcDiameter / 2, y = topLeftCoordinate.y.toFloat()),
//                bottomRight = Offset(x = bottomLeftCoordinate.x.toFloat() + arcDiameter / 2, y = bottomLeftCoordinate.y.toFloat())
//            ),
//            startAngleDegrees = -90f + rotationAngle,
//            sweepAngleDegrees = -180f + rotationAngle,
//            forceMoveTo = false
//        )
//        close()
//    }
//    return path
//}

fun List<OCRBlock>.getSelectedText(): String {
    return this.filter { it.lines.firstOrNull { it.selected } != null }
        .joinToString("\n") { it.selectedText }
}

fun List<OCRBlock>.handleAll(isClear: Boolean): List<OCRBlock> {
    return this.map { block ->
        val updatedLines = block.lines.map { line ->
            line.copy(selected = !isClear)
        }
        block.copy(
            lines = updatedLines,
            selectedText = updatedLines.filter { it.selected }.joinToString("") { it.text }
        )
    }
}

fun List<OCRBlock>.updateBlocksByTap(
    tapOffset: Offset,
    onLineFound: (line: OCRLine) -> Unit
): List<OCRBlock> {
    var lineIndex = -1
    val block = this.firstOrNull { block ->
        lineIndex = block.lines.indexOfFirst {
            it.rect.contains(tapOffset.x.toInt(), tapOffset.y.toInt())
        }
        if (lineIndex != -1) {
            onLineFound(block.lines[lineIndex])
        }
        return@firstOrNull lineIndex != -1
    }
    if (lineIndex != -1 && block != null) {
        val isSelected = !block.lines[lineIndex].selected
        val updatedLine = block.lines[lineIndex].copy(selected = isSelected)
        val updatedLines = block.lines.toMutableList().apply {
            set(lineIndex, updatedLine)
        }
        val updatedBlock = block.copy(
            lines = updatedLines,
            selectedText = updatedLines.filter { it.selected }.joinToString("") { it.text }
        )
        return this.toMutableList().apply {
            set(this.indexOf(block), updatedBlock)
        }
    }
    return this
}

fun List<OCRBlock>.updateBlocksByDrag(
    currentOffsetChange: Offset,
    dragAmount: Offset,
    onLineAdded: (line: OCRLine) -> Unit
): List<OCRBlock> {
    val updated = this.map { block ->
        val lines = block.lines.map { line ->
            if (currentOffsetChange.y.toInt() >= line.rect.top && currentOffsetChange.y.toInt() < line.rect.bottom) {
                if (line.selected && dragAmount.y < 0 || !line.selected && dragAmount.y > 0) {
                    onLineAdded(line)
                    return@map line.copy(
                        selected = dragAmount.y > 0
                    )
                }
            }
            return@map line
        }
        return@map block.copy(
            lines = lines,
            selectedText = lines.filter { it.selected }.joinToString("") { it.text }
        )
    }
    return updated
}