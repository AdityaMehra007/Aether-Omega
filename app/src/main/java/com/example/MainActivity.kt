package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AetherBottomNav
import com.example.ui.components.AetherHeader
import com.example.ui.screens.MncScreen
import com.example.ui.screens.OrbitalScreen
import com.example.ui.screens.QuantumScreen
import com.example.ui.screens.SingularityScreen
import com.example.ui.screens.TokamakScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SpaceBlack
import com.example.viewmodel.AetherOmegaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AetherOmegaApp()
            }
        }
    }
}

@Composable
fun AetherOmegaApp(
    viewModel: AetherOmegaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle back button to return to master command tab
    BackHandler(enabled = uiState.currentTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SpaceBlack,
        topBar = {
            AetherHeader(
                netEnergyMw = if (uiState.tokamak.isScrammed) 0.0 else 450.0,
                isScrammed = uiState.tokamak.isScrammed
            )
        },
        bottomBar = {
            AetherBottomNav(
                selectedTab = uiState.currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBlack)
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                0 -> SingularityScreen(
                    state = uiState,
                    onExecuteCycle = { viewModel.executeSingularityCycle() }
                )
                1 -> TokamakScreen(
                    tokamak = uiState.tokamak,
                    onPowerChanged = { viewModel.setGyrotronPower(it) },
                    onScram = { viewModel.triggerEmergencyScram() },
                    onRestore = { viewModel.restoreReactor() }
                )
                2 -> OrbitalScreen(
                    orbital = uiState.orbital,
                    onGroundStationChanged = { viewModel.updateGroundStation(it) },
                    onToggleLaser = { viewModel.toggleLaser() }
                )
                3 -> QuantumScreen(
                    surfaceCode = uiState.surfaceCode,
                    ringLwe = uiState.ringLwe,
                    onInjectError = { q, type -> viewModel.injectQuantumError(q, type) },
                    onDecodeSyndromes = { viewModel.decodeSurfaceCode() },
                    onTestRingLwe = { viewModel.testRingLweEncryption(it) }
                )
                4 -> MncScreen(
                    mnc = uiState.mncResults
                )
            }
        }
    }
}
