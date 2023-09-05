package com.psp.cs2.ui.autocomplete.item

import com.psp.cs2.ui.autocomplete.AutoCompleteItem
import com.psp.cs2.ui.autocomplete.AutoCompleteItemType
import com.runescape.cs2.CS2Type

class AutoCompleteFunction(name: String, returnType: CS2Type, val arguments: Array<AutoCompleteArgument>)
	: AutoCompleteItem(name, returnType, AutoCompleteItemType.METHOD) {

	override var displayName = "$name(${arguments.joinToString(", ") { "${it.returnType.name} ${it.name}" }})"

}
