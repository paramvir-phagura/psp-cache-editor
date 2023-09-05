package com.runescape.cs2.ast;


public interface IBreakableNode extends IControllableFlowNode {
	public FlowBlock getEnd();
	public boolean canBreak();
}
