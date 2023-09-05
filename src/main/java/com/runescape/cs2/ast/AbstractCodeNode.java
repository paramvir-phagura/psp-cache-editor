package com.runescape.cs2.ast;

import com.runescape.cs2.CodePrinter;

public abstract class AbstractCodeNode {

	public abstract void print(CodePrinter printer);

	public AbstractCodeNode() {

	}

	@Override
	public String toString() {
//		return "disabled";
		return CodePrinter.print(this);
	}

}
