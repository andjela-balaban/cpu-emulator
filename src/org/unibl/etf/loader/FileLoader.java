package org.unibl.etf.loader;

import java.util.*;
import org.unibl.etf.instruction.*;
import java.io.*;
import java.nio.file.*;


public class FileLoader implements Loader {
	private Path filePath;
	
	public FileLoader(Path filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public List<Instruction> loadInstructions(){
		List<Instruction> instructions = new ArrayList<>();
		try {
			List<String> lines = Files.readAllLines(filePath);
			
			String opcode = null;
			for(String line : lines) {
				List<Operand> operands = new ArrayList<>();
				String[] parts = line.split(" ", 2);	// makismalan broj dijelova na koji string treba da se podijeli
				opcode = parts[0];
				if(opcode.equals("HALT")) {
					instructions.add(new Instruction(opcode, null));
				}
				else {
					operands = parseOperands(parts[1]);
					instructions.add(new Instruction(opcode, operands));					
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}

		return instructions;
	}
	
	
	private static List<Operand> parseOperands(String operandString) {
		List<Operand> operands = new ArrayList<>();
		String[] operandParts = operandString.split(",");

		for (String operandPart : operandParts) {
			operandPart = operandPart.trim();
			AddressingType addressingType;
			String value;

			if (operandPart.matches("R[0-3]")) {  // Registar
				addressingType = AddressingType.REGISTER;
				value = operandPart;
			} 
			else if (operandPart.startsWith("#")) {  // Immediate adresiranje
	            addressingType = AddressingType.IMMEDIATE;
	            value = operandPart.substring(1); 
			}
			else if (operandPart.startsWith("[") && operandPart.endsWith("]")) {  // Direktno adresiranje
				addressingType = AddressingType.DIRECT;
				value = operandPart.substring(1, operandPart.length() - 1);
			} 
			else if (operandPart.startsWith("(") && operandPart.endsWith(")")) {  // Indirektno adresiranje
				addressingType = AddressingType.INDIRECT;
				value = operandPart.substring(1, operandPart.length() - 1);
			} 
			else {
				throw new IllegalArgumentException("Nepoznat format operanda: " + operandPart);
			}

			operands.add(new Operand(value, addressingType));
		}
		
		return operands;
	}
	
}