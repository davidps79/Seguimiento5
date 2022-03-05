package model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;

public class WalletSystem {
	private ObservableList<Transaction> allItems = FXCollections.observableArrayList();
	private FilteredList<Transaction> filteredItems = new FilteredList<>(allItems);
	
	public void addTransaction(double value, String description, String type, LocalDate date) {
		Transaction transaction = new Transaction(value, description, type, date);
		allItems.add(transaction);
	}
	
	public void removeTransaction(Transaction transaction) {
		allItems.remove(transaction);
	}

	public FilteredList<Transaction> getData() {
		return filteredItems;
	}
	
	public double[] calculateBalance() {
		double[] sum = new double[3];
		for (Transaction transaction: filteredItems) {			
			if (transaction.getType().equals(TransactionTypes.Income)) {
				sum[1]+=transaction.getValue();
			} else
				sum[2]+=transaction.getValue();
		}		
		sum[0]=sum[1]+sum[2];
		return sum;
	}
	
	public double calculateBalance2() {
		double sum = 0;
		for (Transaction transaction: allItems) {
			sum+=transaction.getValue();
		}
		return sum;
	}
	
	public void applyFilter(LocalDate minDate, LocalDate maxDate) {

        LocalDate finalMin = minDate == null ? LocalDate.MIN : minDate;
        LocalDate finalMax = maxDate == null ? LocalDate.MAX : maxDate;
        
		filteredItems.setPredicate(item -> !finalMin.isAfter(item.getDate()) && !finalMax.isBefore(item.getDate()));
	}
	
	public void deleteFilter() {
		filteredItems.setPredicate(item -> true);
	}
}
