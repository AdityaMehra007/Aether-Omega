package com.example.model

data class AgentInfo(
    val id: String,
    val name: String,
    val role: String,
    val clearance: Int,
    val activeSkill: String,
    val status: AgentStatus,
    val recentLog: String
)

enum class AgentStatus {
    IDLE,
    OPTIMIZING,
    VERIFYING,
    CONSENSUS_LOCKED,
    COMMITTED
}

data class DagVertex(
    val vertexId: String,
    val senderAgent: String,
    val roundNum: Long,
    val parentHashes: List<String>,
    val payloadSummary: String,
    val digest: String,
    val timestampNs: Long
)

data class EngineMetric(
    val id: String,
    val name: String,
    val lineage: String,
    val latencyUs: Long,
    val receiptHash: String,
    val outputSummary: String,
    val isVerified: Boolean = true
)

data class TokamakState(
    val electronDensity: Double = 1.5e20, // m^-3
    val ionTempKev: Double = 15.2, // keV
    val confinementTimeSec: Double = 2.15, // seconds
    val tearingModeAmplitude: Double = 0.042, // meters
    val eccdGyrotronMw: Double = 18.5, // MW
    val coilCurrentsKa: List<Double> = List(18) { idx ->
        12.0 + 2.5 * kotlin.math.sin(2.0 * Math.PI * idx / 18.0)
    },
    val isScrammed: Boolean = false,
    val lastUpdateMs: Long = System.currentTimeMillis()
) {
    val tripleProduct: Double
        get() = electronDensity * ionTempKev * confinementTimeSec

    val isIgnited: Boolean
        get() = !isScrammed && tripleProduct >= 3.0e21 // Lawson Criterion for D-T plasma
}

data class OrbitalState(
    val altitudeKm: Double = 1150.0,
    val inclinationDeg: Double = 53.2,
    val orbitalVelocityKmS: Double = 7.2765,
    val j2PrecessionDegDay: Double = -3.3412,
    val pointAheadLeadUrad: Double = 48.5439,
    val relativisticDriftPpb: Double = -0.4829,
    val targetGroundStation: String = "CERN Primary Geo-Hub",
    val isLaserFiring: Boolean = true
)

data class SurfaceCodeState(
    val dataQubits: List<Int> = List(9) { 0 }, // 0 = |0>, 1 = |1>
    val xSyndromes: List<Int> = List(4) { 0 }, // Phase flip detection
    val zSyndromes: List<Int> = List(4) { 0 }, // Bit flip detection
    val injectedErrorQubits: Map<Int, String> = emptyMap(), // Qubit index -> "X" or "Z"
    val decodedCorrection: String = "NOMINAL (NO ERRORS DETECTED)",
    val logicalFidelity: Float = 1.0f
)

data class RingLweState(
    val degree: Int = 64,
    val modulus: Int = 3329,
    val originalBitstring: String = "1011001011100010",
    val decryptedBitstring: String = "1011001011100010",
    val ciphertextSnippet: String = "c1:[2841, 192, 3102,...] c2:[941, 2304,...]",
    val noiseVariance: Double = 1.85,
    val isVerifiedExact: Boolean = true
)

data class MncResults(
    val spannerHlc: String = "HLC: (1794058095738483, 2, us-east-1)",
    val spannerEpsilonMs: Double = 1.2,
    val auroraAcks: String = "Quorum 5/6 ACKs (Vw >= 4) LSN: 84920",
    val crdtConvergedVal: String = "Grid Bus: 42.5 TW (Converged SEC)",
    val pagedAttnSharing: String = "Pages: [0, 1, 3] (2 Prefix Hits)",
    val flashAttnError: String = "2.22e-16 (Exact IEEE-754 machine precision)",
    val linUcbArm: String = "Selected Arm #0 (UCB: 0.8000)",
    val stripeTxId: String = "tx_88a4c1 (Sum == 0 Verified)",
    val isAllPassing: Boolean = true
)
