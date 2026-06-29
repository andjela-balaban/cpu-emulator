package org.unibl.etf.memory;

public interface IDataMemory {
	void write(long address, byte data);
	byte read(long address);
	long getMemorySize();
}
