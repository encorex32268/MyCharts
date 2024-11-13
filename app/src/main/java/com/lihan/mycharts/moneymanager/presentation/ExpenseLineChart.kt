package com.lihan.mycharts.moneymanager.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.mycharts.moneymanager.domain.Expense
import com.lihan.mycharts.moneymanager.domain.monthlyExpenses
import com.lihan.mycharts.moneymanager.domain.toDateString
import com.lihan.mycharts.ui.theme.MyChartsTheme
import kotlin.math.roundToInt

@Composable
fun ExpenseLineChart(
    modifier: Modifier = Modifier,
    data: List<Expense>,
    isShowHelperLine: Boolean = false
) {

    val textStyle = LocalTextStyle.current.copy(
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    )
    val verticalPaddingPx = 4.dp.value
    val horizontalPaddingPx = 8.dp.value
    val xLabelTopPaddingPx = 4.dp.value
    val xLabelBottomPaddingPx = 4.dp.value

    val textMeasurer = rememberTextMeasurer()

    val xLabels = data.map {
        textMeasurer.measure(
            text = it.timestamp.toDateString(),
            style = textStyle
        )
    }
    val xLabelMaxHeight = xLabels.maxOfOrNull { it.size.height }?:0
    val xLabelMaxWidth = xLabels.maxOfOrNull { it.size.width }?:0
    val xLabelSpacing =  16.dp.value
    val maxXLabelLineCount = xLabels.maxOfOrNull { it.lineCount } ?: 0
    val xLabelLineHeight = xLabelMaxHeight / maxXLabelLineCount



    val xTopLabels = data.map {
        textMeasurer.measure(
            text = it.cost,
            style = textStyle.copy(
                fontSize = 9.sp
            )
        )
    }




    Canvas(modifier = modifier) {

        val viewPortY = verticalPaddingPx + xLabelMaxHeight * 2 + xLabelTopPaddingPx
        val viewPortBottom = size.height - viewPortY
        val viewPortHeight = size.height - (xLabelMaxHeight + 2*verticalPaddingPx + xLabelLineHeight)

        val yMaxValue =  data.maxOfOrNull { it.cost.toInt() } ?: 0
        val yLabelData = data.run {
            val dataList = mutableListOf<Int>()
            dataList.add(0, yMaxValue)
            dataList.add((yMaxValue * 0.75).roundToInt())
            dataList.add((yMaxValue * 0.5).roundToInt())
            dataList.add((yMaxValue * 0.25).roundToInt())
            dataList.add(0)
            dataList.toList()
        }


        val yLabels = yLabelData.map {
            textMeasurer.measure(
                text = it.toString(),
                style = textStyle
            )
        }
        val yLabelMaxWidth =  yLabels.maxOfOrNull { it.size.width }?:0
        val viewPortLeft = horizontalPaddingPx + yLabelMaxWidth
        val spaceBetweenLabels = viewPortHeight / yLabelData.size - (xLabelLineHeight)

        val viewPortRect = Rect(
            top = viewPortY,
            bottom = viewPortBottom,
            left = viewPortLeft,
            right = size.width
        )

        if (isShowHelperLine){
            drawLine(
                color = Color.Red,
                start = Offset(
                    x = viewPortLeft,
                    y = viewPortY
                ),
                end = Offset(
                    x = viewPortLeft,
                    y = viewPortHeight
                ),
                strokeWidth = 10f
            )
            drawRect(
                color = Color.LightGray.copy(alpha = 0.3f),
                topLeft = viewPortRect.topLeft,
                size = viewPortRect.size
            )
        }

        val points = mutableListOf<Offset>()
        xLabels.forEachIndexed { index, result ->
            val x = viewPortLeft + xLabelMaxWidth * index * 1.5f + xLabelSpacing*2
            val y = viewPortBottom + xLabelBottomPaddingPx
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x ,
                    y = y
                )
            )

            drawText(
                textLayoutResult = xTopLabels[index],
                topLeft = Offset(
                    x = x ,
                    y = viewPortY - verticalPaddingPx - xLabelLineHeight
                )
            )


            drawLine(
                color = Color.LightGray,
                strokeWidth = 2f,
                start = Offset(x = x+xLabelMaxWidth/2 , y = viewPortY),
                end = Offset( x = x+xLabelMaxWidth/2 , y = viewPortBottom)
            )
            val expense = data[index]
            val averge =  (viewPortBottom - viewPortY) / yMaxValue
            val circleY = viewPortBottom - averge * expense.cost.toInt()
            drawCircle(
                color = Color.Red,
                radius = 10f,
                center = Offset(
                    x = x + xLabelMaxWidth / 2,
                    y = circleY
                )
            )
            points.add(Offset(
                x = x + xLabelMaxWidth / 2 ,
                y = circleY
            ))
            


        }

        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Red,
            style = Stroke(width = 4f)
        )


        if (isShowHelperLine){
            drawCircle(
                color = Color.Gray,
                radius = 10f,
                center = Offset(
                    x = viewPortLeft,
                    y = viewPortBottom
                )
            )

            drawCircle(
                color = Color.Gray,
                radius = 10f,
                center = Offset(
                    x = viewPortLeft,
                    y = viewPortY
                )
            )
        }


        yLabels.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + yLabelMaxWidth - result.size.width
            val y = viewPortY +
                    index * (xLabelLineHeight + spaceBetweenLabels) -
                    xLabelLineHeight / 2f
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                )
            )
            drawLine(
                color = Color.LightGray,
                start = Offset(x = viewPortLeft , y = y +  xLabelLineHeight / 2f),
                end = Offset(x =  size.width , y = y + xLabelLineHeight / 2f),
                strokeWidth = 2f
            )

        }









    }

}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun ExpenseLineChartPreview() {
    MyChartsTheme {
        ExpenseLineChart(
            modifier = Modifier
                .width(700.dp)
                .height(300.dp)
                .background(Color.White)
            ,
            data = monthlyExpenses
        )
    }

}