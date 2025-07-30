package dev.redcom1988.hermes.ui.components.color_picker.pickers

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.redcom1988.hermes.ui.components.color_picker.data.ColorRange
import dev.redcom1988.hermes.ui.components.color_picker.data.Colors.gradientColors
import dev.redcom1988.hermes.ui.components.color_picker.util.ColorPickerHelper
import dev.redcom1988.hermes.ui.components.color_picker.util.blue
import dev.redcom1988.hermes.ui.components.color_picker.util.darken
import dev.redcom1988.hermes.ui.components.color_picker.util.drawColorSelector
import dev.redcom1988.hermes.ui.components.color_picker.util.green
import dev.redcom1988.hermes.ui.components.color_picker.util.lighten
import dev.redcom1988.hermes.ui.components.color_picker.util.red
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@Composable
internal fun ClassicColorPicker(
    modifier: Modifier = Modifier,
    showAlphaBar: Boolean,
    onPickedColor: (Color) -> Unit
) {
    var pickerLocation by remember {
        mutableStateOf(Offset.Zero)
    }
    var colorPickerSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    var alpha by remember {
        mutableFloatStateOf(1f)
    }
    var rangeColor by remember {
        mutableStateOf(Color.White)
    }
    var color by remember {
        mutableStateOf(Color.White)
    }
    LaunchedEffect(rangeColor, pickerLocation, colorPickerSize, alpha) {
        val xProgress = 1 - (pickerLocation.x / colorPickerSize.width)
        val yProgress = pickerLocation.y / colorPickerSize.height
        color = Color(
            rangeColor
                .red()
                .lighten(xProgress)
                .darken(yProgress),
            rangeColor
                .green()
                .lighten(xProgress)
                .darken(yProgress),
            rangeColor
                .blue()
                .lighten(xProgress)
                .darken(yProgress),
            alpha = (255 * alpha).roundToInt()
        )
    }
    LaunchedEffect(color) {
        onPickedColor(color)
    }
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
        Box(
            modifier = modifier
                .onSizeChanged {
                    colorPickerSize = it
                }
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            val x = it.x.coerceIn(0f, colorPickerSize.width.toFloat())
                            val y = it.y.coerceIn(0f, colorPickerSize.height.toFloat())
                            pickerLocation = Offset(x, y)
                        }
                    }
                    return@pointerInteropFilter true
                }
                .size(200.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                drawRect(Brush.horizontalGradient(listOf(Color.White, rangeColor)))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                this.drawColorSelector(color.copy(alpha = 1f), pickerLocation)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ColorSlideBar(colors = gradientColors) {
            val (rangeProgress, range) = ColorPickerHelper.calculateRangeProgress(it.toDouble())
            val red: Int
            val green: Int
            val blue: Int
            when (range) {
                ColorRange.RedToYellow -> {
                    red = 255
                    green = (255 * rangeProgress).roundToInt()
                    blue = 0
                }
                ColorRange.YellowToGreen -> {
                    red = (255 * (1 - rangeProgress)).roundToInt()
                    green = 255
                    blue = 0
                }
                ColorRange.GreenToCyan -> {
                    red = 0
                    green = 255
                    blue = (255 * rangeProgress).roundToInt()
                }
                ColorRange.CyanToBlue -> {
                    red = 0
                    green = (255 * (1 - rangeProgress)).roundToInt()
                    blue = 255
                }
                ColorRange.BlueToPurple -> {
                    red = (255 * rangeProgress).roundToInt()
                    green = 0
                    blue = 255
                }
                ColorRange.PurpleToRed -> {
                    red = 255
                    green = 0
                    blue = (255 * (1 - rangeProgress)).roundToInt()
                }
            }
            rangeColor = Color(red, green, blue)
        }
        if (showAlphaBar) {
            Spacer(modifier = Modifier.height(16.dp))
            ColorSlideBar(colors = listOf(Color.Transparent, rangeColor)) {
                alpha = it
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
private fun ClassicColorPickerPreview() {
    ClassicColorPicker(
        modifier = Modifier,
        showAlphaBar = true,
        onPickedColor = {}
    )
}