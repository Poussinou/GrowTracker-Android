package me.anon.model

/**
 * // TODO: Add class description
 */
abstract class Action : Cloneable
{
	val date = System.currentTimeMillis()
	val notes: String? = null

	enum class ActionName constructor(val printString: String, val colour: Int)
	{
		FIM("Fuck I Missed", 0x9AFFCC80.toInt()),
		FLUSH("Flush", 0x9AFFE082.toInt()),
		FOLIAR_FEED("Foliar Feed", 0x9AE6EE9C.toInt()),
		LST("Low Stress Training", 0x9AFFF59D.toInt()),
		LOLLIPOP("Lollipop", 0x9AFFD180.toInt()),
		PESTICIDE_APPLICATION("Pesticide Application", 0x9AEF9A9A.toInt()),
		TOP("Topped", 0x9ABCAAA4.toInt()),
		TRANSPLANTED("Transplanted", 0x9AFFFF8D.toInt()),
		TRIM("Trim", 0x9AFFAB91.toInt());

		companion object
		{
			fun names(): List<String> = values().map { it.printString }
		}
	}
}
