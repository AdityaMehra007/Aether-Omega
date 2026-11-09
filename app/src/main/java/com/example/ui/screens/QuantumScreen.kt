package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.RingLweState
import com.example.model.SurfaceCodeState
import com.example.ui.theme.EmeraldMatrix
import com.example.ui.theme.FusionAmber
import com.example.ui.theme.LaserCrimson
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelDark
import com.example.ui.theme.PanelVariant
import com.example.ui.theme.PlasmaCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun QuantumScreen(
    surfaceCode: SurfaceCodeState,
    ringLwe: RingLweState,
    onInjectError: (Int, String) -> Unit,
    onDecodeSyndromes: () -> Unit,
    onTestRingLwe: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                text = "FAULT-TOLERANT QUANTUM SURFACE CODE & LATTICE PQC",
                color = PlasmaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // 2D Rotated Surface Code Lattice Canvas
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
                    val padding = 40.dp.toPx()
                    val gridWidth = size.width - 2 * padding
                    val gridHeight = size.height - 2 * padding
                    val stepX = gridWidth / 2f
                    val stepY = gridHeight / 2f

                    // 1. Draw Stabilizer Plaquettes (Checkered background)
                    // Plaquette Z0 (Top-right)
                    val pZ0_active = surfaceCode.zSyndromes.getOrElse(0) { 0 } == 1
                    drawRect(
                        color = if (pZ0_active) LaserCrimson.copy(alpha = 0.35f) else QuantumViolet.copy(alpha = 0.15f),
                        topLeft = Offset(padding + stepX, padding),
                        size = androidx.compose.ui.geometry.Size(stepX, stepY)
                    )

                    // Plaquette Z1 (Bottom-left)
                    val pZ1_active = surfaceCode.zSyndromes.getOrElse(1) { 0 } == 1
                    drawRect(
                        color = if (pZ1_active) LaserCrimson.copy(alpha = 0.35f) else QuantumViolet.copy(alpha = 0.15f),
                        topLeft = Offset(padding, padding + stepY),
                        size = androidx.compose.ui.geometry.Size(stepX, stepY)
                    )

                    // Plaquette X0 (Top-left)
                    val pX0_active = surfaceCode.xSyndromes.getOrElse(0) { 0 } == 1
                    drawRect(
                        color = if (pX0_active) LaserCrimson.copy(alpha = 0.35f) else PlasmaCyan.copy(alpha = 0.12f),
                        topLeft = Offset(padding, padding),
                        size = androidx.compose.ui.geometry.Size(stepX, stepY)
                    )

                    // Plaquette X1 (Bottom-right)
                    val pX1_active = surfaceCode.xSyndromes.getOrElse(1) { 0 } == 1
                    drawRect(
                        color = if (pX1_active) LaserCrimson.copy(alpha = 0.35f) else PlasmaCyan.copy(alpha = 0.12f),
                        topLeft = Offset(padding + stepX, padding + stepY),
                        size = androidx.compose.ui.geometry.Size(stepX, stepY)
                    )

                    // 2. Draw 9 Data Qubits on 3x3 intersections
                    for (row in 0..2) {
                        for (col in 0..2) {
                            val qIdx = row * 3 + col
                            val qX = padding + col * stepX
                            val qY = padding + row * stepY
                            val hasError = surfaceCode.injectedErrorQubits.containsKey(qIdx)
                            val errType = surfaceCode.injectedErrorQubits[qIdx]

                            drawCircle(
                                color = if (hasError) LaserCrimson else PlasmaCyan,
                                radius = 9.dp.toPx(),
                                center = Offset(qX, qY)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 9.dp.toPx(),
                                center = Offset(qX, qY),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }
                }

                // Overlay Status Chip
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (surfaceCode.logicalFidelity >= 0.99f) EmeraldMatrix.copy(alpha = 0.2f) else LaserCrimson.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LOGICAL FIDELITY: ${(surfaceCode.logicalFidelity * 100).toInt()}%",
                        color = if (surfaceCode.logicalFidelity >= 0.99f) EmeraldMatrix else LaserCrimson,
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
                        text = "Rotated Patch d=3 | 9 Data Qubits",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Noise Injection & MWPM Decoder Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PanelDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(PanelBorder, PanelVariant)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "QUANTUM ERROR INJECTION & MWPM DECODER",
                        color = QuantumViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = surfaceCode.decodedCorrection,
                        color = if (surfaceCode.logicalFidelity >= 0.99f) EmeraldMatrix else LaserCrimson,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onInjectError(4, "X") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(LaserCrimson, PanelBorder)))
                        ) {
                            Text(
                                text = "FLIP X (Q4)",
                                color = LaserCrimson,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { onInjectError(1, "Z") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FusionAmber, PanelBorder)))
                        ) {
                            Text(
                                text = "FLIP Z (Q1)",
                                color = FusionAmber,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDecodeSyndromes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_decode_surface_code"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QuantumViolet,
                            contentColor = SpaceBlack
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RUN MWPM SYNDROME DECODER & RESTORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Post-Quantum Ring-LWE Lattice Cryptography Playground
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
                            text = "RING-LWE LATTICE CRYPTOGRAPHY (ML-KEM)",
                            color = FusionAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldMatrix.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "EXACT MATCH",
                                color = EmeraldMatrix,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Polynomial Ring: R_q = ℤ₃₃₂₉[X] / (X⁶⁴ + 1)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Plaintext:  ${ringLwe.originalBitstring}",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "• Ciphertext: ${ringLwe.ciphertextSnippet}",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "• Decrypted:  ${ringLwe.decryptedBitstring} (Error margin: < 416/3329)",
                        color = EmeraldMatrix,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val newBits = List(16) { Random.nextInt(2) }.joinToString("")
                            onTestRingLwe(newBits)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_test_ring_lwe"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FusionAmber,
                            contentColor = SpaceBlack
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GENERATE NEW RING-LWE KEY & ENCRYPT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
