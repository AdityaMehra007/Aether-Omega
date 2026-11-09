package com.example

import com.example.engine.OrbitalRelativityEngine
import com.example.engine.QuantumSurfaceCodeEngine
import com.example.engine.RingLweEngine
import com.example.engine.TokamakMhdEngine
import com.example.model.SurfaceCodeState
import com.example.model.TokamakState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testRingLweEncryptionDecryptionExactMatch() {
        val testBits = "1011001011100010"
        val state = RingLweEngine.executeKeyGenAndEncryption(testBits)
        assertTrue("Ring-LWE decryption must match original bits", state.isVerifiedExact)
        assertEquals(testBits, state.decryptedBitstring)
    }

    @Test
    fun testQuantumSurfaceCodeSyndromeDecoder() {
        val state = SurfaceCodeState()
        // Inject error on Q4
        val stateWithNoise = QuantumSurfaceCodeEngine.injectError(state, 4, "X")
        assertTrue("Should detect noise", stateWithNoise.logicalFidelity < 1.0f)
        assertTrue("Z stabilizers should have non-zero syndrome", stateWithNoise.zSyndromes.any { it == 1 })

        // Decode syndromes
        val restoredState = QuantumSurfaceCodeEngine.runSyndromeDecoder(stateWithNoise)
        assertEquals(1.0f, restoredState.logicalFidelity, 0.001f)
        assertTrue(restoredState.injectedErrorQubits.isEmpty())
    }

    @Test
    fun testTokamakLawsonIgnition() {
        val state = TokamakState(
            electronDensity = 1.5e20,
            ionTempKev = 15.0,
            confinementTimeSec = 2.0
        )
        // 1.5e20 * 15 * 2 = 4.5e21 >= 3.0e21
        assertTrue("Tokamak must be ignited (Lawson criterion)", state.isIgnited)
    }

    @Test
    fun testOrbitalJ2Precession() {
        val ephemeris = OrbitalRelativityEngine.calculateEphemeris(1150.0, 53.2, "CERN Primary Geo-Hub")
        assertTrue("Orbital velocity should be ~7.28 km/s", ephemeris.orbitalVelocityKmS in 7.0..7.5)
        assertTrue("J2 precession should be negative", ephemeris.j2PrecessionDegDay < 0.0)
    }
}
