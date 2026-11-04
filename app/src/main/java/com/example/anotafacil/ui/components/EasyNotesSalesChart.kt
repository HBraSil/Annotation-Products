package com.example.anotafacil.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerController
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

private val MONTHS = listOf(
    "Fev/26",
    "Mar/26",
    "Abr",
    "Mai",
    "Jun",
    "Jul",
    "Ago/26",
)


@Composable
fun SalesChart(
    sales: List<Double>,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    valueFormatter: (Double) -> String = { brazilianCurrencyFormatter.format(it) },
    axisFormatter: (Double) -> String = { "R$ ${it.toInt()}" },
) {
    val markerFormatter = remember {
        DefaultCartesianMarker.ValueFormatter { _, targets ->
            val value =
                (targets.first() as LineCartesianLayerMarkerTarget)
                    .points.first()
                    .entry.y

            valueFormatter(value)
        }
    }

    var selectedPoint by remember { mutableStateOf<Double?>(null) }

    val modelProducer = remember { CartesianChartModelProducer() }
    val indicatorComponent = rememberShapeComponent(
        fill = Fill(MaterialTheme.colorScheme.onBackground),
        shape = CircleShape,
    )

    val marker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            margins = Insets(
                8.dp
            ),
            padding = Insets(
                horizontal = 4.dp,
                vertical = 4.dp,
            ),
            background = indicatorComponent,
        ),
        indicator = {
            indicatorComponent
        },
        indicatorSize = 8.dp,
        valueFormatter = markerFormatter,
        labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,
    )

    val markerVisibilityListener = remember {
        object : CartesianMarkerVisibilityListener {

            override fun onShown(
                marker: CartesianMarker,
                targets: List<CartesianMarker.Target>,
            ) {
                val target =
                    targets.first() as? LineCartesianLayerMarkerTarget
                        ?: return

                selectedPoint = target.x
            }

            override fun onHidden(marker: CartesianMarker) {}
        }
    }

    val maxValue = remember(sales) { (sales.maxOrNull() ?: 0.0) * 1.2 }

    LaunchedEffect(sales) {
        modelProducer.runTransaction {
            lineModel {
                series(
                    x = sales.indices.map { it.toDouble() },
                    y = sales,
                )
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(
                            Fill(MaterialTheme.colorScheme.onSurface)
                        ),
                        stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 2.dp),
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minY = 0.0,
                    maxY = if (maxValue <= 0.0) 1.0 else maxValue
                )
            ),

            startAxis = VerticalAxis.rememberStart(
                label = rememberTextComponent(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    axisFormatter(value)
                },
                itemPlacer = VerticalAxis.ItemPlacer.step(
                    step = { if (maxValue <= 0.0) 1.0 else (maxValue / 5.0) },
                    shiftTopLines = false
                ),
            ),

            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                ),
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    labels.getOrNull(value.toInt()) ?: ""
                },
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(
                    spacing = { 1 }
                ),
            ),
            marker = marker,
            markerVisibilityListener = markerVisibilityListener,
            markerController = CartesianMarkerController.rememberToggleOnTap()
        ),
        modelProducer = modelProducer,
        modifier = modifier.height(300.dp),
    )
}


@Composable
@Preview
fun SalesChartPreview() {
    SalesChart(
        sales = listOf(
            850.0,
            1100.0,
            950.0,
            1400.0,
            1750.0,
            1500.0,
            1900.0,
        ),
        labels = listOf(
            "Fev/26",
            "Mar/26",
            "Abr",
            "Mai",
            "Jun",
            "Jul",
            "Ago/26",
        )
    )
}
