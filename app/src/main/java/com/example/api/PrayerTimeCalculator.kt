package com.example.api

import java.util.Calendar
import kotlin.math.*

object PrayerTimeCalculator {

    data class PrayerTimes(
        val fajr: String,
        val sunrise: String,
        val dhuhr: String,
        val asr: String,
        val maghrib: String,
        val isha: String
    )

    /**
     * Calculates prayer times for a given date, coordinates, and timezone offset.
     * Uses Muslim World League standard conventions (Fajr = 18°, Isha = 17° or 18°).
     */
    fun calculate(
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerTimes {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        
        // 1. Calculate Julian Date / Estimate Sun Position
        // Approximate solar positioning equations
        val d = dayOfYear.toDouble()
        val g = (357.529 + 0.98560028 * d) % 360.0
        val q = (280.459 + 0.98564736 * d) % 360.0
        
        val gRad = Math.toRadians(g)
        val L = (q + 1.915 * sin(gRad) + 0.020 * sin(2.0 * gRad)) % 360.0
        val LRad = Math.toRadians(L)
        
        // Obliquity of the Ecliptic
        val e = 23.439 - 0.00000036 * d
        val eRad = Math.toRadians(e)
        
        // Sun Declination
        var declination = asin(sin(eRad) * sin(LRad))
        
        // Right Ascension of Sun
        var ra = atan2(cos(eRad) * sin(LRad), cos(LRad))
        ra = (Math.toDegrees(ra) + 360.0) % 360.0
        
        // Equation of time in degrees
        var eqt = q - ra
        if (eqt < -180.0) eqt += 360.0
        if (eqt > 180.0) eqt -= 360.0
        eqt /= 15.0 // Convert to hours
        
        // Convert coordinates to radians
        val latRad = Math.toRadians(latitude)
        
        // 2. Base calculations (in hours from midnight local time)
        // Midday (Dhuhr)
        val baseDhuhr = 12.0 - (longitude / 15.0) - eqt + timezoneOffsetHours
        
        // Hour Angles for Sunrise and Sunset (Alt angle = -0.833 degrees)
        val sunriseSunAlt = Math.toRadians(-0.833)
        var sunriseHourAngle = calculateHourAngle(sunriseSunAlt, latRad, declination)
        
        // Hour Angle for Fajr (Alt angle = -18.0 degrees)
        val fajrSunAlt = Math.toRadians(-18.0)
        var fajrHourAngle = calculateHourAngle(fajrSunAlt, latRad, declination)
        
        // Hour Angle for Isha (Alt angle = -17.0 degrees)
        val ishaSunAlt = Math.toRadians(-17.0)
        var ishaHourAngle = calculateHourAngle(ishaSunAlt, latRad, declination)
        
        // Asr Calculation (Shafi standard: Shadow factor = 1)
        val gAsr = atan(1.0 + tan(abs(latRad - declination)))
        var asrHourAngle = calculateHourAngle(gAsr, latRad, declination)

        // Sunrise & Sunset
        val sunrise = baseDhuhr - sunriseHourAngle
        val sunset = baseDhuhr + sunriseHourAngle
        
        // Fajr
        val fajr = baseDhuhr - fajrHourAngle
        
        // Asr
        val asr = baseDhuhr + asrHourAngle
        
        // Isha
        val isha = baseDhuhr + ishaHourAngle

        return PrayerTimes(
            fajr = formatTime(fajr),
            sunrise = formatTime(sunrise),
            dhuhr = formatTime(baseDhuhr),
            asr = formatTime(asr),
            maghrib = formatTime(sunset),
            isha = formatTime(isha)
        )
    }

    private fun calculateHourAngle(targetAltRad: Double, latRad: Double, decRad: Double): Double {
        val num = sin(targetAltRad) - sin(latRad) * sin(decRad)
        val den = cos(latRad) * cos(decRad)
        val cosH = num / den
        if (cosH < -1.0 || cosH > 1.0) {
            // Unreachable angles (midnight sun / polar night region fallbacks)
            return 6.0 // Simple 6 hour fallback
        }
        return Math.toDegrees(acos(cosH)) / 15.0
    }

    private fun formatTime(timeInHours: Double): String {
        var hoursVal = timeInHours % 24.0
        if (hoursVal < 0.0) hoursVal += 24.0
        
        val totalMinutes = (hoursVal * 60.0 + 0.5).toInt()
        val hours = (totalMinutes / 60) % 24
        val minutes = totalMinutes % 60
        
        return String.format("%02d:%02d", hours, minutes)
    }
}
