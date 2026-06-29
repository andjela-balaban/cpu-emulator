package org.unibl.etf.processor;

import java.util.*;
import org.unibl.etf.instruction.*;
import org.unibl.etf.memory.*;

public class Processor implements IProcessor {
	private Map<String, Long> registers;	
	private long pc;			// program counter
	private Instruction instructionRegister;
	private IInstructionMemory instructionMemory;
	private IDataMemory dataMemory;
	private boolean halt;
	private boolean equalsFlagRegister;
	private boolean lesserFlagRegister;
	private boolean greaterFlagRegister;
	private Scanner scanner;
	
	
	public Processor() {
		this.registers = new HashMap<>();
		registers.put("R0", 0L);
		registers.put("R1", 0L);
		registers.put("R2", 0L);
        registers.put("R3", 0L);
        this.instructionMemory = new InstructionMemory();
//        this.dataMemory = new DataMemory();
        this.dataMemory = new EfficientDataMemory();
        this.halt = false;
        this.equalsFlagRegister = false;
        this.lesserFlagRegister = false;
        this.greaterFlagRegister = false;
        this.scanner = new Scanner(System.in);
	}
	
	@Override
	public void loadProgram(List<Instruction> program) {
		long address = 0;
		for(Instruction instruction : program) {
			instructionMemory.storeAt(address++, instruction);
		}
	}

	@Override
	public void start() {
		pc = 0;
		while (!halt) {
	        fetchInstruction();		// fetch faza - preuzimanje instrukcije iz memorije(Instruction memory)
	        decodeAndExecuteInstruction();	// decode i execute faze - dekodovanje i izvrsavanje instrukcija
	    }
		end();
	}
	
	@Override
	public void end() {
		scanner.close();
		System.out.println("Procesor zaustavljen - program je zavrsen!");
	}
	
	public void fetchInstruction() {
		instructionRegister = instructionMemory.getAt(pc);
		pc++;
	}
	
	public void decodeAndExecuteInstruction() {
		List<Operand> operands = instructionRegister.getOperands();
		String operation = instructionRegister.getOpcode().toUpperCase();
		System.out.println("\033[1m" + instructionRegister + "\033[0m");
		switch(operation) {
//			osnovne aritmeticke operacije
			case "ADD":		
				add(operands);
				break;
			case "SUB":
				sub(operands);
				break;
			case "MUL":
				mul(operands);
				break;
			case "DIV":
				div(operands);
				break;
				
//			osnovne bitske logicke operacije
			case "AND":
				and(operands);
				break;
			case "OR":
				or(operands);
				break;	
			case "NOT":
				not(operands);
				break;
			case "XOR":
				xor(operands);
				break;
				
//			instrukcija za transfer podataka
			case "MOV":
				mov(operands);
				break;
				
//			instrukcija za uslovno i bezuslovno grananje
			case "JMP":
				jmp(operands);
				break;
			case "JE":
				je(operands);
				break;
			case "JNE":
				jne(operands);
				break;
			case "JGE":
				jge(operands);
				break;
			case "JL":
				jl(operands);
				break;
			case "CMP":
				cmp(operands);
				break;
				
//			I/O rutine za učitavanje znaka sa tastature u registar i ispis znaka iz registra na ekran
			case "IN":
				in(operands);
				break;
			case "OUT":
				out(operands);
				break;

//			instrukcija za zaustavljanje rada procesora
			case "HALT":
				this.halt = true; 	// flag da se zaustavi izvrsavanje
				break;
				
			default:
				throw new UnsupportedOperationException("Unsupported operation: " + instructionRegister.getOpcode());
		}
		printRegisters();
	}
	
