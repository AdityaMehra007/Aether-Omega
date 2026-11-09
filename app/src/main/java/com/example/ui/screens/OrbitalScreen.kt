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
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrbitalState
import com.example.ui.theme.EmeraldMatrix
import com.example.ui.theme.FusionAmber
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelDark
import com.example.ui.theme.PanelVariant
import com.example.ui.theme.PlasmaCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.StellarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

val GROUND_STATIONS = listOf(
    "CERN Primary Geo-Hub",
    "ITER Cadarache Grid",
    "Mojave Spaceport Optical Hub",
    "Tokyo Bay Super-Grid",
    "Atacama Array Relay"
)

@Composable
fun OrbitalScreen(
    orbital: OrbitalState,
    onGroundStationChanged: (String) -> Unit,
    onToggleLaser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbital")
    val orbitAngleDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitAngle"
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
                text = "ORBITAL SWARM & RELATIVISTIC BEAMING",
                color = PlasmaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // Interactive Celestial Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PanelDark)
                    .border(1.dp, PanelBorder, RoundedCornerShape(16.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val rEarth = size.minDimension * 0.22f
                    val rOrbit = size.minDimension * 0.40f

                    // 1. Draw Earth
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), SpaceBlack),
                            center = center,
                            radius = rEarth
                        ),
                        radius = rEarth,
                        center = center
                    )
                    drawCircle(
                        color = PlasmaCyan.copy(alpha = 0.4f),
                        radius = rEarth,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // 2. Draw Orbit Path (Dashed)
                    drawCircle(
                        color = QuantumViolet.copy(alpha = 0.4f),
                        radius = rOrbit,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    // 3. Ground Station Beacon on Earth surface
                    val stationAngle = Math.toRadians(45.0)
                    val gX = center.x + rEarth * cos(stationAngle).toFloat()
                    val gY = center.y + rEarth * sin(stationAngle).toFloat()
                    drawCircle(
                        color = EmeraldMatrix,
                        radius = 4.dp.toPx(),
                        center = Offset(gX, gY)
                    )

                    // 4. Satellite Position along orbit
                    val satRad = Math.toRadians(orbitAngleDeg.toDouble())
                    val sX = center.x + rOrbit * cos(satRad).toFloat()
                    val sY = center.y + rOrbit * sin(satRad).toFloat()

                    // Laser Beaming Line from Satellite to Ground Station
                    if (orbital.isLaserFiring) {
                        drawLine(
                            color = PlasmaCyan.copy(alpha = 0.85f),
                            start = Offset(sX, sY),
                            end = Offset(gX, gY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Satellite Beacon
                    drawCircle(
                        color = StellarGold,
                        radius = 6.dp.toPx(),
                        center = Offset(sX, sY)
                    )
                }

                // Overlay Status Chip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (orbital.isLaserFiring) PlasmaCyan.copy(alpha = 0.2f) else PanelVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (orbital.isLaserFiring) "LASER BEAMING ACTIVE (1.2 GW)" else "OPTICAL LINK STANDBY",
                        color = if (orbital.isLaserFiring) PlasmaCyan else TextMuted,
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
                        text = "Alt: ${orbital.altitudeKm.toInt()} km | Inc: ${orbital.inclinationDeg}°",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Ephemeris & Relativity Telemetry Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "VELOCITY",
                    value = "${orbital.orbitalVelocityKmS} km/s",
                    accent = PlasmaCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "J2 PRECES.",
                    value = "${orbital.j2PrecessionDegDay}°/d",
                    accent = QuantumViolet,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "BRADLEY LEAD",
                    value = "${orbital.pointAheadLeadUrad} µrad",
                    accent = FusionAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Relativistic Bradley Aberration Deep Dive
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GENERAL & SPECIAL RELATIVITY COMPENSATION",
                        color = FusionAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Bradley Point-Ahead Lead: 2v/c = ${orbital.pointAheadLeadUrad} µrad (~56m offset on ground)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• Net Atomic Clock Drift: ${orbital.relativisticDriftPpb} ppb (GR Blueshift vs SR Dilation)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• Earth Oblateness (J2 = 1.0826e-3): Rotates orbital plane -3.34° every 24 hours",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Target Ground Station Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TARGET TERRESTRIAL RECEIVER HUB",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GROUND_STATIONS.forEach { station ->
                        val isSelected = orbital.targetGroundStation == station
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PlasmaCyan.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { onGroundStationChanged(station) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = if (isSelected) PlasmaCyan else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = station,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Beaming Switch Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PHASED-ARRAY POWER LASER",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (orbital.isLaserFiring) "Beaming 1.20 GW optical flux" else "Laser shutter closed",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Switch(
                        checked = orbital.isLaserFiring,
                        onCheckedChange = { onToggleLaser() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PlasmaCyan,
                            checkedTrackColor = PlasmaCyan.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = PanelBorder
                        ),
                        modifier = Modifier.testTag("switch_orbital_laser")
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
