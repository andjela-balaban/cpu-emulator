package org.unibl.etf.instruction;

import java.util.*;

public class Instruction {
	private String opcode;	// operation
    private List<Operand> operands;

    public Instruction(String operation, List<Operand> operands) {
        this.opcode = operation;
        this.operands = operands;
    }

	public String getOpcode() {
		return opcode;
	}

	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	public List<Operand> getOperands() {
		return operands;
	}

	public void setOperands(List<Operand> operands) {
		this.operands = operands;
	}
	
	public void addOperands(Operand operand) {
		this.operands.add(operand);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(opcode + " ");
		if(opcode.equals("HALT")) {
			return sb.toString();
		}
		for(int i = 0; i < operands.size() - 1; i++) {
			sb.append(operands.get(i) + ", ");
		}
		sb.append(operands.get((operands.size() - 1)));
		return sb.toString();
		
	}
}