	protected void add(List<Operand> operands) {
		//(primjer: ADD R0, R1, R2  ->  R0 = R1 + R2)
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija ADD zahtijeva tri operanda.");
		}
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
		
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije ADD mora biti registar.");
		}
			
		registers.put(dest.getValue(), valueSrc1 + valueSrc2);
	}
	
	protected void sub(List<Operand> operands) {
		// (primjer: SUB R0, R1, R2  ->   R0 = R1 - R2)
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija SUB zahtijeva tri operanda.");
		}
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
		
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije SUB mora biti registar.");
		}
		
		registers.put(dest.getValue(), valueSrc1 - valueSrc2);
	}
	
	protected void mul(List<Operand> operands) {
		// MUL R0, R1, R2  ->   R0 = R1 * R2
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija MUL zahtijeva tri operanda.");
		}
		
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
	
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije MUL mora biti registar.");
		}
			
		registers.put(dest.getValue(), valueSrc1 * valueSrc2);
	}
	
	protected void div(List<Operand> operands) {
		// MUL R0, R1, R2  ->   R0 = R1 / R2
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija DIV zahtijeva tri operanda.");
		}

		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
			
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
//		System.out.println("v1 = " + valueSrc1 + ", v2 = " + valueSrc2);
		if(valueSrc2 == 0) {
			throw new ArithmeticException("Division by zero!");				
		}
		
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije DIV mora biti registar.");
		}
				
		registers.put(dest.getValue(), valueSrc1 / valueSrc2);
	}
	
	protected void and(List<Operand> operands) {
		// AND R0, R1, R2  ->   R0 = R1 & R2
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija AND zahtijeva tri operanda.");
		}
		
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
	
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije AND mora biti registar.");
		}
			
		registers.put(dest.getValue(), valueSrc1 & valueSrc2);
	}
	
	protected void or(List<Operand> operands) {
		// OR R0, R1, R2  ->   R0 = R1 | R2
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija OR zahtijeva tri operanda.");
		}
		
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
	
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije OR mora biti registar.");
		}
			
		registers.put(dest.getValue(), valueSrc1 | valueSrc2);
	}
	
	protected void not(List<Operand> operands) {
		// NOT R0, R1  ->  R0 = ~R1
		if(operands.size() != 2) {
			throw new IllegalArgumentException("GRESKA! - operacija NOT zahtijeva dva operanda.");
		}
		
		Operand dest = operands.get(0);
		Operand src = operands.get(1);
	
		long valueSrc = getOperandValue(src);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije NOT mora biti registar.");
		}
			
		registers.put(dest.getValue(), ~valueSrc);
	}
	
	protected void xor(List<Operand> operands) {
		//XOR R0, R1, R2  ->   R0 = R1 ^ R2
		if(operands.size() != 3) {
			throw new IllegalArgumentException("GRESKA! - operacija XOR zahtijeva tri operanda.");
		}
		
		Operand dest = operands.get(0);
		Operand src1 = operands.get(1);
		Operand src2 = operands.get(2);
	
		long valueSrc1 = getOperandValue(src1);
		long valueSrc2 = getOperandValue(src2);
			
		if (dest.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("Destinacija u kojoj se cuva vrijednost operacije XOR mora biti registar.");
		}
			
		registers.put(dest.getValue(), valueSrc1 ^ valueSrc2);
	}
	
	protected void mov(List<Operand> operands) {
		// MOV R1, [1000]  ->   ucitati podatke iz dataMemory u registar R1
		if(operands.size() != 2) {
			throw new IllegalArgumentException("GRESKA! - operacija MOV zahtijeva dva operanda.");
		}
	    Operand dest = operands.get(0);
	    Operand src = operands.get(1);
	    
	    if(dest.getAddressingType() == AddressingType.REGISTER) {
	    	long value = getOperandValue(src);
	 	    registers.put(dest.getValue(), value);
	    }
	    
	    else if (dest.getAddressingType() == AddressingType.DIRECT) {
	    	long value = getOperandValue(src);
	    	long address = Long.parseLong(dest.getValue());
	    	store(address, value);
	    }
	    else if (dest.getAddressingType() == AddressingType.INDIRECT) {
	    	long value = getOperandValue(src);
	    	long address = dataMemory.read(Long.parseLong(dest.getValue()));
	    	store(address, value);
	    }
	    else {
	    	throw new IllegalArgumentException("GRESKA! - nedozvoljeno adresiranje.");
	    }
	}
	
	// Jump
	protected void jmp(List<Operand> operands) {	
		if(operands.size() != 1) {
			throw new IllegalArgumentException("GRESKA! - operacija JMP zahtijeva jedan operand.");
		}
		Operand operand = operands.get(0);
		long newPCValue = getOperandValue(operand);
		if(newPCValue >= 0 && newPCValue < instructionMemory.getMemorySize()) {
			this.pc = newPCValue;			
		}
		else {
			throw new IllegalArgumentException("GRESKA! - nije moguce izvrsiti JMP metod.");
		}
	}
	
	// Jump if Equal
	protected void je(List<Operand> operands) {
		if(equalsFlagRegister == true)
			jmp(operands);
	}

	// Jump if Not Equal
	protected void jne(List<Operand> operands) {
		if(equalsFlagRegister == false)
			jmp(operands);
	}
	
	// Jump if Greater or Equal
	protected void jge(List<Operand> operands) {
		if(equalsFlagRegister == true || greaterFlagRegister == true)
			jmp(operands);
	}
	
	// Jump if Less
	protected void jl(List<Operand> operands) {
		if(lesserFlagRegister == true)
			jmp(operands);
	}
	
	protected void cmp(List<Operand> operands) {
		if(operands.size() != 2) {
			throw new IllegalArgumentException("GRESKA! - operacija CMP zahtijeva dva operanda.");
		}
		long value1 = getOperandValue(operands.get(0));
		long value2 = getOperandValue(operands.get(1));
		
//		System.out.println("CMP: value1 = " + value1 + " & value2 = " + value2);
		if(value1 == value2) {
			this.equalsFlagRegister = true;
			this.greaterFlagRegister = false;
			this.lesserFlagRegister = false;
		}
		else if(value1 > value2) {
			this.equalsFlagRegister = false;
			this.greaterFlagRegister = true;
			this.lesserFlagRegister = false;
		}
		else if(value1 < value2) {
			this.equalsFlagRegister = false;
			this.greaterFlagRegister = false;
			this.lesserFlagRegister = true;
		}
//		System.out.println("equals: " + equalsFlagRegister);
//		System.out.println("greater: " + greaterFlagRegister);
//		System.out.println("lesser: " + lesserFlagRegister);
	}
	
	protected void in(List<Operand> operands) {
		if(operands.size() != 1) {
			throw new IllegalArgumentException("GRESKA! - operacija IN zahtijeva jedan operand.");
		}
		Operand operand = operands.get(0);
		if(operand.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("GRESKA! - operand mora biti registar.");
		}
		char inputChar = scanner.next().charAt(0); 
		registers.put(operand.getValue(), (long) inputChar);
	}
	
	protected void out(List<Operand> operands) {
		if(operands.size() != 1) {
			throw new IllegalArgumentException("GRESKA! - operacija OUT zahtijeva jedan operand.");
		}
		Operand operand = operands.get(0);
		if(operand.getAddressingType() != AddressingType.REGISTER) {
			throw new IllegalArgumentException("GRESKA! - operand mora biti registar.");
		}
		long value = getRegisterValue(operand.getValue());
	    char outputChar = (char) value;
//	    System.out.print(outputChar);
		System.out.println("Registar " + operand.getValue() + ": \'" + outputChar + "\'");
	}
	
	protected void store(long address, long value) {
		dataMemory.write(address, (byte) value); 
	}
	
	protected long getOperandValue(Operand operand) {
		switch (operand.getAddressingType()) {
			case REGISTER:
				String reg = operand.getValue();
				return registers.getOrDefault(reg, 0L);
			case DIRECT:
				long address = 0;
				if(operand.getValue().matches("R[0-3]")) {		// direktno adresiranje preko registra - npr.[R1]
					address = registers.get(operand.getValue());
				}
				else {
					address = Long.parseLong(operand.getValue());
				}
				return dataMemory.read(address);
			case INDIRECT:
				long indirectAddress = 0;
				if(operand.getValue().matches("R[0-3]")) {		// indirektno adresiranje preko registra - npr.(R1)
					indirectAddress = registers.get(operand.getValue());
				}
				else {
					long addressValue = Long.parseLong(operand.getValue());
					indirectAddress = dataMemory.read(addressValue);
				}
				return dataMemory.read(indirectAddress);
			case IMMEDIATE:
				return Long.parseLong(operand.getValue()); 
			default:
				throw new IllegalArgumentException("Nepoznat tip adresiranja za operand: " + operand.getAddressingType());
	    }
	}
	
	protected void printRegisters() {
		System.out.println("R0: " + registers.get("R0"));
		System.out.println("R1: " + registers.get("R1"));
		System.out.println("R2: " + registers.get("R2"));
		System.out.println("R3: " + registers.get("R3"));
	}
	
	protected long getRegisterValue(String register) {
		return registers.get(register);
	}
	
	protected long getPc() {
		return this.pc;
	}
	
	protected boolean isHalt() {
		return halt;
	}
	protected void setHalt(boolean halt) {
		this.halt = halt;
	}
}
