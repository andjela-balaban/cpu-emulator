package org.unibl.etf.processor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.unibl.etf.instruction.*;
import org.unibl.etf.loader.FileLoader;

import java.nio.file.Paths;
import java.util.*;

public class ProcessorTest {
    private Processor processor;

    @BeforeEach
    public void setUp() {
        processor = new Processor();
        FileLoader loader = new FileLoader(Paths.get("testFile.txt"));
		List<Instruction> instructions = loader.loadInstructions();
        processor.loadProgram(instructions);
    }

    // testovi za 'MOV' operaciju
    @Test
    public void testMoveImmediateToRegister() {
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("20", AddressingType.IMMEDIATE)	//#20 
        ));
        assertEquals(20, processor.getRegisterValue("R1"));
    }

    @Test
    public void testMoveDirectToRegister() {
        processor.mov(List.of(
        	new Operand("100", AddressingType.DIRECT),	// [100]
            new Operand("50", AddressingType.IMMEDIATE) // #50
        ));
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("100", AddressingType.DIRECT)	// [100] 
        ));
        assertEquals(50, processor.getRegisterValue("R1"));
    }
    
    @Test
    public void testMoveRegisterToDirect() {
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),	// [100]
            new Operand("50", AddressingType.IMMEDIATE) // #50
        ));
        processor.mov(List.of(
            new Operand("100", AddressingType.DIRECT),	// [100] 
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(50, processor.getRegisterValue("R1"));
    }

    @Test
    public void testMoveRegisterToDirectMemory() {
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("20", AddressingType.IMMEDIATE) // #20
        ));
        processor.mov(List.of(
        	new Operand("5", AddressingType.DIRECT),	// [5]
            new Operand("R1", AddressingType.REGISTER) 
        ));
        assertEquals(20, processor.getOperandValue(new Operand("5", AddressingType.DIRECT)));
    }
    
    @Test
    public void testMoveDirectToDirectMemory() {
        processor.mov(List.of(
        	new Operand("10", AddressingType.DIRECT),
            new Operand("20", AddressingType.IMMEDIATE) // #20
        ));
        processor.mov(List.of(
        	new Operand("5", AddressingType.DIRECT),	// [5]
            new Operand("10", AddressingType.DIRECT) 
        ));
        assertEquals(20, processor.getOperandValue(new Operand("5", AddressingType.DIRECT)));
    }

    @Test
    public void testMoveIndirectToRegister() {
        processor.mov(List.of(
        	new Operand("10", AddressingType.DIRECT),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),	//[20]
            new Operand("10", AddressingType.IMMEDIATE)  // #10
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("20", AddressingType.INDIRECT) // (20) 
        ));
        assertEquals(30, processor.getRegisterValue("R1"));
    }
    
    @Test
    public void testMoveIndirectToDirectMemory() {
        processor.mov(List.of(
        	new Operand("10", AddressingType.DIRECT),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),	//[20]
            new Operand("10", AddressingType.IMMEDIATE)  // #10
        ));
        processor.mov(List.of(
            new Operand("5", AddressingType.DIRECT),
            new Operand("20", AddressingType.INDIRECT) // (20) 
        ));
        assertEquals(30, processor.getOperandValue(new Operand("5", AddressingType.DIRECT)));
    }
    
    @Test
    public void testMoveDirectToIndirectMemory() {
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),	//[20]
            new Operand("10", AddressingType.IMMEDIATE)  // #10
        ));
        processor.mov(List.of(
            new Operand("5", AddressingType.INDIRECT),
            new Operand("20", AddressingType.DIRECT) // (20) 
        ));
        assertEquals(10, processor.getOperandValue(new Operand("5", AddressingType.INDIRECT)));
    }
    
    @Test
    public void testMoveIndirectToIndirectMemory() {
    	processor.mov(List.of(
    		new Operand("10", AddressingType.DIRECT),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
    	processor.mov(List.of(
    		new Operand("20", AddressingType.DIRECT),	//[20]
    		new Operand("10", AddressingType.IMMEDIATE)  // #10
        ));
        
        processor.mov(List.of(
            new Operand("5", AddressingType.INDIRECT),
            new Operand("20", AddressingType.INDIRECT) // (20) 
        ));
        assertEquals(30, processor.getOperandValue(new Operand("5", AddressingType.INDIRECT)));
    }
    
    @Test
    public void testMoveImmediateToIndirectMemory() {
        processor.mov(List.of(
            new Operand("5", AddressingType.INDIRECT),
            new Operand("20", AddressingType.IMMEDIATE) // (20) 
        ));
        assertEquals(20, processor.getOperandValue(new Operand("5", AddressingType.INDIRECT)));
    }
    
    @Test
    public void testMoveRegisterToIndirect() {
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
        
        processor.mov(List.of(
            new Operand("20", AddressingType.INDIRECT), // (20) 
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(30, processor.getOperandValue(new Operand("20", AddressingType.INDIRECT)));
    }
    
    @Test
    public void testMoveRegisterToRegister() {
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
        
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER), // (20) 
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(30, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testMoveExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.mov(List.of(
                new Operand("R1", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija MOV zahtijeva dva operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testMoveExceptionAddressing() {        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        	processor.mov(List.of(
        		new Operand("5", AddressingType.IMMEDIATE), // #5 
        		new Operand("R1", AddressingType.REGISTER)
        	));
        });

        String expectedMessage = "GRESKA! - nedozvoljeno adresiranje.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    
   
//    TESTOVI ZA ARITMETICKE OPERACIJE
    
    // sabiranje
    @Test
    public void testAddOperationRegisters() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));

        processor.add(List.of(
        	new Operand("R3", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(15, processor.getRegisterValue("R3"));
    }
    
    @Test
    public void testAddOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT), 	// [10]
            new Operand("5", AddressingType.IMMEDIATE) // #5
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));

        processor.add(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("10", AddressingType.DIRECT)	// [10]
        ));
        assertEquals(15, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testAddOperationIndirect() {
    	processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),	// [10]
            new Operand("30", AddressingType.IMMEDIATE)  // #30
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),	// [20]
            new Operand("10", AddressingType.IMMEDIATE) 	// #10
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10
        ));

        processor.add(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("20", AddressingType.INDIRECT) // (10)
        ));
        assertEquals(40, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testAddExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.add(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija ADD zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testAddExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.add(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije ADD mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    

    // oduzimanje
    @Test
    public void testSubtractOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),      
            new Operand("15", AddressingType.IMMEDIATE) // #15 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE)  // #5
        ));

        processor.sub(List.of(
        	new Operand("R3", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(10, processor.getRegisterValue("R3"));
    }
    
    @Test
    public void testSubOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT), // [10]
            new Operand("5", AddressingType.IMMEDIATE) // #5
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE)  // #10 
        ));

        processor.sub(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("10", AddressingType.DIRECT)  // [10]
        ));
        assertEquals(5, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testSubOperationIndirect() {
    	processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT), 	// [10]
            new Operand("30", AddressingType.IMMEDIATE) 	// #30
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),	// [20]
            new Operand("10", AddressingType.IMMEDIATE)	// #10 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE)  // #10 
        ));

        processor.sub(List.of(
        	new Operand("R2", AddressingType.REGISTER),
        	new Operand("20", AddressingType.INDIRECT), 	// (20)
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(20, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testSubExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.sub(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija SUB zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testSubExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.sub(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije SUB mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    
    // mnozenje
    @Test
    public void testMultiplyOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("3", AddressingType.IMMEDIATE) // #3 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("4", AddressingType.IMMEDIATE) // #4 
        ));

        processor.mul(List.of(
        	new Operand("R3", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(12, processor.getRegisterValue("R3"));
    }
    
    @Test
    public void testMultiplyOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT), // [10]
            new Operand("5", AddressingType.IMMEDIATE) // #5
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE)	// #10 
        ));

        processor.mul(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("10", AddressingType.DIRECT)	// [10]
        ));
        assertEquals(50, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testMultiplyOperationIndirect() {
    	processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("30", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.mul(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("20", AddressingType.INDIRECT)
        ));
        assertEquals(300, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testMultiplyExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.mul(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija MUL zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testMultiplyExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.mul(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije MUL mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // dijeljenje
    @Test
    public void testDivideOperationRegister() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("20", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) 
        ));

        processor.div(List.of(
        	new Operand("R3", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(4, processor.getRegisterValue("R3"));
    }
    
    @Test
    public void testDivideOperationDirect() {
        processor.mov(List.of(
        	new Operand("10", AddressingType.DIRECT),
        	new Operand("5", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.div(List.of(
        	new Operand("R2", AddressingType.REGISTER),
        	new Operand("R1", AddressingType.REGISTER), 
        	new Operand("10", AddressingType.DIRECT)
        ));
        assertEquals(2, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testDivideOperationIndirect() {
    	processor.mov(List.of(
    		new Operand("10", AddressingType.DIRECT),
    		new Operand("30", AddressingType.IMMEDIATE) 
    	));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
        	new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.div(List.of(
        	new Operand("R2", AddressingType.REGISTER),
        	new Operand("20", AddressingType.INDIRECT),
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(3, processor.getRegisterValue("R2"));
    }
    
    @Test
    public void testDivideExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.div(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija DIV zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testDiviseExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.div(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije DIV mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDivideByZero() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("0", AddressingType.IMMEDIATE) 
        ));

        Exception exception = assertThrows(ArithmeticException.class, () -> {
            processor.div(List.of(
            	new Operand("R3", AddressingType.REGISTER),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Division by zero!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    
//    TESTOVI ZA LOGICKE OPERACIJE
    
    // logicko 'I'
    @Test
    public void testAndOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        )); 
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        )); 

        processor.and(List.of(
        	new Operand("R3", AddressingType.REGISTER), 
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(8, processor.getRegisterValue("R3")); // 8 = 1000
    }
    
    @Test
    public void testAndOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        ));

        processor.and(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("10", AddressingType.DIRECT)
        ));
        assertEquals(8, processor.getRegisterValue("R2"));	// 8 = 1000
    }
    
    @Test
    public void testAndOperationIndirect() {
    	processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        ));

        processor.and(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("20", AddressingType.INDIRECT)
        ));
        assertEquals(8, processor.getRegisterValue("R2"));	// 8 = 1000 
    }
    
    @Test
    public void testAndExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.and(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija AND zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testAndExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.and(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije AND mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // logicko 'ILI'
    @Test
    public void testOrOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("12", AddressingType.IMMEDIATE) 	// 12 = 1100 
        )); 
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE)	// 10 = 1010
        )); 

        processor.or(List.of(
        	new Operand("R3", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(14, processor.getRegisterValue("R3")); // 14 = 1110
    }
    
    @Test
    public void testOrOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        ));

        processor.or(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("10", AddressingType.DIRECT)
        ));
        assertEquals(14, processor.getRegisterValue("R2"));	// 14 = 1110
    }
    
    @Test
    public void testOrOperationIndirect() {
    	processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        ));

        processor.or(List.of(
        	new Operand("R2", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("20", AddressingType.INDIRECT)
        ));
        assertEquals(14, processor.getRegisterValue("R2"));	// 14 = 1110
    }
    
    @Test
    public void testOrExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.or(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija OR zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testOrExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.or(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije OR mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // logicko 'NE'
    @Test
    public void testNotOperationRegister() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("15", AddressingType.IMMEDIATE) // 15 = 1111
        )); 

        processor.not(List.of(
        	new Operand("R0", AddressingType.REGISTER),
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(~15L, processor.getRegisterValue("R0")); 
    }
    
    @Test
    public void testNotOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("15", AddressingType.IMMEDIATE) // 15 = 1111
        )); 

        processor.not(List.of(
        	new Operand("R0", AddressingType.REGISTER),
        	new Operand("10", AddressingType.DIRECT)
        ));
        assertEquals(~15L, processor.getRegisterValue("R0")); 
    }
    
    @Test
    public void testNotOperationIndirect() {
    	
    	processor.mov(List.of(
    		new Operand("10", AddressingType.DIRECT),
    		new Operand("15", AddressingType.IMMEDIATE) // 15 = 1111
    	));
    	processor.mov(List.of(
    		new Operand("20", AddressingType.DIRECT),
    		new Operand("10", AddressingType.IMMEDIATE) 
       ));
      
        processor.not(List.of(
        	new Operand("R0", AddressingType.REGISTER),
        	new Operand("20", AddressingType.INDIRECT)
        ));
        assertEquals(~15L, processor.getRegisterValue("R0")); 
    }
    
    @Test
    public void testNotExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.not(List.of(
                new Operand("R1", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija NOT zahtijeva dva operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testNotExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.not(List.of(
            	new Operand("10", AddressingType.DIRECT), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije NOT mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // logicki 'XOR'
    @Test
    public void testXorOperationRegister() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100
        )); 
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),	
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        )); 

        processor.xor(List.of(
        	new Operand("R3", AddressingType.REGISTER), 
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        assertEquals(6, processor.getRegisterValue("R3")); 	// 6 = 0110
    }
    
    @Test
    public void testXorOperationDirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100
        )); 
        processor.mov(List.of(
        	new Operand("R2", AddressingType.REGISTER),	
            new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
        )); 

        processor.xor(List.of(
        	new Operand("R3", AddressingType.REGISTER),
        	new Operand("R2", AddressingType.REGISTER),
        	new Operand("10", AddressingType.DIRECT)
        ));
        assertEquals(6, processor.getRegisterValue("R3")); 	// 6 = 0110 
    }
    
    @Test
    public void testXorOperationIndirect() {
    	processor.mov(List.of(
           new Operand("10", AddressingType.DIRECT),
           new Operand("12", AddressingType.IMMEDIATE) // 12 = 1100 
        ));
    	processor.mov(List.of(
    	  new Operand("20", AddressingType.DIRECT),
    	  new Operand("10", AddressingType.IMMEDIATE) 
    	));
    	processor.mov(List.of(
    	   new Operand("R1", AddressingType.REGISTER),	
           new Operand("10", AddressingType.IMMEDIATE) // 10 = 1010 
    	)); 

    	processor.xor(List.of(
    	   new Operand("R3", AddressingType.REGISTER),
           new Operand("R1", AddressingType.REGISTER),
           new Operand("20", AddressingType.INDIRECT)
    	));
    	assertEquals(6, processor.getRegisterValue("R3")); 	// 6 = 0110 
    }
    
    @Test
    public void testXorExceptionSize() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.xor(List.of(
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija XOR zahtijeva tri operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testXorExceptionType() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) // #5 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) // #10 
        ));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.xor(List.of(
            	new Operand("10", AddressingType.DIRECT),
                new Operand("R1", AddressingType.REGISTER), 
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "Destinacija u kojoj se cuva vrijednost operacije XOR mora biti registar.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

  
  
//    TESTOVI ZA 'JUMP' OPERACIJE
    @Test
    public void testJumpOperationImmediate() {
        processor.jmp(List.of(
            new Operand("5", AddressingType.IMMEDIATE)
        ));
        assertEquals(5, processor.getPc());
    }

    @Test
    public void testJumpOperationRegister() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("2", AddressingType.IMMEDIATE) 
        ));
        processor.jmp(List.of(
            new Operand("R1", AddressingType.REGISTER)
        ));
        assertEquals(2, processor.getPc());
    }

    @Test
    public void testJumpOperationDirect() {
        processor.mov(List.of(
            new Operand("5", AddressingType.DIRECT),
            new Operand("2", AddressingType.IMMEDIATE) 
        ));
        processor.jmp(List.of(
            new Operand("5", AddressingType.DIRECT)
        ));
        assertEquals(2, processor.getPc());
    }

    @Test
    public void testJumpOperationIndirect() {
        processor.mov(List.of(
            new Operand("10", AddressingType.DIRECT),
            new Operand("3", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("20", AddressingType.DIRECT),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.jmp(List.of(
            new Operand("20", AddressingType.INDIRECT)
        ));
        assertEquals(3, processor.getPc());
    }

    @Test
    public void testJumpExceptionSize() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.jmp(List.of(
                new Operand("R1", AddressingType.REGISTER),
                new Operand("R2", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija JMP zahtijeva jedan operand.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    public void testJumpExceptionNotPossibleToJump() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.jmp(List.of(
            	new Operand("-1", AddressingType.IMMEDIATE)
            ));
        });

        String expectedMessage = "GRESKA! - nije moguce izvrsiti JMP metod.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    
    
//    testovi za 'CMP' operacije i uslovno grananje
    @Test
    public void testCmpAndJeOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.cmp(List.of(
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        processor.je(List.of(
            new Operand("5", AddressingType.IMMEDIATE)
        ));
        assertEquals(5, processor.getPc());
    }

    @Test
    public void testCmpAndJneOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) 
        ));

        processor.cmp(List.of(
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        processor.jne(List.of(
            new Operand("5", AddressingType.IMMEDIATE)
        ));
        assertEquals(5, processor.getPc());
    }

    @Test
    public void testCmpAndJgeOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.cmp(List.of(
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        processor.jge(List.of(
            new Operand("5", AddressingType.IMMEDIATE)
        ));
        assertEquals(5, processor.getPc());
    }

    @Test
    public void testCmpAndJlOperation() {
        processor.mov(List.of(
            new Operand("R1", AddressingType.REGISTER),
            new Operand("5", AddressingType.IMMEDIATE) 
        ));
        processor.mov(List.of(
            new Operand("R2", AddressingType.REGISTER),
            new Operand("10", AddressingType.IMMEDIATE) 
        ));

        processor.cmp(List.of(
            new Operand("R1", AddressingType.REGISTER), 
            new Operand("R2", AddressingType.REGISTER)
        ));
        processor.jl(List.of(
            new Operand("5", AddressingType.IMMEDIATE)
        ));
        assertEquals(5, processor.getPc());
    }
    
    @Test
    public void testCmpExceptionSize() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            processor.cmp(List.of(
                new Operand("R1", AddressingType.REGISTER)
            ));
        });

        String expectedMessage = "GRESKA! - operacija CMP zahtijeva dva operanda.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    
    // test za 'HALT' operaciju
    @Test
    public void testHaltOperation() {
        processor.setHalt(true);
        assertTrue(processor.isHalt());
    }
}
