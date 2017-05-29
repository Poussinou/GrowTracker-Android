package me.anon.model

import android.content.Context
import me.anon.lib.DateRenderer
import me.anon.lib.Unit
import me.anon.lib.toDays
import java.util.*

/**
 * Plant object as per json spec
 */
data class Plant(
	var id: String = UUID.randomUUID().toString(),
	var name: String = "",
	var strain: String = "",
	var plantDate: Long = System.currentTimeMillis(),
	var clone: Boolean = false,
	var medium: PlantMedium = PlantMedium.SOIL,
	var mediumDetails: String = "",
	var images: ArrayList<String> = ArrayList<String>(),
	var actions: ArrayList<Action> = ArrayList<Action>()
)
{
	/**
	 * Gets the current stage of the plant, or null
	 */
	fun getStage(): PlantStage? = (actions.lastOrNull { action -> action is StageChange } as StageChange).newStage

	/**
	 * Returns a map of plant stages
	 * @return
	 */
	fun getStages(): Map<PlantStage, Action> = actions.filter { action -> action is StageChange }.associateBy({(it as StageChange).newStage}, {it})

	/**
	 * Generates a short textual summary of watering details of the plant
	 */
	fun generateShortSummary(context: Context): String
	{
		val deliveryUnit = Unit.getSelectedDeliveryUnit(context)
		val stage = getStage()
		var summary = ""

		if (stage == PlantStage.HARVESTED)
		{
			summary += "Harvested"
		}
		else
		{
			val planted = DateRenderer().timeAgo(plantDate.toDouble(), 3)
			summary += "<b>" + planted.time.toInt() + " " + planted.unit.type + "</b>"

			val lastWater: Water? = actions.lastOrNull { action -> action is Water } as Water
			val stageTimes = calculateStageTime()

			if (stageTimes.containsKey(stage) && stage != null)
			{
				summary += " / <b>" + (stageTimes[stage] ?: 0).toDays() + stage.printString.substring(0, 1).toLowerCase() + "</b>"
			}

			if (lastWater != null)
			{
				summary += "<br/>"
				summary += "Watered: <b>" + DateRenderer().timeAgo(lastWater.date.toDouble()).formattedDate + "</b> ago"
				summary += "<br/>"

				if (lastWater.ph != null)
				{
					summary += "<b>" + lastWater.ph + " PH</b> "

					if (lastWater.runoff != null)
					{
						summary += "-> <b>" + lastWater.runoff + " PH</b> "
					}
				}

				if (lastWater.amount != null)
				{
					summary += "<b>" + Unit.ML.to(deliveryUnit, lastWater.amount) + deliveryUnit.label + "</b>"
				}
			}
		}

		if (summary.endsWith("<br/>"))
		{
			summary = summary.substring(0, summary.length - "<br/>".length)
		}

		return summary
	}

	/**
	 * Generates a long summary of the last watering details for the plant
	 */
	fun generateLongSummary(context: Context): String
	{
		val measureUnit = Unit.getSelectedMeasurementUnit(context)
		val deliveryUnit = Unit.getSelectedDeliveryUnit(context)
		var summary = strain + " - "
		val stage = getStage()

		if (stage == PlantStage.HARVESTED)
		{
			summary += "Harvested"
		}
		else
		{
			val planted = DateRenderer().timeAgo(plantDate.toDouble(), 3)
			summary += "<b>Planted " + planted.time.toInt() + " " + planted.unit.type + " ago</b>"

			val lastWater: Water? = actions.lastOrNull { action -> action is Water } as Water
			val stageTimes = calculateStageTime()

			if (stageTimes.containsKey(stage) && stage != null)
			{
				summary += " / <b>" + (stageTimes[stage] ?: 0L).toDays() + stage.printString.substring(0, 1).toLowerCase() + "</b>"
			}

			if (lastWater != null)
			{
				summary += "<br/><br/>"
				summary += "Last watered: <b>" + DateRenderer().timeAgo(lastWater.date.toDouble()).formattedDate + "</b> ago"
				summary += "<br/>"

				if (lastWater.ph != null)
				{
					summary += "<b>" + lastWater.ph + " PH</b> "

					if (lastWater.runoff != null)
					{
						summary += "-> <b>" + lastWater.runoff + " PH</b> "
					}
				}

				if (lastWater.amount != null)
				{
					summary += "<b>" + Unit.ML.to(deliveryUnit, lastWater.amount) + deliveryUnit.label + "</b>"
				}

				if (lastWater.additives.size > 0)
				{
					var total = 0.0
					for (additive in lastWater.additives)
					{
						total += additive.amount ?: 0.0
					}

					summary += "<br/> + <b>" + Unit.ML.to(measureUnit, total) + measureUnit.label + "</b> additives"
				}
			}
		}

		if (summary.endsWith("<br/>"))
		{
			summary = summary.substring(0, summary.length - "<br/>".length)
		}

		return summary
	}

	/**
	 * Calculates the time spent in each plant stage
	 *
	 * @return The list of plant stages with time in milliseconds. Keys are in order of stage defined in [PlantStage]
	 */
	fun calculateStageTime(): SortedMap<PlantStage, Long>
	{
		val endDate = actions.findLast { action -> (action is StageChange) && action.newStage == PlantStage.HARVESTED }?.date ?: System.currentTimeMillis()
		var stages = mutableMapOf<PlantStage, Long>()

		stages.putAll(
			actions
				.filter {
					action -> action is StageChange
				}
				.associateBy(
					{(it as StageChange).newStage}, {it.date}
				)
		)

		stages = stages.toSortedMap(compareBy<PlantStage> { stage -> stage.ordinal })
		var lastStage: Long = 0

		for ((stageIndex, plantStage) in stages.keys.withIndex())
		{
			var difference: Long

			if (stageIndex == 0)
			{
				difference = endDate - (stages[plantStage] ?: 0)
			}
			else
			{
				difference = lastStage - (stages[plantStage] ?: 0)
			}

			lastStage = stages[plantStage] ?: 0
			stages[plantStage] = difference
		}

		return stages
	}
}
