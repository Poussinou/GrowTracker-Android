package me.anon.lib

import org.junit.Assert
import org.junit.Test

/**
 * // TODO: Add class description
 */
class UtilsTest
{
	@Test
	fun testUtils_ToDays()
	{
		val oneDay = 60 * 60 * 24 * 1000L
		Assert.assertTrue(oneDay.toDays() == 1.0)
	}
}
