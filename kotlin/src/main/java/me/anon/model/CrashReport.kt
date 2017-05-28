package me.anon.model

import java.io.Serializable

data class CrashReport
(
	// App information
	val version: String = "unknown",
	val packageName: String = "unknown",
	val versionCode: String = "0",
	// Device information
	val model: String = "unknown",
	val manufacturer: String = "unknown",
	val osVersion: String = "unknown",
	val exception: Throwable? = null,
	val additionalMessage: String = "",
	val timestamp: Long = 0L
) : Serializable
