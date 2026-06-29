package org.unibl.etf.processor;

import java.util.*;

import org.unibl.etf.instruction.*;

public interface IProcessor {
	void loadProgram(List<Instruction> program);
	void start();
	void end();
}
