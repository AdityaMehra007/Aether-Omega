package com.example.engine

import com.example.model.OrbitalState
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * High-Precision Orbital Laser Telemetry & Relativistic Mechanics Engine.
 * Implements J2 secular geopotential perturbation, Bradley point-ahead angle,
 * and General + Special Relativistic atomic clock dilation.
 */
object OrbitalRelativityEngine {
    private const val MU_EARTH = 398600.4418 // km^3 / s^2
    private const val R_EARTH = 6378.137     // km
    private const val J2 = 1.08262668e-3
    private const val C_KM_S = 299792.458    // km/s

    fun calculateEphemeris(altitudeKm: Double, inclinationDeg: Double, groundStation: String): OrbitalState {
        val a = R_EARTH + altitudeKm
        val incRad = Math.toRadians(inclinationDeg)
        val n = sqrt(MU_EARTH / (a * a * a)) // Mean motion (rad/s)

        // J2 Secular RAAN precession rate (rad/s)
        val raanRateRadS = -1.5 * n * J2 * ((R_EARTH / a) * (R_EARTH / a)) * cos(incRad)
        val raanDegDay = Math.toDegrees(raanRateRadS) * 86400.0

        // Circular orbital speed
        val vOrb = sqrt(MU_EARTH / a)

        // Bradley aberration point-ahead angle in microradians (2 * v / c)
        val pointAheadUrad = (2.0 * vOrb / C_KM_S) * 1.0e6

        // Combined General Relativistic Gravitational Blueshift + Special Relativistic Time Dilation
        val gmC2 = MU_EARTH / (C_KM_S * C_KM_S)
        val clockDriftPpb = ((gmC2 / R_EARTH) - (1.5 * gmC2 / a) - 0.5 * ((vOrb / C_KM_S) * (vOrb / C_KM_S))) * 1.0e9

        return OrbitalState(
            altitudeKm = altitudeKm,
            inclinationDeg = inclinationDeg,
            orbitalVelocityKmS = (vOrb * 10000.0).toLong() / 10000.0,
            j2PrecessionDegDay = (raanDegDay * 10000.0).toLong() / 10000.0,
            pointAheadLeadUrad = (pointAheadUrad * 10000.0).toLong() / 10000.0,
            relativisticDriftPpb = (clockDriftPpb * 10000.0).toLong() / 10000.0,
            targetGroundStation = groundStation,
            isLaserFiring = true
        )
    }
}
