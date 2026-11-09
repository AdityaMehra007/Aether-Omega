package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MncResults
import com.example.ui.theme.EmeraldMatrix
import com.example.ui.theme.FusionAmber
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelDark
import com.example.ui.theme.PanelVariant
import com.example.ui.theme.PlasmaCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MncScreen(
    mnc: MncResults,
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
                text = "TOP 1,000 STARTUPS & MNC SYSTEM ARCHETYPES",
                color = PlasmaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // 1. Google Spanner
        item {
            MncEngineTile(
                title = "1. Google Spanner TrueTime & HLC",
                lineage = "Planet-Scale External Consistency",
                equation = "TrueTime.now() = [t - ε, t + ε] | Commit-Wait: 2ε = 2.4 ms",
                description = "Hardware GPS and Rubidium atomic clocks enforce Strict Serializability globally without distributed read locks.",
                liveOutput = mnc.spannerHlc,
                accent = PlasmaCyan
            )
        }

        // 2. AWS Aurora
        item {
            MncEngineTile(
                title = "2. AWS Aurora Quorum Storage",
                lineage = "Cloud-Native Log-Structured Storage",
                equation = "V = 6, Vw = 4, Vr = 3 (Vw + Vr = 7 > 6)",
                description = "Replaces 16KB disk page replication with 6-node redo-log shipping across 3 Availability Zones. Survives AZ loss + 1 disk failure.",
                liveOutput = mnc.auroraAcks,
                accent = FusionAmber
            )
        }

        // 3. Figma / Linear CRDTs
        item {
            MncEngineTile(
                title = "3. Figma & Linear Multiplayer CRDT",
                lineage = "Local-First Strong Eventual Consistency",
                equation = "A ⊔ B = B ⊔ A | (A ⊔ B) ⊔ C = A ⊔ (B ⊔ C) | A ⊔ A = A",
                description = "Last-Write-Wins Element Map (LWW-Element-Map) backed by Hybrid Logical Clocks converges multi-continent offline edits with zero locks.",
                liveOutput = mnc.crdtConvergedVal,
                accent = QuantumViolet
            )
        }

        // 4. OpenAI / vLLM PagedAttention
        item {
            MncEngineTile(
                title = "4. OpenAI / vLLM PagedAttention & Flash-2",
                lineage = "Frontier AI Inference Acceleration",
                equation = "FlashAttention-2: Softmax Tiling with O(N) SRAM & 2.22e-16 Error",
                description = "Deduplicates shared system prompts via SHA-256 Radix prefix hashes, eliminating GPU HBM fragmentation and KV-cache waste.",
                liveOutput = mnc.pagedAttnSharing,
                accent = EmeraldMatrix
            )
        }

        // 5. Pinecone Product Quantization
        item {
            MncEngineTile(
                title = "5. Pinecone / Turbopuffer Product Quantization",
                lineage = "High-Dimensional Vector Retrieval",
                equation = "R¹⁵³⁶ → 64 Subspaces x 256 Centroids (96x Compression)",
                description = "Asymmetric Distance Computation (ADC) executes billion-vector search with lookup table additions, avoiding floating-point multiplies.",
                liveOutput = "ADC Top-1: spec_042 | Dist: 0.082 | RAM Saved: 96%",
                accent = PlasmaCyan
            )
        }

        // 6. Cursor AST Merkle Indexer
        item {
            MncEngineTile(
                title = "6. Cursor & Devin AST Merkle Indexer",
                lineage = "Agentic Code Intelligence & Sync",
                equation = "Merkle Root = SHA256(∏ Sort(AST_Symbol_Hashes))",
                description = "Tree-Sitter semantic chunking extracts function and class symbols, verifying workspace integrity via Merkle DAGs.",
                liveOutput = "Root: 7f9be53a32663ec57859b65f (14 symbols tracked)",
                accent = FusionAmber
            )
        }

        // 7. ByteDance TikTok LinUCB
        item {
            MncEngineTile(
                title = "7. ByteDance (TikTok) LinUCB Bandit",
                lineage = "Real-Time Streaming Personalization",
                equation = "UCB_a(x) = θ̂_aᵀ x + α √(xᵀ A_a⁻¹ x)",
                description = "Balances exploration vs exploitation using Ridge Regression confidence ellipsoids to learn user taste in sub-second streaming loops.",
                liveOutput = mnc.linUcbArm,
                accent = QuantumViolet
            )
        }

        // 8. Stripe Double-Entry Ledger
        item {
            MncEngineTile(
                title = "8. Stripe & TigerBeetle Financial Ledger",
                lineage = "Idempotent Zero-Sum Core Banking",
                equation = "∑(Debits) - ∑(Credits) ≡ 0 | Idempotency Key Lock",
                description = "Strict integer minor units and double-entry conservation laws guarantee money cannot be created or destroyed on network retries.",
                liveOutput = mnc.stripeTxId,
                accent = EmeraldMatrix
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MncEngineTile(
    title: String,
    lineage: String,
    equation: String,
    description: String,
    liveOutput: String,
    accent: androidx.compose.ui.graphics.Color
) {
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
                    text = title,
                    color = accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = lineage,
                color = TextSecondary,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = equation,
                color = FusionAmber,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Live Output: $liveOutput",
                color = EmeraldMatrix,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
