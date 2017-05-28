package me.anon.model

/**
 * // TODO: Add class description
 */
enum class PlantMedium constructor(val printString: String)
{
	SOIL("Soil"),
	HYDRO("Hydroponics");

	companion object
	{
		fun names(): List<String> = Action.ActionName.values().map { it.printString }
	}
}
