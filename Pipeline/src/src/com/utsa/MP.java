package src.com.utsa;

import java.util.ArrayList;
import java.util.Scanner;

public class MP {
	ArrayList<String> instruction = new ArrayList<String>();
	ArrayList<String> input1 = new ArrayList<String>();
	ArrayList<String> input2 = new ArrayList<String>();
	ArrayList<String> input3 = new ArrayList<String>();

	boolean isDecodeInputReady = false;
	boolean isExInputReady = false;
	boolean isMemInputReady = false;
	boolean isWBInputReady = false;
	boolean ishalt = false;
	
	int isRegisterLocked[] = {0,0,0,0,0,0,0,0};
	int isRegisterNegative[] = {1,1,1,1,1,1,1,1};
	
	int programCounter = 0;
	int clockCycles =0;
	int fetchedInsCount = 0;
	int finishedInsCount = 0;
	
	int[] memory = new int[1024];

	Input inputToDecode = new Input();	
	Input inputToEx = new Input();
	Input inputToMem = new Input();
	Input inputToWr = new Input();

	int registers[] = {0,0,0,0,0,0,0,0};

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Program Number [1-6]");
		int progNumber = sc.nextInt();
		MP mp = new MP();
		mp.createiMemory(progNumber);
		mp.mainMethod();
	}
	
	public void mainMethod(){
		Boolean isContinue = false;
		int noOfStalls = 4;
		for(int i = 0;i<1000;i++){
			if(ishalt && !isDecodeInputReady && !isExInputReady  && !isMemInputReady && !isWBInputReady){
				break;
			}
			clockCycles++;
			System.out.println("----------------------------");
			System.out.println("ClockCycle:"+clockCycles);
			writeback(); 			//Writeback
			memory();				//Memory
			isContinue = execute();	//Execute
			if(isContinue){
				noOfStalls++;
				System.out.println("Continue From execute..");
				continue;
			}
			isContinue = decode();	//Decode
			if(isContinue){
				noOfStalls++;
				System.out.println("Continue From decode..");
				continue;
			}
			if(!ishalt)
				fetch();				//Fetch
		}
		printData(clockCycles,noOfStalls);
	}
	
	private void printData(int i, int noOfStalls) {
		System.out.println("Name: Narender Soorineeda, ID: efq398");
		System.out.println("Fetched Ins Count:"+fetchedInsCount);
		System.out.println("Finished Ins Count:"+finishedInsCount);		
		System.out.println("Number of Clock Cycles = "+i);
		System.out.println("Number of Stalls  = "+noOfStalls);
		System.out.println("Registers:");
		System.out.println("R0:"+registers[0]);
		System.out.println("R1:"+registers[1]);
		System.out.println("R2:"+registers[2]);
		System.out.println("R3:"+registers[3]);
		System.out.println("R4:"+registers[4]);
		System.out.println("R5:"+registers[5]);
		System.out.println("R6:"+registers[6]);
		System.out.println("R7:"+registers[7]);
	}

	//Fetch the instruction
	//Lock the target
	//Input to Decode
	//Increment PC
	private  boolean fetch(){
		fetchedInsCount++;
		System.out.println("Fetch");
		System.out.println("PC:"+programCounter);
		//Fetch the instruction
		String ins = instruction.get(programCounter/4);
		String in1 = input1.get(programCounter/4);
		String in2 = input2.get(programCounter/4);
		String in3 = input3.get(programCounter/4);
		System.out.println("ins is :"+ins);

		if("HLT".equals(ins)){
			ishalt = true;
			isDecodeInputReady = true;
			inputToDecode.setInstrucion("HLT");
			return true;
		}
		//Increment PC
		programCounter += 4;

		//Input to Decode
		inputToDecode.setInstrucion(ins);
		inputToDecode.setTarget(in1);
		inputToDecode.setInputl(in2);
		inputToDecode.setInput2(in3);		
		isDecodeInputReady = true;
		
		if("BEQZ".equals(ins)||"BNEQZ".equals(ins)){
			inputToDecode.setInputl(in1);
			inputToDecode.setInput2(in2);
		}
		//Lock the target
		return false;
	}

	//Input to Execute
	//returns true if its a stall
	//		IN1 -- locked or
	//		IN2 -- locked or
	//		if it is a BEZ or BNEZ instruction
	private  Boolean decode(){
		int[] registerNumber = {-1,-1};
		if(isDecodeInputReady){
			System.out.println("Decode");
			
			if("HLT".equals(inputToDecode.getInstrucion())){
				System.out.println("HLT decode..");
				isDecodeInputReady = false;
				isExInputReady = true;
				inputToEx.setInstrucion("HLT");
				return false;
			}

			//Special case for store.. check dependency on source as well..
			//		Source -- locked ??
			if("ST".equals(inputToDecode.getInstrucion())){
				registerNumber = getRegisterNumber(inputToDecode.getTarget());
				if(registerNumber[0] == 1){
					System.out.println("Source is locked"+inputToDecode.getTarget());
					return true;
				}
			}

			//		IN1 -- locked or
			registerNumber = getRegisterNumber(inputToDecode.getInputl());
			if(registerNumber[0] == 1){
				System.out.println("IN1 is locked"+inputToDecode.getInputl());
				return true;
			}else{
				System.out.println("IN1 is not locked..Register"+inputToDecode.getInputl());
				//		IN2 -- locked or
				registerNumber = getRegisterNumber(inputToDecode.getInput2());
				if(registerNumber[0] == 1){
					System.out.println("IN2 is locked"+inputToDecode.getInput2());
					return true;
				}else{
					System.out.println("IN2 is not locked..Register"+inputToDecode.getInput2());					
					//Input to Execute
					decodeInstruction();
					isExInputReady = true;
					isDecodeInputReady = false;
					
					//Placing lock on the target register...
					String ins = inputToDecode.getInstrucion();
					int[] register1 = getRegisterNumber(inputToDecode.getTarget());
					if(!"ST".equals(ins) && !"BEQZ".equals(ins)&&!"BNEQZ".equals(ins)&&!"HLT".equals(ins)){
						System.out.println("FF:"+register1[1]+"-->"+isRegisterLocked[register1[1]]);
						isRegisterLocked[register1[1]] += 1;
						System.out.println("YY:"+register1[1]+"-->"+isRegisterLocked[register1[1]]);
					}

					// if it is a BEZ or BNEZ instruction
					if("BEQZ".equals(inputToDecode.getInstrucion())||"BNEQZ".equals(inputToDecode.getInstrucion()))
						return true;
				}
			}
		}
		return false;
	}
	
	//set ins to Execute block
	//set Target to Execute block
	//set Input1 to Execute block
	//set Input2 to Execute block	
	private void decodeInstruction() {
			int[] registerNumber = {-1,-1};
			//set ins to Execute block
			inputToEx.setInstrucion(inputToDecode.getInstrucion());
			//set Target to Execute block
			inputToEx.setTarget(inputToDecode.getTarget());
			System.out.println("Ins is:"+inputToDecode.getInstrucion());
			switch(inputToDecode.getInstrucion()){
				case "ADD":
				case "SUB":
				case "MUL":
				case "DIV":
				case "XOR":
				case "AND":
				case "OR":
				case "ADDI":
				case "SUBI":
				case "LD":
				case "ST":
				case "BEQZ":
				case "BNEQZ":
					//set Input1 to Execute block
					registerNumber = getRegisterNumber(inputToDecode.getInputl());
					if(registerNumber[1] == 8){
						inputToEx.setValueA(Integer.parseInt(inputToDecode.getInputl()));
					}else if(registerNumber[1] != 9){
						inputToEx.setValueA(registers[registerNumber[1]]);
					}
					
					//set Input2 to Ex block
					registerNumber = getRegisterNumber(inputToDecode.getInput2());
					if(registerNumber[1] == 8){
						inputToEx.setValueB(Integer.parseInt(inputToDecode.getInput2()));
					}else if(registerNumber[1] != 9){
						inputToEx.setValueB(registers[registerNumber[1]]);
					}
					break;
			default:
				System.out.println("naren..error:default in decodeInstruction");
			}
	}

	//Sets Input to Memory
	//returns true if its a stall
	//		if it is a BEZ or BNEZ instruction
	//set ins to Memory block
	//set Target to Memory block
	//set Input1 to Memory block
	//set Input2 to Memory block	
	private  Boolean execute(){
		if(isExInputReady){
			if("HLT".equals(inputToEx.getInstrucion())){
				System.out.println("HLT execute..");
				isExInputReady = false;
				isMemInputReady = true;
				inputToMem.setInstrucion("HLT");
				return false;
			}
			System.out.println("Execute");
			System.out.println("ins is:"+inputToEx.getInstrucion());
			int valueA = inputToEx.getValueA();
			int valueB = inputToEx.getValueB();
			int valueC;
			switch(inputToEx.getInstrucion()){
			case "ADD":
				valueC = valueA + valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "SUB":
				valueC = valueA - valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "ADDI":
				valueC = valueA + valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "SUBI":
				valueC = valueA - valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "MUL":
				valueC = valueA * valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "DIV":
				valueC = valueA / valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "LD":
			case "ST":
				valueC = valueA + valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "BEQZ":
				isExInputReady = false;
				isMemInputReady = true;
				inputToMem.setInstrucion("BEQZ");
				System.out.println("value is :"+valueA+"-->PC is:"+programCounter);
				if(valueA == 0){
					programCounter = programCounter + (valueB*4);
					System.out.println("updated PC:"+programCounter);
					return true;
				}	
				break;
			case "BNEQZ":
				isExInputReady = false;
				isMemInputReady = true;
				inputToMem.setInstrucion("BNEQZ");
				if(valueA != 0){
					System.out.println("value is :"+valueA+"-->PC is:"+programCounter);
					programCounter = programCounter + (valueB*4);
					System.out.println("updated PC:"+programCounter);
					return true;
				}
				break;
			case "XOR":
				valueC = valueA ^ valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "AND":
				valueC = valueA & valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "OR":
				valueC = valueA | valueB;
				isMemInputReady = true;
				isExInputReady = false;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			default:
				isExInputReady = false;
			}
			isExInputReady = false;
			
			// if it is a BEZ or BNEZ instruction
			if("BEQZ".equals(inputToEx.getInstrucion())||"BNEQZ".equals(inputToEx.getInstrucion())){
				System.out.println("instruction is branch..return true..");
				return true;
			}
			
			return false;
		}
		return false;
	}
	
	//Sets Input to WriteBack
	//set ins to WriteBack block
	//set Target to WriteBack block
	//set Input1 to WriteBack block
	//set Input2 to WriteBack block	
	private  void memory(){
		if(isMemInputReady){
			System.out.println("Memory");
			if("BEQZ".equals(inputToMem.getInstrucion()) || "BNEQZ".equals(inputToMem.getInstrucion())){
				System.out.println(inputToMem.getInstrucion()+" Memory..");
				isMemInputReady = false;
				isWBInputReady = true;
				inputToWr.setInstrucion(inputToMem.getInstrucion());
			}else if("HLT".equals(inputToMem.getInstrucion())){
				System.out.println("HLT Memory..");
				isMemInputReady = false;
				isWBInputReady = true;
				inputToWr.setInstrucion("HLT");
			}else{
	  			switch(inputToMem.getInstrucion()){
					case "LD":
						int valueD = memory[inputToMem.getValueC()];
						isWBInputReady = true;
						isMemInputReady = false;
						inputToWr.setInstrucion(inputToMem.getInstrucion());
						inputToWr.setValueC(valueD);
						inputToWr.setTarget(inputToMem.getTarget());
						System.out.println("DiskRead:"+inputToMem.getTarget()+"-->"+inputToMem.getValueC()+"-->"+valueD);
						break;
					case "ST":
						int[] registerNumber = getRegisterNumber(inputToMem.getTarget());
						System.out.println("DiskWrite:"+inputToMem.getTarget()+"-->"+inputToMem.getValueC()+"-->"+registers[registerNumber[1]]);
						memory[inputToMem.getValueC()] = registers[registerNumber[1]];
						isMemInputReady = false;
						isWBInputReady = true;
						inputToWr.setInstrucion("ST");
						break;
					default:				
						inputToWr.setInstrucion(inputToMem.getInstrucion());
						inputToWr.setValueC(inputToMem.getValueC());
						inputToWr.setTarget(inputToMem.getTarget());
						isWBInputReady = true;
						isMemInputReady = false;
	  			}
			}

		}
	}
	
	//Writeback the value to register..	
	private  void writeback(){
		if(isWBInputReady){
			finishedInsCount++;
			System.out.println("Writeback");

			if(		   "BEQZ".equals(inputToWr.getInstrucion()) 
					|| "BNEQZ".equals(inputToWr.getInstrucion()) 
					|| "HLT".equals(inputToWr.getInstrucion()) 
					|| "ST".equals(inputToWr.getInstrucion())
			){
				System.out.println("No write-->INS:"+inputToWr.getInstrucion()+" Write..");
				isWBInputReady = false;
			}else{			
				System.out.println(inputToWr.getTarget()+"-->"+inputToWr.getValueC());
				int[] registerNumber = getRegisterNumber(inputToWr.getTarget());
				registers[registerNumber[1]] = inputToWr.getValueC();
				isWBInputReady = false;
				System.out.println("BB:"+registerNumber[1]+"-->"+isRegisterLocked[registerNumber[1]]);
				System.out.println("Target Register:"+inputToWr.getTarget());
				System.out.println("AA--> lock count"+isRegisterLocked[registerNumber[1]]);
				isRegisterLocked[registerNumber[1]] -= 1;
				System.out.println("AA--> lock count"+isRegisterLocked[registerNumber[1]]);
				System.out.println("AA:"+registerNumber[1]+"-->"+isRegisterLocked[registerNumber[1]]);
			}
			}
	}
	
