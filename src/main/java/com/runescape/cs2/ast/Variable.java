package com.runescape.cs2.ast;

import com.runescape.cs2.instructions.AbstractInstruction;
import com.runescape.cs2.CS2Type;

public interface Variable {

    String getName();
    CS2Type getType();
    AbstractInstruction generateStoreInstruction();
    AbstractInstruction generateLoadInstruction();

}
