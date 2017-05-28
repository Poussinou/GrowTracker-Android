package me.anon.model

/**
 * // TODO: Add class description
 */
enum class PlantStage constructor(val printString: String)
{
	PLANTED("Planted"),
	GERMINATION("Germination"),
	VEGETATION("Vegetation"),
	FLOWER("Flower"),
	DRYING("Drying"),
	CURING("Curing"),
	HARVESTED("Harvested");

	companion object
	{
		fun names(): List<String> = Action.ActionName.values().map { it.printString }
	}
}
