package src.com.utsa;

import java.util.ArrayList;

public class MachineCodes {
	ArrayList<String> instruction = new ArrayList<String>();
	ArrayList<String> input1 = new ArrayList<String>();
	ArrayList<String> input2 = new ArrayList<String>();
	ArrayList<String> input3 = new ArrayList<String>();

	public ArrayList<String> getInstruction() {
		return instruction;
	}

	public ArrayList<String> getInput1() {
		return input1;
	}

	public ArrayList<String> getInput2() {
		return input2;
	}

	public ArrayList<String> getInput3() {
		return input3;
	}
	
	public void createiMemory(int progNumber){
		switch(progNumber){
			case 1:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");		
			instruction.add("ST");
			instruction.add("MUL");
			instruction.add("ST");
			instruction.add("SUBI");
			instruction.add("SUBI");
			instruction.add("SUBI");
			instruction.add("BNEQZ");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("LD");
			instruction.add("LD");
			instruction.add("ADD");
			instruction.add("ADDI");
			instruction.add("DIV");
			instruction.add("ST");
			instruction.add("SUBI");
			instruction.add("SUBI");
			instruction.add("SUBI");
			instruction.add("SUBI");
			instruction.add("BNEQZ");
			instruction.add("HLT");
	
			input1.add("R1");
			input1.add("R5");
			input1.add("R2");
			input1.add("R3");
			input1.add("R1");
			input1.add("R4");
			input1.add("R4");
			input1.add("R2");
			input1.add("R3");
			input1.add("R1");
			input1.add("R1");
			input1.add("R1");
			input1.add("R2");
			input1.add("R3");
			input1.add("R4");
			input1.add("R5");
			input1.add("R6");		
			input1.add("R5");
			input1.add("R7");
			input1.add("R5");
			input1.add("R5");
			input1.add("R2");
			input1.add("R3");
			input1.add("R4");
			input1.add("R1");
			input1.add("R1");
			input1.add("RR");
	
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R2");
			input2.add("R1");
			input2.add("R3");
			input2.add("R2");
			input2.add("R3");
			input2.add("R1");
			input2.add("-7");
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R2");
			input2.add("R3");		
			input2.add("R5");
			input2.add("R0");
			input2.add("R5");
			input2.add("R4");
			input2.add("R2");
			input2.add("R3");
			input2.add("R4");
			input2.add("R1");
			input2.add("-11");
			input2.add("RR");
	
			input3.add("10");
			input3.add("2");
			input3.add("196");
			input3.add("396");
			input3.add("0");
			input3.add("R5");
			input3.add("0");
			input3.add("4");
			input3.add("4");
			input3.add("1");
			input3.add("RR");
			input3.add("10");
			input3.add("196");
			input3.add("396");
			input3.add("596");
			input3.add("0");
			input3.add("0");		
			input3.add("R6");
			input3.add("4");
			input3.add("R7");
			input3.add("0");
			input3.add("4");
			input3.add("4");
			input3.add("4");
			input3.add("1");
			input3.add("RR");
			input3.add("RR");
			break;
		case 2:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("SUBI");
			instruction.add("BEQZ");		
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("SUB");
			instruction.add("SUBI");
			instruction.add("BEQZ");
			instruction.add("MUL");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("ST");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("HLT");
	
			input1.add("R7");
			input1.add("R1");
			input1.add("R2");
			input1.add("R2");
			input1.add("R3");
			input1.add("R4");
			input1.add("R2");
			input1.add("R2");
			input1.add("R2");
			input1.add("R3");
			input1.add("R4");
			input1.add("R0");
			input1.add("R3");
			input1.add("R7");
			input1.add("R1");
			input1.add("R0");
			input1.add("RR");
	
			input2.add("R0");
			input2.add("R0");
			input2.add("R1");
			input2.add("12");
			input2.add("R0");
			input2.add("R4");
			input2.add("R4");
			input2.add("R2");
			input2.add("3");
			input2.add("R3");
			input2.add("R4");
			input2.add("-6");
			input2.add("R7");
			input2.add("R7");
			input2.add("R1");
			input2.add("-14");
			input2.add("RR");		
	
			input3.add("0");
			input3.add("1");
			input3.add("10");
			input3.add("RR");
			input3.add("1");
			input3.add("1");
			input3.add("R1");
			input3.add("1");
			input3.add("RR");
			input3.add("R4");
			input3.add("1");
			input3.add("RR");
			input3.add("0");
			input3.add("4");
			input3.add("1");
			input3.add("RR");
			input3.add("RR");
			break;
		case 3:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ST");
			instruction.add("HLT");		
	
			input1.add("R1");
			input1.add("R1");
			input1.add("R1");
			input1.add("R1");
			input1.add("RR");
			
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R1");
			input2.add("RR");
	
			input3.add("10");
			input3.add("8");
			input3.add("16");
			input3.add("4");
			input3.add("RR");
			break;
		case 4:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("SUBI");
			instruction.add("BEQZ");		
			instruction.add("ADDI");
			instruction.add("SUBI");
			instruction.add("BEQZ");
			instruction.add("ADDI");
			instruction.add("SUBI");
			instruction.add("BEQZ");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("HLT");
	
			input1.add("R4");
			input1.add("R1");
			input1.add("R5");
			input1.add("R5");
			input1.add("R2");
			input1.add("R5");
			input1.add("R5");
			input1.add("R3");
			input1.add("R5");
			input1.add("R5");
			input1.add("R4");
			input1.add("R3");
			input1.add("R0");
			input1.add("R2");
			input1.add("R0");
			input1.add("R1");
			input1.add("R0");
			input1.add("RR");
	
			input2.add("R0");
			input2.add("R0");
			input2.add("R1");
			input2.add("13");
			input2.add("R0");
			input2.add("R2");
			input2.add("8");
			input2.add("R0");
			input2.add("R3");
			input2.add("3");
			input2.add("R4");
			input2.add("R3");
			input2.add("-5");
			input2.add("R2");
			input2.add("-10");
			input2.add("R1");
			input2.add("-15");		
			input2.add("RR");		
	
			input3.add("0");
			input3.add("0");
			input3.add("10");
			input3.add("RR");
			input3.add("0");
			input3.add("10");
			input3.add("RR");
			input3.add("0");
			input3.add("5");
			input3.add("RR");
			input3.add("1");
			input3.add("1");
			input3.add("RR");
			input3.add("1");
			input3.add("RR");
			input3.add("1");
			input3.add("RR");
			input3.add("RR");		
			break;
		case 5:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");		
			instruction.add("ADDI");
			instruction.add("SUBI");
			instruction.add("BEQZ");
			instruction.add("AND");
			instruction.add("BEQZ");
			instruction.add("ADDI");
			instruction.add("MUL");
			instruction.add("ADDI");
			instruction.add("BEQZ");
			instruction.add("HLT");
	
			input1.add("R1");
			input1.add("R2");
			input1.add("R3");
			input1.add("R7");
			input1.add("R4");
			input1.add("R5");
			input1.add("R5");
			input1.add("R6");
			input1.add("R6");
			input1.add("R1");
			input1.add("R3");
			input1.add("R4");
			input1.add("R0");
			input1.add("RR");
	
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R4");
			input2.add("6");
			input2.add("R2");
			input2.add("1");
			input2.add("R1");
			input2.add("R3");
			input2.add("R4");
			input2.add("-8");
			input2.add("RR");
	
			input3.add("0");
			input3.add("1023");
			input3.add("1");
			input3.add("2");
			input3.add("0");
			input3.add("32");
			input3.add("RR");
			input3.add("R3");
			input3.add("RR");
			input3.add("1");
			input3.add("R7");
			input3.add("1");
			input3.add("RR");
			input3.add("RR");
			break;
		case 6:
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADDI");
			instruction.add("ADD");
			instruction.add("ADD");
			instruction.add("ADD");
			instruction.add("SUBI");
			instruction.add("BNEQZ");
			instruction.add("ST");
			instruction.add("HLT");
	
			input1.add("R1");
			input1.add("R2");
			input1.add("R5");
			input1.add("R3");
			input1.add("R1");
			input1.add("R2");
			input1.add("R5");
			input1.add("R5");
			input1.add("R3");
			input1.add("RR");
			
			input2.add("R0");
			input2.add("R0");
			input2.add("R0");
			input2.add("R1");
			input2.add("R2");
			input2.add("R3");
			input2.add("R5");
			input2.add("-5");
			input2.add("R3");
			input2.add("RR");
	
			input3.add("0");
			input3.add("1");
			input3.add("10");
			input3.add("R2");
			input3.add("R0");
			input3.add("R0");
			input3.add("1");
			input3.add("RR");
			input3.add("0");
			input3.add("RR");
				break;
		}
	}

}
