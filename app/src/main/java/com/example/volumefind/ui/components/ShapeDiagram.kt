package com.example.volumefind.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volumefind.domain.model.VolumeShape

@Composable
fun ShapeDiagram(
    shape: VolumeShape,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .size(width = 160.dp, height = 120.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (shape) {
                VolumeShape.CUBE -> drawCube(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.RECTANGULAR_PRISM -> drawRectangularPrism(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.CYLINDER -> drawCylinder(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.SPHERE -> drawSphere(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.CONE -> drawCone(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.SQUARE_PYRAMID -> drawSquarePyramid(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.TRIANGULAR_PRISM -> drawTriangularPrism(w, h, primaryColor, surfaceColor, outlineColor)
                VolumeShape.HEMISPHERE -> drawHemisphere(w, h, primaryColor, surfaceColor, outlineColor)
            }
        }
    }
}

private fun DrawScope.drawCube(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val side = h * 0.55f
    val offset = side * 0.45f
    val x0 = (w - side - offset) / 2f
    val y0 = (h - side - offset) / 2f + offset

    // Front square
    val frontPath = Path().apply {
        moveTo(x0, y0)
        lineTo(x0 + side, y0)
        lineTo(x0 + side, y0 + side)
        lineTo(x0, y0 + side)
        close()
    }
    drawPath(frontPath, fillColor)
    drawPath(frontPath, strokeColor, style = stroke)

    // Top face
    val topPath = Path().apply {
        moveTo(x0, y0)
        lineTo(x0 + offset, y0 - offset)
        lineTo(x0 + side + offset, y0 - offset)
        lineTo(x0 + side, y0)
        close()
    }
    drawPath(topPath, strokeColor, style = stroke)

    // Right face
    val rightPath = Path().apply {
        moveTo(x0 + side, y0)
        lineTo(x0 + side + offset, y0 - offset)
        lineTo(x0 + side + offset, y0 + side - offset)
        lineTo(x0 + side, y0 + side)
        close()
    }
    drawPath(rightPath, strokeColor, style = stroke)

    // Dimension line for side
    drawLine(
        color = dimColor,
        start = Offset(x0, y0 + side + 12f),
        end = Offset(x0 + side, y0 + side + 12f),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawRectangularPrism(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val length = w * 0.55f
    val height = h * 0.5f
    val depth = h * 0.3f
    val x0 = (w - length - depth) / 2f
    val y0 = (h - height - depth) / 2f + depth

    val frontPath = Path().apply {
        moveTo(x0, y0)
        lineTo(x0 + length, y0)
        lineTo(x0 + length, y0 + height)
        lineTo(x0, y0 + height)
        close()
    }
    drawPath(frontPath, fillColor)
    drawPath(frontPath, strokeColor, style = stroke)

    val topPath = Path().apply {
        moveTo(x0, y0)
        lineTo(x0 + depth, y0 - depth)
        lineTo(x0 + length + depth, y0 - depth)
        lineTo(x0 + length, y0)
        close()
    }
    drawPath(topPath, strokeColor, style = stroke)

    val rightPath = Path().apply {
        moveTo(x0 + length, y0)
        lineTo(x0 + length + depth, y0 - depth)
        lineTo(x0 + length + depth, y0 + height - depth)
        lineTo(x0 + length, y0 + height)
        close()
    }
    drawPath(rightPath, strokeColor, style = stroke)
}

private fun DrawScope.drawCylinder(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val rx = w * 0.28f
    val ry = h * 0.12f
    val cylHeight = h * 0.55f
    val cx = w / 2f
    val topY = (h - cylHeight) / 2f + ry

    // Bottom arc
    drawArc(
        color = strokeColor,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - rx, topY + cylHeight - ry),
        size = Size(rx * 2, ry * 2),
        style = stroke
    )

    // Sides
    drawLine(
        color = strokeColor,
        start = Offset(cx - rx, topY),
        end = Offset(cx - rx, topY + cylHeight),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = strokeColor,
        start = Offset(cx + rx, topY),
        end = Offset(cx + rx, topY + cylHeight),
        strokeWidth = strokeWidth
    )

    // Top ellipse
    drawOval(
        color = fillColor,
        topLeft = Offset(cx - rx, topY - ry),
        size = Size(rx * 2, ry * 2)
    )
    drawOval(
        color = strokeColor,
        topLeft = Offset(cx - rx, topY - ry),
        size = Size(rx * 2, ry * 2),
        style = stroke
    )

    // Radius line
    drawLine(
        color = dimColor,
        start = Offset(cx, topY),
        end = Offset(cx + rx, topY),
        strokeWidth = 2f
    )
    drawCircle(color = dimColor, radius = 3f, center = Offset(cx, topY))
}

private fun DrawScope.drawSphere(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val r = h * 0.42f
    val cx = w / 2f
    val cy = h / 2f

    drawCircle(
        color = fillColor,
        radius = r,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = strokeColor,
        radius = r,
        center = Offset(cx, cy),
        style = stroke
    )

    // Dashed equator
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    drawOval(
        color = dimColor.copy(alpha = 0.8f),
        topLeft = Offset(cx - r, cy - r * 0.3f),
        size = Size(r * 2, r * 0.6f),
        style = Stroke(width = 2f, pathEffect = dashEffect)
    )

    // Radius line
    drawLine(
        color = strokeColor,
        start = Offset(cx, cy),
        end = Offset(cx + r * 0.85f, cy - r * 0.5f),
        strokeWidth = 2.5f
    )
    drawCircle(color = strokeColor, radius = 3.5f, center = Offset(cx, cy))
}

private fun DrawScope.drawCone(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val rx = w * 0.32f
    val ry = h * 0.12f
    val apex = Offset(w / 2f, h * 0.15f)
    val baseY = h * 0.82f
    val cx = w / 2f

    // Base arc
    drawArc(
        color = strokeColor,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - rx, baseY - ry),
        size = Size(rx * 2, ry * 2),
        style = stroke
    )

    // Cone sides
    drawLine(
        color = strokeColor,
        start = apex,
        end = Offset(cx - rx, baseY),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = strokeColor,
        start = apex,
        end = Offset(cx + rx, baseY),
        strokeWidth = strokeWidth
    )

    // Height centerline
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    drawLine(
        color = dimColor,
        start = apex,
        end = Offset(cx, baseY),
        strokeWidth = 2f,
        pathEffect = dashEffect
    )

    // Radius line
    drawLine(
        color = strokeColor,
        start = Offset(cx, baseY),
        end = Offset(cx + rx, baseY),
        strokeWidth = 2.5f
    )
    drawCircle(color = strokeColor, radius = 3f, center = Offset(cx, baseY))
}

private fun DrawScope.drawSquarePyramid(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val apex = Offset(w / 2f, h * 0.15f)
    val pFrontLeft = Offset(w * 0.22f, h * 0.82f)
    val pFrontRight = Offset(w * 0.68f, h * 0.85f)
    val pBackRight = Offset(w * 0.82f, h * 0.68f)

    val frontFace = Path().apply {
        moveTo(apex.x, apex.y)
        lineTo(pFrontLeft.x, pFrontLeft.y)
        lineTo(pFrontRight.x, pFrontRight.y)
        close()
    }
    drawPath(frontFace, fillColor)
    drawPath(frontFace, strokeColor, style = stroke)

    val sideFace = Path().apply {
        moveTo(apex.x, apex.y)
        lineTo(pFrontRight.x, pFrontRight.y)
        lineTo(pBackRight.x, pBackRight.y)
        close()
    }
    drawPath(sideFace, strokeColor, style = stroke)

    // Height centerline
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    drawLine(
        color = dimColor,
        start = apex,
        end = Offset(w * 0.52f, h * 0.76f),
        strokeWidth = 2f,
        pathEffect = dashEffect
    )
}

private fun DrawScope.drawTriangularPrism(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val p1 = Offset(w * 0.18f, h * 0.78f)
    val p2 = Offset(w * 0.50f, h * 0.78f)
    val p3 = Offset(w * 0.34f, h * 0.34f)
    val dx = w * 0.38f
    val dy = -h * 0.22f

    val frontTriangle = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(frontTriangle, fillColor)
    drawPath(frontTriangle, strokeColor, style = stroke)

    // Long edges
    drawLine(color = strokeColor, start = p1, end = Offset(p1.x + dx, p1.y + dy), strokeWidth = strokeWidth)
    drawLine(color = strokeColor, start = p2, end = Offset(p2.x + dx, p2.y + dy), strokeWidth = strokeWidth)
    drawLine(color = strokeColor, start = p3, end = Offset(p3.x + dx, p3.y + dy), strokeWidth = strokeWidth)

    // Back edges
    drawLine(
        color = strokeColor,
        start = Offset(p2.x + dx, p2.y + dy),
        end = Offset(p3.x + dx, p3.y + dy),
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawHemisphere(
    w: Float,
    h: Float,
    strokeColor: Color,
    fillColor: Color,
    dimColor: Color
) {
    val strokeWidth = 3f
    val stroke = Stroke(width = strokeWidth)
    val r = h * 0.42f
    val cx = w / 2f
    val cy = h * 0.65f

    // Dome arc (upper half)
    drawArc(
        color = strokeColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - r, cy - r),
        size = Size(r * 2, r * 2),
        style = stroke
    )

    // Flat base ellipse
    drawOval(
        color = fillColor,
        topLeft = Offset(cx - r, cy - r * 0.28f),
        size = Size(r * 2, r * 0.56f)
    )
    drawOval(
        color = strokeColor,
        topLeft = Offset(cx - r, cy - r * 0.28f),
        size = Size(r * 2, r * 0.56f),
        style = stroke
    )

    // Radius line
    drawLine(
        color = dimColor,
        start = Offset(cx, cy),
        end = Offset(cx + r, cy),
        strokeWidth = 2.5f
    )
    drawCircle(color = dimColor, radius = 3.5f, center = Offset(cx, cy))
}
