package org.unibl.etf.memory;

import java.util.*;

public class DataMemory implements IDataMemory {
	private Map<Long, Byte> memory;
	
	public DataMemory(){
		this.memory = new HashMap<>();
	}
	
	@Override
	public void write(long address, byte data) {
		memory.put(address, data);
	}
	
	@Override
	public byte read(long address) {
		return memory.getOrDefault(address, (byte) 0); // vraca 0 ako adresa nije vec inicijalizovana
	}
	
	@Override
	public long getMemorySize() {
		return memory.size();
	}
}
