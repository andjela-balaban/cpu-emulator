package org.unibl.etf.memory;

import org.unibl.etf.instruction.*;

public interface IInstructionMemory {
	void storeAt(long address, Instruction instruction);
	Instruction getAt(long address);
	long getMemorySize();
}
