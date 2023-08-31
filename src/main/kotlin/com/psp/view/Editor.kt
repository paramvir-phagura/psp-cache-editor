package com.psp.view

import javafx.beans.property.SimpleStringProperty
import tornadofx.View

sealed class Editor(val name: String) : View() {

    val nameProp = SimpleStringProperty(name)
}
