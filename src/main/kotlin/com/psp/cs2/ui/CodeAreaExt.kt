package com.psp.cs2.ui

import com.psp.controller.Cs2EditorController
import org.fxmisc.richtext.CodeArea
import java.time.Duration

fun CodeArea.buildStyle() {
	val cleanupWhenNoLongerNeedIt = richChanges().successionEnds(Duration.ofMillis(500)).subscribe {
		setStyleSpans(0, Cs2EditorController.computeHighlighting(text))
	}
}
