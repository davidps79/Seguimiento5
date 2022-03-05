package model;

import java.time.*;

public class Transaction {
	private double value;
	private String description;
	private LocalDate date;
	private TransactionTypes type;
	
	public Transaction(double value, String description, String type, LocalDate date) {
		if (type.equals("Egreso")) {
			value*=-1;
			this.type=TransactionTypes.Expense;
		} else {
			this.type=TransactionTypes.Income;
		}
		this.value = value;
		this.description = description;
		this.date = date;
	}
	
	public double getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public TransactionTypes getType() {
		return type;
	}
}
