package org.unibl.etf.instruction;

public class Operand {
	private String value;
	private AddressingType addressingType;
	
	public Operand() {
		super();
	}
	
	public Operand(String value, AddressingType addressingType) {
		this.value = value;
		this.addressingType = addressingType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AddressingType getAddressingType() {
		return addressingType;
	}

	public void setAddressingType(AddressingType addressingType) {
		this.addressingType = addressingType;
	}
	
	@Override
	public String toString() {
//		return "operand value: " + value + ", operand adressing type: " + addressingType;
//		return value;
		return addressingType == AddressingType.DIRECT ? "[" + value + "]" : addressingType == AddressingType.INDIRECT ? "(" + value + ")" : addressingType == AddressingType.IMMEDIATE ? "#" + value : value;
		
//		if(addressingType == AddressingType.DIRECT) {
//			return "[" + value + "]";
//		}
//		else if(addressingType == AddressingType.INDIRECT) {
//			return "(" + value + ")";
//		}
//		else if(addressingType == AddressingType.IMMEDIATE) {
//			return "#" + value;
//		}
//		return value;
	}
}
