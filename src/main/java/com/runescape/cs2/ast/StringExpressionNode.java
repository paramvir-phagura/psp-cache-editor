package com.runescape.cs2.ast;

import com.runescape.cs2.CS2Type;
import com.runescape.cs2.CodePrinter;
import com.runescape.cs2.util.TextUtils;

public class StringExpressionNode extends ExpressionNode /*implements IIntConstantNode<String>*/ {

    /**
     * Contains string which this expression holds.
     */
    private String data;

    public StringExpressionNode(String data) {
    	this.data = data;
    }

    public String getData() {
    	return data;
    }

    @Override
    public CS2Type getType() {
    	return CS2Type.STRING;
    }
/*
	@Override
	public String getConst() {
		return this.data;
	}
*/
	@Override
	public ExpressionNode copy() {
		return new StringExpressionNode(this.data);
	}

	@Override
	public void print(CodePrinter printer) {
		printer.print(TextUtils.quote(data));
	}

}
