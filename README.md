# 🖥️ CPU Emulator - Computer Architecture

[![Java](https://img.shields.io/badge/Java-21%2B%20%2F%20OpenJDK-orange.svg)](https://www.oracle.com/java/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5-brightgreen.svg)](https://junit.org/junit5/)
[![Course](https://img.shields.io/badge/Course-Computer%20Architecture-blue.svg)](https://etf.unibl.org/)

A software emulator for a custom 64-bit processor architecture, developed in **Java** as part of the **Computer Architecture** course at the Faculty of Electrical Engineering, University of Banja Luka (ETF BL).

The emulator simulates the complete instruction processing cycle (**Fetch-Decode-Execute**), supporting general-purpose registers, arithmetic-logic operations, conditional and unconditional branching, I/O operations, multiple addressing modes, and a **paged memory model** for efficient host memory utilization across a 64-bit address space.

---

## ⚙️ Processor Specification

The programmer's model consists of:
- **General-Purpose Registers**: 4 64-bit registers: `R0`, `R1`, `R2`, and `R3`.
- **Program Counter (`PC`)**: 64-bit register storing the memory address of the next instruction to fetch.
- **Instruction Register (`IR`)**: Holds the currently decoded instruction during execution.
- **Address Space**: 64-bit guest virtual address space (each memory location is 1 byte wide).
- **Condition Flags**:
  - `equalsFlagRegister` – set if operands in `CMP` are equal.
  - `lesserFlagRegister` – set if the first operand is less than the second.
  - `greaterFlagRegister` – set if the first operand is greater than the second.
- **Control Cycle**:
  - `Fetch`: Fetches the instruction from instruction memory via `PC` and increments `PC`.
  - `Decode & Execute`: Parses operands, calculates effective addresses, and executes operations in the ALU / control unit.
  - Prints the state of all registers (`R0`-`R3`) after each executed step.

---

## 🎯 Addressing Modes

The emulator supports 4 addressing modes (`AddressingType`):

| Mode | Syntax | Description | Example |
| :--- | :--- | :--- | :--- |
| **Register (`REGISTER`)** | `Rx` | Value is read directly from or written to register `R0`-`R3`. | `MOV R1, R2` |
| **Immediate (`IMMEDIATE`)** | `#val` | Constant literal specified directly in the instruction. | `ADD R1, R2, #5` |
| **Direct (`DIRECT`)** | `[adr]` or `[Rx]` | Data memory address given as a constant or via a register. | `MOV R1, [1000]` / `MOV R1, [R2]` |
| **Indirect (`INDIRECT`)** | `(adr)` or `(Rx)` | Pointer addressing: memory location holds the address of the actual data. | `MOV (5), [5]` / `MUL R1, (5), R2` |

---

## 📋 Instruction Set

The processor supports a rich set of assembly instructions:

### 1. Arithmetic Operations
*All arithmetic instructions require three operands: destination (register) and two source operands.*
- `ADD dest, src1, src2` — Addition: `dest = src1 + src2`
- `SUB dest, src1, src2` — Subtraction: `dest = src1 - src2`
- `MUL dest, src1, src2` — Multiplication: `dest = src1 * src2`
- `DIV dest, src1, src2` — Integer division: `dest = src1 / src2` *(throws `ArithmeticException` on division by zero)*

### 2. Bitwise Logic Operations
- `AND dest, src1, src2` — Bitwise AND: `dest = src1 & src2`
- `OR dest, src1, src2` — Bitwise OR: `dest = src1 | src2`
- `XOR dest, src1, src2` — Bitwise XOR: `dest = src1 ^ src2`
- `NOT dest, src` — Bitwise NOT (complement): `dest = ~src`

### 3. Data Transfer
- `MOV dest, src` — Copies data from source to destination. Supports transfers between registers, immediate values, and direct/indirect memory addresses.

### 4. Comparison and Branching (Control Flow)
- `CMP op1, op2` — Compares two operands and updates internal flags (`equals`, `lesser`, `greater`).
- `JMP target` — Unconditional jump to the specified instruction line/address.
- `JE target` — Jump if equal (`value1 == value2`).
- `JNE target` — Jump if not equal (`value1 != value2`).
- `JL target` — Jump if less (`value1 < value2`).
- `JGE target` — Jump if greater or equal (`value1 >= value2`).

### 5. Input / Output (I/O) and Execution Control
- `IN Rx` — Reads a character from standard input (keyboard) and stores its ASCII value in the register.
- `OUT Rx` — Prints the character from the register to standard output (console).
- `HALT` — Halts the processor and terminates program execution.

---

## 🧠 Memory Architecture

According to the specification requirements, memory is modularized and optimized:

1. **Instruction Memory (`InstructionMemory`)**:
   - Stores loaded program instructions indexed by sequence number (address = `PC`).
2. **Data Memory (`IDataMemory`)**:
   - `DataMemory`: Basic `HashMap<Long, Byte>` implementation.
   - **`EfficientDataMemory` (Recommended implementation)**:
     - Implements a **paging** mechanism with page size `PAGE_SIZE = 8 bytes`.
     - Pages are dynamically allocated in host memory only upon the first write to a given address range (`sparse allocation`).
     - Enables access to any address across the entire 64-bit address space (`0` to `2^64 - 1`), with minimal physical host RAM overhead.

---

## 📂 Project Structure

```text
AR_emulator/
├── Zadatak/
│   └── AR 2023 Projektni zadatak.pdf    # Official project assignment and specification (Serbian)
├── src/
│   ├── module-info.java                 # Modular project definition
│   └── org/unibl/etf/
│       ├── instruction/
│       │   ├── AddressingType.java      # Enum for addressing modes (REGISTER, DIRECT, INDIRECT, IMMEDIATE)
│       │   ├── Instruction.java         # Instruction model (opcode + operand list)
│       │   └── Operand.java             # Operand model (value + addressing type)
│       ├── loader/
│       │   ├── Loader.java              # Interface for program loading
│       │   └── FileLoader.java          # Assembly text file parser
│       ├── memory/
│       │   ├── IInstructionMemory.java  # Interface for instruction memory
│       │   ├── InstructionMemory.java   # Instruction memory implementation
│       │   ├── IDataMemory.java         # Interface for data memory
│       │   ├── DataMemory.java          # Basic data memory implementation
│       │   └── EfficientDataMemory.java # Paged data memory (optimized RAM usage)
│       └── processor/
│           ├── IProcessor.java          # Processor interface (loadProgram, start, end)
│           ├── Processor.java           # Main emulator engine (Fetch-Decode-Execute, registers, ALU)
│           ├── Main.java                # Main CLI entry point
│           └── ProcessorTest.java       # Comprehensive JUnit 5 tests (over 50 test cases)
├── InstructionFile.txt                  # Example assembly program (arithmetic, memory, I/O)
├── InstructionFile2.txt                 # Example assembly program with branching (JMP)
├── testFile.txt                         # Test input file for unit tests
├── .gitignore                           # Git ignore rules for binaries and IDE configs
└── README.md                            # Project documentation
```

---

## 📝 Assembly Code Examples

Example program from [`InstructionFile.txt`](InstructionFile.txt):

```assembly
ADD R2, R1, #5      ; R2 = R1 + 5
MOV (5), [5]        ; Indirect move: store value from address 5 to the address pointed to by address 5
ADD R1, R2, #3      ; R1 = R2 + 3
MUL R3, R1, R2      ; R3 = R1 * R2
CMP R1, [R2]        ; Compare R1 with content of memory at address R2
SUB R0, R3, [1000]  ; R0 = R3 - memory[1000]
MOV [5], #25        ; memory[5] = 25
MOV [1000], R2      ; memory[1000] = R2
MUL R1, [4], #2     ; R1 = memory[4] * 2
DIV R1, [5], [1000] ; R1 = memory[5] / memory[1000]
ADD R1, R2, R3      ; R1 = R2 + R3
NOT R2, R1          ; R2 = ~R1
MOV R1, R2          ; R1 = R2
MUL R1, (5), R2     ; R1 = memory[memory[5]] * R2
OUT R3              ; Print ASCII character from register R3
IN R1               ; Read character from keyboard into R1
OUT R1              ; Print read character from R1
OUT R2              ; Print character from R2
HALT                ; Halt processor execution
```

---

## 🧪 Testing

The project includes an extensive suite of automated tests implemented using **JUnit 5** ([`ProcessorTest.java`](src/org/unibl/etf/processor/ProcessorTest.java)):

- **`MOV` Instruction Tests**: All transfer variants (immediate → register, direct → register, register → direct, indirect → register, direct → indirect, etc.), as well as validation of invalid addressing modes and operand counts.
- **Arithmetic Tests**: `ADD`, `SUB`, `MUL`, `DIV` with registers, direct, and indirect addressing, including division by zero tests (`ArithmeticException`).
- **Bitwise Logic Tests**: `AND`, `OR`, `XOR`, `NOT` with bit-level correctness verification.
- **Branching Tests**: `JMP`, `JE`, `JNE`, `JGE`, `JL` coupled with `CMP` instructions and verification of the program counter (`PC`) state.
- **Exception Tests**: Robustness testing for illegal operand counts or unsupported addressing modes.

---

## 🚀 Getting Started

### Requirements
- **Java SE Development Kit (JDK) 17+** (JDK 21 or newer recommended).

### Running from the Terminal

1. **Compile the project:**
   ```bash
   javac -d bin $(find src -name "*.java" -not -name "*Test.java" -not -name "module-info.java")
   ```

2. **Run the emulator:**
   ```bash
   java -cp bin org.unibl.etf.processor.Main
   ```

3. **Provide the path to the instruction file:**
   When prompted, enter the file name or relative path:
   ```text
   Unesite putanju do fajla sa instrukcijama: InstructionFile.txt
   ```

### Running in Eclipse / IntelliJ IDEA / VS Code
1. Open the project in your preferred IDE as a Java project (mark `src` as the source root).
2. Run `Main.java` (`Run As -> Java Application`).
3. To run the tests, right-click on `ProcessorTest.java` → `Run As -> JUnit Test`.
