package org.unibl.etf.memory;

import java.util.*;

public class EfficientDataMemory implements IDataMemory {
	private final int PAGE_SIZE = 8; // velicina stranice u bajtovima 
	private Map<Long, Byte[]> memory;
	
	public EfficientDataMemory() {
		this.memory = new HashMap<>();
	}

	@Override
	public void write(long address, byte data) {
		long pageKey = address / PAGE_SIZE;
        int index = (int) (address % PAGE_SIZE);

        Byte[] page = memory.get(pageKey);
        
        if (page == null) {
            page = new Byte[PAGE_SIZE];	// ako stranica ne postoji, alociraj novu stranicu
            memory.put(pageKey, page);
        }
        page[index] = data;
	}

	@Override
	public byte read(long address) {
		long pageKey = address / PAGE_SIZE;
        int index = (int) (address % PAGE_SIZE);

        Byte[] page = memory.get(pageKey);
        
        // ako stranica ne postoji, vratiti nulu
        if (page == null) {	
            return 0;
        }
        else {
        	if(page[index] == null) 
        		return 0;
        	
        	else
        		return page[index];
        }
	}	
	
	@Override
	public long getMemorySize() {
		return memory.size();
	}
}
