package controller;

import java.time.LocalDate;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Transaction;

import application.Main;
import exception.UnexpectedInputException;
import model.*;

public class MainViewController{
	
	private WalletSystem walletSystem;
	private Main main;
	
	public MainViewController() {
		walletSystem = new WalletSystem();
	}
	
	@FXML
	Label balanceText;
	
	@FXML
	Label valueIndicator;
	
	@FXML
	Label incomeIndicator;
	
	@FXML
	Label expenseIndicator;
	
	// Agregar transacción
	
	@FXML
	ComboBox<String> typeInput;
	
	@FXML
	TextField valueInput;
	
	@FXML
	TextField descriptionInput;
	
	@FXML
	Button addTransactionButton;
	
	@FXML
	DatePicker dateInput;
	
	@FXML
	VBox addMenu;
	
	// Filtro
	
	@FXML
	DatePicker datePicker1;
	
	@FXML
	DatePicker datePicker2;
	
	@FXML
	Button applyFilterButton;
	
	@FXML
	Button deleteFilterButton;
	
	// Tabla
	
	@FXML
	TableView<Transaction> table;
	
	@FXML
	TableColumn<Transaction, LocalDate> dateColumn;
	
	@FXML
	TableColumn<Transaction, Double> valueColumn;
	
	// Info
	
	@FXML
	VBox infoMenu;
	
	@FXML
	TextField typeOutput;
	
	@FXML
	TextField valueOutput;
	
	@FXML
	TextField descriptionOutput;

	@FXML
	TextField dateOutput;
	
	@FXML
	Button deleteButton;
	
	private ObservableList<String> options = FXCollections.observableArrayList("Ingreso", "Egreso");
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	@FXML
	public void initialize() {
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
		typeInput.setItems(options);
		datePicker1.setValue(LocalDate.now());
		datePicker2.setValue(LocalDate.now());
	}
	
	public void refreshData() {
		table.setItems(main.getData());
	}
	
	@FXML
	void addTransaction() {
		try {
			String type = typeInput.getValue();
			
			if (type==null) {
				throw new UnexpectedInputException("El campo Tipo de Movimiento está vacío, debe asignar un valor antes de continuar");
			}
			
			String preValue = valueInput.getText();
			if (preValue.equals("") || preValue==null) {
				throw new UnexpectedInputException("El campo Monto está vacío, debe asignar un valor antes de continuar");
			}
			
			double value;
			try {
				value = Double.parseDouble(valueInput.getText());
				
				if (value<0) {
					throw new UnexpectedInputException("No se puede ingresar un valor negativo en el campo Monto");
				}
				
			} catch(NumberFormatException e) {
				throw new UnexpectedInputException("Debe ingresar un valor numérico en el campo Monto"); // Quería lanzar mi propia excepción xD
			}
			
			LocalDate date = dateInput.getValue();
			
			if (date==null) {
				throw new UnexpectedInputException("El campo Fecha está vacío, debe asignar un valor antes de continuar");
			}
			
			main.addTransaction(value, descriptionInput.getText(), type, date);
			
			typeInput.valueProperty().set(null);
			valueInput.setText(null);
			descriptionInput.setText(null);
			dateInput.setValue(null);
			recalculate();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Movimiento agregado");
			alert.setHeaderText(null);
			alert.setContentText("El movimiento ha sido agregado exitosamente");
			alert.showAndWait();
		} catch(UnexpectedInputException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(deleteButton.getScene().getWindow());
			alert.setTitle("Campo inválido");
			alert.setHeaderText(null);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}
		
	@FXML
	public void showInfo() {
		Transaction transaction = table.getSelectionModel().getSelectedItem();
		if (transaction != null) {
			String translatedType = transaction.getType()==TransactionTypes.Expense?"Gasto":"Ingreso";
			typeOutput.setText(translatedType);
			valueOutput.setText("$"+transaction.getValue());
			String description = transaction.getDescription().equals("")?"Sin descripción":transaction.getDescription();
			descriptionOutput.setText(description);
			dateOutput.setText(transaction.getDate().toString());
			infoMenu.setDisable(false);
		}
	}
	
	@FXML
	public void hideInfo() {
		infoMenu.setDisable(true);
		typeOutput.setText(null);
		valueOutput.setText(null);
		descriptionOutput.setText(null);
		dateOutput.setText(null);
		table.getSelectionModel().select(null);
	}
	
	@FXML
	public void deleteTransaction() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(deleteButton.getScene().getWindow());
		alert.setTitle("¿Eliminar movimiento?");
		alert.setHeaderText(null);
		alert.setContentText("Los movimientos eliminados no podrán ser recuperados en el futuro");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			Transaction transaction = table.getSelectionModel().getSelectedItem();
			main.removeTransaction(transaction);
			hideInfo();
		}
		recalculate();
	}
	
	@FXML
	public void applyFilter() {      
		LocalDate minDate = datePicker1.getValue();
        LocalDate maxDate = datePicker2.getValue();
		main.applyFilter(minDate, maxDate);
		deleteFilterButton.setDisable(false);
		balanceText.setText("Balance (filtrado)");
		recalculate();
	}
	
	@FXML
	public void deleteFilter() {
		main.deleteFilter();
		applyFilterButton.setDisable(false);
		deleteFilterButton.setDisable(true);
		recalculate();
		datePicker1.setValue(LocalDate.now());
		datePicker2.setValue(LocalDate.now());
		balanceText.setText("Balance");
		recalculate();
	}
	
	public void recalculate() {
		double[] values = main.calculateBalance();
		valueIndicator.setText("$" + values[0]);
		incomeIndicator.setText("$" + values[1]);
		expenseIndicator.setText("$" + values[2]);
	}
}
