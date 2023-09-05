package com.psp.cs2.ui.autocomplete.item

import com.psp.cs2.ui.autocomplete.AutoCompleteItem
import com.psp.cs2.ui.autocomplete.AutoCompleteItemType
import com.runescape.cs2.CS2Type

class AutoCompleteArgument(name: String, returnType: CS2Type) : AutoCompleteItem(name, returnType, AutoCompleteItemType.ARGUMENT)
