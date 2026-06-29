package org.unibl.etf.memory;

import java.util.*;

import org.unibl.etf.instruction.*;

public class InstructionMemory implements IInstructionMemory {
	private Map<Long, Instruction> memory;
	
	public InstructionMemory() {
		this.memory = new HashMap<>();
	}
	
	public InstructionMemory(Map<Long, Instruction> memory) {
		this.memory = memory;
	}

	@Override
	public long getMemorySize() {
		return memory.size();
	}
	
	@Override
	public void storeAt(long address, Instruction instruction) {
		memory.put(address, instruction);
	}

	@Override
	public Instruction getAt(long address) {
		return memory.get(address);
	}
}
