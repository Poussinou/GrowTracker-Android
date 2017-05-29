package me.anon.lib

import android.content.Context
import android.preference.PreferenceManager
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Unit class used for measurement input
 */
enum class Unit constructor(val label: String)
{
	ML("ml")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				L -> return toTwoDecimalPlaces(fromValue * 0.001)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.000219969)
				QUART -> return toTwoDecimalPlaces(fromValue * 0.000879877)
				TSP -> return toTwoDecimalPlaces(fromValue * 0.168936)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.000264172)
				USQUART -> return toTwoDecimalPlaces(fromValue * 0.00105669)
				USTSP -> return toTwoDecimalPlaces(fromValue * 0.202884)
			}

			return fromValue
		}
	},

	L("l")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 1000.0)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.219969)
				QUART -> return toTwoDecimalPlaces(fromValue * 0.879877)
				TSP -> return toTwoDecimalPlaces(fromValue * 168.936)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.264172)
				USQUART -> return toTwoDecimalPlaces(fromValue * 1.05669)
				USTSP -> return toTwoDecimalPlaces(fromValue * 202.884)
			}

			return fromValue
		}
	},

	GAL("gal")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 4546.09)
				L -> return toTwoDecimalPlaces(fromValue * 4.54609)
				QUART -> return toTwoDecimalPlaces(fromValue * 4.0)
				TSP -> return toTwoDecimalPlaces(fromValue * 768.0)
				USGAL -> return toTwoDecimalPlaces(fromValue * 1.20095)
				USQUART -> return toTwoDecimalPlaces(fromValue * 4.8038)
				USTSP -> return toTwoDecimalPlaces(fromValue * 922.33)
			}

			return fromValue
		}
	},

	QUART("quart")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 1136.52)
				L -> return toTwoDecimalPlaces(fromValue * 1.13652)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.25)
				TSP -> return toTwoDecimalPlaces(fromValue * 192.0)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.300237)
				USQUART -> return toTwoDecimalPlaces(fromValue * 1.20095)
				USTSP -> return toTwoDecimalPlaces(fromValue * 230.582)
			}

			return fromValue
		}
	},

	TSP("tsp")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 5.91939)
				L -> return toTwoDecimalPlaces(fromValue * 0.00591939)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.00130208)
				QUART -> return toTwoDecimalPlaces(fromValue * 0.00520834)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.00156374)
				USQUART -> return toTwoDecimalPlaces(fromValue * 0.00625495)
				USTSP -> return toTwoDecimalPlaces(fromValue * 1.20095)
			}

			return fromValue
		}
	},

	USGAL("us gal")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 3785.41)
				L -> return toTwoDecimalPlaces(fromValue * 3.78541)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.832674)
				QUART -> return toTwoDecimalPlaces(fromValue * 4.0)
				TSP -> return toTwoDecimalPlaces(fromValue * 639.494)
				USQUART -> return toTwoDecimalPlaces(fromValue * 0.00130208)
				USTSP -> return toTwoDecimalPlaces(fromValue * 0.00130208)
			}

			return fromValue
		}
	},

	USQUART("us quart")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 946.353)
				L -> return toTwoDecimalPlaces(fromValue * 0.946353)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.208169)
				QUART -> return toTwoDecimalPlaces(fromValue * 0.832674)
				TSP -> return toTwoDecimalPlaces(fromValue * 159.873)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.25)
				USTSP -> return toTwoDecimalPlaces(fromValue * 192.0)
			}

			return fromValue
		}
	},

	USTSP("us tsp")
	{
		override fun to(to: Unit, fromValue: Double): Double
		{
			when (to)
			{
				ML -> return toTwoDecimalPlaces(fromValue * 4.92892)
				L -> return toTwoDecimalPlaces(fromValue * 0.00492892)
				GAL -> return toTwoDecimalPlaces(fromValue * 0.00108421)
				QUART -> return toTwoDecimalPlaces(fromValue * 0.00433684)
				TSP -> return toTwoDecimalPlaces(fromValue * 0.832674)
				USGAL -> return toTwoDecimalPlaces(fromValue * 0.00130208)
				USQUART -> return toTwoDecimalPlaces(fromValue * 0.00520833)
			}

			return fromValue
		}
	};

	/**
	 * x to given unit
	 * @param to Unit to convert to
	 * *
	 * @param fromValue ml value
	 * *
	 * @return converted value
	 */
	abstract fun to(to: Unit, fromValue: Double): Double

	companion object
	{
		private fun toTwoDecimalPlaces(input: Double): Double
		{
			return BigDecimal(input).setScale(2, RoundingMode.HALF_EVEN).toDouble()
		}

		fun getSelectedDeliveryUnit(context: Context): Unit
		{
			val index: Int = PreferenceManager.getDefaultSharedPreferences(context).getInt("delivery_unit", -1)
			return values()[if (index == -1) L.ordinal else index]
		}

		fun getSelectedMeasurementUnit(context: Context): Unit
		{
			val index: Int = PreferenceManager.getDefaultSharedPreferences(context).getInt("measurement_unit", -1)
			return values()[if (index == -1) ML.ordinal else index]
		}
	}
}
