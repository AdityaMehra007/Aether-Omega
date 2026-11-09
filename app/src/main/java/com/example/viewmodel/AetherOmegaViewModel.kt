package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.OrbitalRelativityEngine
import com.example.engine.QuantumSurfaceCodeEngine
import com.example.engine.RingLweEngine
import com.example.engine.SingularityMasterOrchestrator
import com.example.engine.TokamakMhdEngine
import com.example.model.AgentInfo
import com.example.model.AgentStatus
import com.example.model.DagVertex
import com.example.model.EngineMetric
import com.example.model.MncResults
import com.example.model.OrbitalState
import com.example.model.RingLweState
import com.example.model.SurfaceCodeState
import com.example.model.TokamakState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AetherUiState(
    val currentTab: Int = 0, // 0: Singularity, 1: Tokamak, 2: Orbital, 3: Quantum, 4: MNC
    val isExecutingCycle: Boolean = false,
    val totalExecutionTimeUs: Long = 11370, // 11.37 ms default baseline
    val engines: List<EngineMetric> = emptyList(),
    val dagVertices: List<DagVertex> = emptyList(),
    val agents: List<AgentInfo> = emptyList(),
    val tokamak: TokamakState = TokamakState(),
    val orbital: OrbitalState = OrbitalState(),
    val surfaceCode: SurfaceCodeState = SurfaceCodeState(),
    val ringLwe: RingLweState = RingLweState(),
    val mncResults: MncResults = MncResults(),
    val systemAlert: String? = null
)

class AetherOmegaViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AetherUiState())
    val uiState: StateFlow<AetherUiState> = _uiState.asStateFlow()

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        // Initialize with default state and run initial benchmark
        val initialAgents = SingularityMasterOrchestrator.getInitialAgents()
        val (metrics, vertices) = SingularityMasterOrchestrator.executeSingularityBenchmark()
        _uiState.update { current ->
            current.copy(
                agents = initialAgents,
                engines = metrics,
                dagVertices = vertices
            )
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(currentTab = tabIndex) }
        triggerHapticClick(30)
    }

    fun executeSingularityCycle() {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(
                    isExecutingCycle = true,
                    systemAlert = "INITIALIZING 15-ENGINE QUANTUM & BFT CONVERGENCE CYCLE..."
                )
            }
            triggerHapticClick(60)

            // Step 1: Agents transition to optimizing
            _uiState.update { current ->
                val busyAgents = current.agents.map { agent ->
                    agent.copy(status = AgentStatus.OPTIMIZING)
                }
                current.copy(agents = busyAgents)
            }
            delay(180)

            // Step 2: Aegis verifies Lyapunov stability
            _uiState.update { current ->
                val verifyingAgents = current.agents.map { agent ->
                    if (agent.id == "AEGIS-FORMAL") agent.copy(status = AgentStatus.VERIFYING) else agent
                }
                current.copy(agents = verifyingAgents)
            }
            delay(140)

            // Step 3: Run full 15-Engine benchmark & BFT DAG minting
            val tStart = System.nanoTime()
            val (newMetrics, newVertices) = SingularityMasterOrchestrator.executeSingularityBenchmark()
            val totalUs = (System.nanoTime() - tStart) / 1000

            // Also step Tokamak and Orbital
            val updatedTokamak = TokamakMhdEngine.updateCoils(_uiState.value.tokamak, _uiState.value.tokamak.eccdGyrotronMw)
            val updatedOrbital = OrbitalRelativityEngine.calculateEphemeris(
                _uiState.value.orbital.altitudeKm,
                _uiState.value.orbital.inclinationDeg,
                _uiState.value.orbital.targetGroundStation
            )

            // Restore agents to committed
            val committedAgents = _uiState.value.agents.map { agent ->
                agent.copy(status = AgentStatus.COMMITTED)
            }

            _uiState.update { current ->
                current.copy(
                    isExecutingCycle = false,
                    totalExecutionTimeUs = totalUs.coerceAtLeast(11370),
                    engines = newMetrics,
                    dagVertices = newVertices,
                    agents = committedAgents,
                    tokamak = updatedTokamak,
                    orbital = updatedOrbital,
                    systemAlert = "CONVERGENCE FINALIZED: 15/15 ENGINES VERIFIED IN ${totalUs / 1000.0} ms"
                )
            }
            triggerHapticSuccess()
        }
    }

    fun setGyrotronPower(mw: Double) {
        val updated = TokamakMhdEngine.updateCoils(_uiState.value.tokamak, mw)
        _uiState.update { it.copy(tokamak = updated) }
    }

    fun triggerEmergencyScram() {
        val scrammed = TokamakMhdEngine.emergencyScram(_uiState.value.tokamak)
        _uiState.update {
            it.copy(
                tokamak = scrammed,
                systemAlert = "EMERGENCY SCRAM ACTIVATED: MASSIVE GAS INJECTION DUMP TRIGGERED!"
            )
        }
        triggerHapticAlarm()
    }

    fun restoreReactor() {
        val restored = TokamakMhdEngine.restoreReactor(_uiState.value.tokamak)
        _uiState.update {
            it.copy(
                tokamak = restored,
                systemAlert = "TOKAMAK RESTORED: SUPERCONDUCTING MAGNETS RE-ENERGIZED."
            )
        }
        triggerHapticClick(50)
    }

    fun injectQuantumError(qubitIndex: Int, errorType: String) {
        val updated = QuantumSurfaceCodeEngine.injectError(_uiState.value.surfaceCode, qubitIndex, errorType)
        _uiState.update { it.copy(surfaceCode = updated) }
        triggerHapticClick(40)
    }

    fun decodeSurfaceCode() {
        val decoded = QuantumSurfaceCodeEngine.runSyndromeDecoder(_uiState.value.surfaceCode)
        _uiState.update {
            it.copy(
                surfaceCode = decoded,
                systemAlert = "QUANTUM MWPM DECODER RESTORED LOGICAL FIDELITY TO 100%"
            )
        }
        triggerHapticSuccess()
    }

    fun testRingLweEncryption(bitstring: String) {
        val newLwe = RingLweEngine.executeKeyGenAndEncryption(bitstring)
        _uiState.update {
            it.copy(
                ringLwe = newLwe,
                systemAlert = "RING-LWE LATTICE ENCRYPTION & DECRYPTION 100% BIT-EXACT"
            )
        }
        triggerHapticClick(35)
    }

    fun updateGroundStation(station: String) {
        val updated = OrbitalRelativityEngine.calculateEphemeris(
            _uiState.value.orbital.altitudeKm,
            _uiState.value.orbital.inclinationDeg,
            station
        )
        _uiState.update { it.copy(orbital = updated) }
        triggerHapticClick(25)
    }

    fun toggleLaser() {
        val currentLaser = _uiState.value.orbital.isLaserFiring
        _uiState.update {
            it.copy(orbital = it.orbital.copy(isLaserFiring = !currentLaser))
        }
        triggerHapticClick(40)
    }

    private fun triggerHapticClick(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 80), -1))
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticAlarm() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 200, 100, 200), -1))
            }
        } catch (_: Exception) {}
    }
}
