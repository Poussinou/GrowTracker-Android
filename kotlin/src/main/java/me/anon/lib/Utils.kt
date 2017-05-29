package me.anon.lib

/**
 * Converts assumed long milliseconds into days
 */
fun Long.toDays(): Double = this / (1000.0 * 60.0 * 60.0 * 24.0)
