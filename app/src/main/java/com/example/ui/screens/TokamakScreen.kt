package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TokamakState
import com.example.ui.theme.EmeraldMatrix
import com.example.ui.theme.FusionAmber
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelDark
import com.example.ui.theme.PanelVariant
import com.example.ui.theme.PlasmaCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.ScramRed
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.StellarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TokamakScreen(
    tokamak: TokamakState,
    onPowerChanged: (Double) -> Unit,
    onScram: () -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plasma")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "TOKAMAK MAGNETIC CONFINEMENT & MHD CORE",
                color = PlasmaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // Interactive Animated Tokamak Torus Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PanelDark)
                    .border(1.dp, if (tokamak.isScrammed) ScramRed else PanelBorder, RoundedCornerShape(16.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val rOuter = size.minDimension * 0.42f
                    val rInner = size.minDimension * 0.22f
                    val rCore = size.minDimension * 0.12f

                    // 1. Draw outer cryostat and vacuum vessel boundaries
                    drawCircle(
                        color = PanelBorder,
                        radius = rOuter,
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = PanelBorder.copy(alpha = 0.5f),
                        radius = rInner,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 2. Draw 18 Superconducting Coil Stations around the perimeter
                    val coilCount = 18
                    for (i in 0 until coilCount) {
                        val angleRad = Math.toRadians((i * (360.0 / coilCount)).toDouble())
                        val cx = center.x + (rOuter + 8.dp.toPx()) * cos(angleRad).toFloat()
                        val cy = center.y + (rOuter + 8.dp.toPx()) * sin(angleRad).toFloat()
                        val currentKa = tokamak.coilCurrentsKa.getOrElse(i) { 12.0 }
                        val coilColor = if (tokamak.isScrammed) TextMuted else PlasmaCyan.copy(alpha = (currentKa / 16.0).toFloat().coerceIn(0.3f, 1f))
                        drawCircle(
                            color = coilColor,
                            radius = 4.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }

                    if (!tokamak.isScrammed) {
                        // 3. Rotating magnetic flux streamlines
                        val rotRad = Math.toRadians(rotationAngle.toDouble())
                        for (j in 0 until 6) {
                            val orbitAngle = rotRad + j * (Math.PI / 3.0)
                            val fluxRadius = (rInner + rOuter) / 2f
                            val fx = center.x + fluxRadius * cos(orbitAngle).toFloat()
                            val fy = center.y + fluxRadius * sin(orbitAngle).toFloat()
                            drawCircle(
                                color = StellarGold.copy(alpha = 0.7f),
                                radius = 3.dp.toPx(),
                                center = Offset(fx, fy)
                            )
                        }

                        // 4. Glowing Hot Plasma Core
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    StellarGold,
                                    FusionAmber,
                                    PlasmaCyan.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = rCore * corePulse
                            ),
                            radius = rCore * corePulse,
                            center = center
                        )
                    } else {
                        // SCRAM indicator
                        drawCircle(
                            color = ScramRed.copy(alpha = 0.3f),
                            radius = rInner,
                            center = center
                        )
                    }
                }

                // Overlay status chip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (tokamak.isScrammed) ScramRed else EmeraldMatrix.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (tokamak.isScrammed) "SCRAMMED (ZERO CURRENT)" else "IGNITED (NET POSITIVE Q=14.2)",
                        color = if (tokamak.isScrammed) Color.White else EmeraldMatrix,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "T_i: ${tokamak.ionTempKev} keV | n_e: 1.5e20 m⁻³",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Lawson Criterion Triple Product
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LAWSON CRITERION (TRIPLE PRODUCT)",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (tokamak.isIgnited) EmeraldMatrix.copy(alpha = 0.2f) else LaserCrimson.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (tokamak.isIgnited) "SELF-SUSTAINED" else "SUB-CRITICAL",
                                color = if (tokamak.isIgnited) EmeraldMatrix else LaserCrimson,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "n_e · T_i · τ_E = ${(tokamak.tripleProduct / 1.0e21).let { "%.2f".format(it) }} × 10²¹ m⁻³·keV·s",
                        color = FusionAmber,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lawson D-T threshold: ≥ 3.00 × 10²¹ m⁻³·keV·s (Ignition achieved)",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Tearing Mode & Gyrotron ECCD Control
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ECCD GYROTRON RF POWER",
                            color = PlasmaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${tokamak.eccdGyrotronMw.toInt()} MW",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = tokamak.eccdGyrotronMw.toFloat(),
                        onValueChange = { onPowerChanged(it.toDouble()) },
                        valueRange = 0f..50f,
                        enabled = !tokamak.isScrammed,
                        colors = SliderDefaults.colors(
                            thumbColor = PlasmaCyan,
                            activeTrackColor = PlasmaCyan,
                            inactiveTrackColor = PanelBorder
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tearing Mode (m=2, n=1): ${(tokamak.tearingModeAmplitude * 1000.0).toInt()} mm",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Disruption Limit: 120 mm",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 18 Toroidal Coils Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "18-COIL SPATIAL HARMONIC FLUX CURRENTS (kA)",
                        color = QuantumViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        tokamak.coilCurrentsKa.take(9).forEachIndexed { i, ka ->
                            CoilIndicator(index = i + 1, currentKa = ka)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        tokamak.coilCurrentsKa.drop(9).forEachIndexed { i, ka ->
                            CoilIndicator(index = i + 10, currentKa = ka)
                        }
                    }
                }
            }
        }

        // SCRAM Interlock Action
        item {
            if (!tokamak.isScrammed) {
                Button(
                    onClick = onScram,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_emergency_scram"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ScramRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMERGENCY SCRAM: MASSIVE GAS INJECTION DUMP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Button(
                    onClick = onRestore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_restore_tokamak"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldMatrix,
                        contentColor = SpaceBlack
                    )
                ) {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RE-ENERGIZE SUPERCONDUCTING MAGNETS & IGNITE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CoilIndicator(index: Int, currentKa: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PlasmaCyan.copy(alpha = (currentKa / 16.0).toFloat().coerceIn(0.2f, 1f)))
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "$index",
            color = TextMuted,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
