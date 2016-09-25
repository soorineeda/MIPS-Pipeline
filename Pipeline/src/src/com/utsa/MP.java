package src.com.utsa;

import java.util.ArrayList;
import java.util.Scanner;

public class MP {
	boolean isDecodeInputReady = false, isExInputReady = false, isMemInputReady = false, 
			isWBInputReady = false, ishalt = false;
	int isRegisterLocked[] = {0,0,0,0,0,0,0,0}, isRegisterNegative[] = {1,1,1,1,1,1,1,1},
			registers[] = {0,0,0,0,0,0,0,0}, memory[] = new int[1024];
	int programCounter = 0,clockCycles =0,fetchedInsCount = 0,finishedInsCount = 0;
	Input inputToDecode = new Input(),inputToEx = new Input(),inputToMem = new Input(),inputToWr = new Input();	
	ArrayList<String> instruction, input1, input2, input3;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Program Number [1-6]");
		int progNumber = sc.nextInt();
		MachineCodes machineCodes = new MachineCodes();
		machineCodes.createiMemory(progNumber);		
		MP mp = new MP();
		mp.prepareInputs(machineCodes);
		mp.mainMethod();
		sc.close();
	}

	public void mainMethod(){
		Boolean isContinue = false;
		int noOfStalls = 4;
		for(int i = 0;i<1000;i++){
			if(ishalt && !isDecodeInputReady && !isExInputReady && !isMemInputReady && !isWBInputReady){
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

	private void printData(int clockCycles, int noOfStalls) {
		System.out.println("Name: Narender Soorineeda, ID: efq398");
		System.out.println("Fetched Ins Count:"+fetchedInsCount);
		System.out.println("Finished Ins Count:"+finishedInsCount);	
		System.out.println("Number of Clock Cycles = "+clockCycles);
		System.out.println("Number of Stalls  = "+noOfStalls);
		System.out.println("Registers:");
		for(int i=0; i<8; i++)
			System.out.println("R"+i+":"+registers[i]);
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
	private  boolean decode(){
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
			isMemInputReady = true;
			isExInputReady = false;
			switch(inputToEx.getInstrucion()){
			case "ADD":
				valueC = valueA + valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "SUB":
				valueC = valueA - valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "ADDI":
				valueC = valueA + valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "SUBI":
				valueC = valueA - valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "MUL":
				valueC = valueA * valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "DIV":
				valueC = valueA / valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "LD":
			case "ST":
				valueC = valueA + valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "BEQZ":
				inputToMem.setInstrucion("BEQZ");
				System.out.println("value is :"+valueA+"-->PC is:"+programCounter);
				if(valueA == 0){
					programCounter = programCounter + (valueB*4);
					System.out.println("updated PC:"+programCounter);
					return true;
				}	
				break;
			case "BNEQZ":
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
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "AND":
				valueC = valueA & valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			case "OR":
				valueC = valueA | valueB;
				inputToMem.setInstrucion(inputToEx.getInstrucion());
				inputToMem.setValueC(valueC);
				inputToMem.setTarget(inputToEx.getTarget());
				break;
			default:
				isExInputReady = false;
			}
			isExInputReady = false;

			// if it is a BEZ or BNEZ instruction
			if("BEQZ".equals(inputToEx.getInstrucion()) || "BNEQZ".equals(inputToEx.getInstrucion())){
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
			isWBInputReady = true;
			isMemInputReady = false;
			if("BEQZ".equals(inputToMem.getInstrucion()) || "BNEQZ".equals(inputToMem.getInstrucion())){
				System.out.println(inputToMem.getInstrucion()+" Memory..");
				inputToWr.setInstrucion(inputToMem.getInstrucion());
			}else if("HLT".equals(inputToMem.getInstrucion())){
				System.out.println("HLT Memory..");
				inputToWr.setInstrucion("HLT");
			}else{
				switch(inputToMem.getInstrucion()){
				case "LD":
					int valueD = memory[inputToMem.getValueC()];
					inputToWr.setInstrucion(inputToMem.getInstrucion());
					inputToWr.setValueC(valueD);
					inputToWr.setTarget(inputToMem.getTarget());
					System.out.println("DiskRead:"+inputToMem.getTarget()+"-->"+inputToMem.getValueC()+"-->"+valueD);
					break;
				case "ST":
					int[] registerNumber = getRegisterNumber(inputToMem.getTarget());
					System.out.println("DiskWrite:"+inputToMem.getTarget()+"-->"+inputToMem.getValueC()+"-->"+registers[registerNumber[1]]);
					memory[inputToMem.getValueC()] = registers[registerNumber[1]];
					inputToWr.setInstrucion("ST");
					break;
				default:				
					inputToWr.setInstrucion(inputToMem.getInstrucion());
					inputToWr.setValueC(inputToMem.getValueC());
					inputToWr.setTarget(inputToMem.getTarget());
				}
			}

		}
	}

	//Writeback the value to register..	
	private  void writeback(){
		if(isWBInputReady){
			finishedInsCount++;
			System.out.println("Writeback");
			isWBInputReady = false;
			if(		   "BEQZ".equals(inputToWr.getInstrucion()) 
					|| "BNEQZ".equals(inputToWr.getInstrucion()) 
					|| "HLT".equals(inputToWr.getInstrucion()) 
					|| "ST".equals(inputToWr.getInstrucion())
					){
				System.out.println("No write-->INS:"+inputToWr.getInstrucion()+" Write..");
			}else{			
				System.out.println(inputToWr.getTarget()+"-->"+inputToWr.getValueC());
				int[] registerNumber = getRegisterNumber(inputToWr.getTarget());
				registers[registerNumber[1]] = inputToWr.getValueC();
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
	private void prepareInputs(MachineCodes machineCodes) {
		this.instruction = machineCodes.getInstruction();
		this.input1 = machineCodes.getInput1();
		this.input2 = machineCodes.getInput2();
		this.input3 = machineCodes.getInput3();
	}

}