/*	private String intToBinaryString(int writeBackValue,int regNum) {		
		
		if(writeBackValue <0){
			isRegisterNegative[regNum] = -1;
			writeBackValue *= -1;
		}else{
			isRegisterNegative[regNum] = 1;
		}
		String str = Integer.toBinaryString(writeBackValue);
		System.out.println("naren..int:"+writeBackValue+"\nBinary:"+str);
		while(str.length() < 16){
			String P = "0";
			str = P.concat(str);
		}
		return str;
	}
*/
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
		private int[] getRegisterNumber(String registerName) {
			int[] registerStatus = {-1,-1};
			
			switch(registerName){
				case "R0":
					registerStatus[1] = 0;
					if(isRegisterLocked[0] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
			case "R1":
					registerStatus[1] = 1;
					if(isRegisterLocked[1] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R2":
					registerStatus[1] = 2;
					if(isRegisterLocked[2] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R3":
					registerStatus[1] = 3;
					if(isRegisterLocked[3]!= 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R4":
					registerStatus[1] = 4;
					if(isRegisterLocked[4] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R5":
					registerStatus[1] = 5;
					if(isRegisterLocked[5] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R6":
					registerStatus[1] = 6;
					if(isRegisterLocked[6] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "R7":
					registerStatus[1] = 7;
					if(isRegisterLocked[7] != 0)
						registerStatus[0] = 1;
					else
						registerStatus[0] = 0;
					return registerStatus;
				case "RR":
					registerStatus[1] = 9;
					registerStatus[0] = 0;
					return registerStatus;
				default:
					registerStatus[1] = 8; // 8-->its not a register..
					registerStatus[0] = 0; 
					return registerStatus;
			}
		}
	
}
