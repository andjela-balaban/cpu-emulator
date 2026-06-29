package org.unibl.etf.processor;

import org.unibl.etf.loader.*;
import java.nio.file.*;
import java.util.*;
import org.unibl.etf.instruction.*;

public class Main {
	public static void main(String srgs[]) {
//		FileLoader loader = new FileLoader(Paths.get(".","InstructionFile.txt"));
//		FileLoader loader = new FileLoader(Paths.get("InstructionFile.txt"));
		Scanner in = new Scanner(System.in);
		System.out.print("Unesite putanju do fajla sa instrukcijama: ");
		String path = in.nextLine();
		FileLoader loader = new FileLoader(Paths.get(path));
		List<Instruction> instructions = loader.loadInstructions();
		for(Instruction i : instructions) {
			System.out.println(i);
		}
		System.out.println("---------------------");
		Processor processor = new Processor();
		processor.loadProgram(instructions);
		processor.start();
//		System.out.println("Velicina memorije za instrukcije je " + instructionMemory.getMemorySize());
	}
}
