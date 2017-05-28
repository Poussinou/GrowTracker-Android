package me.anon.model

import java.util.*

/**
 * // TODO: Add class description
 */
data class Water
(
	val ppm: Double? = null,
	val ph: Double? = null,
	val runoff: Double? = null,
	val amount: Double? = null,
	val temp: Int? = null,
	val additives: List<Additive> = ArrayList<Additive>()
) : Action()
