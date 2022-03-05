package application;
import java.time.LocalDate;
import controller.MainViewController;
import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import model.Transaction;
import model.WalletSystem;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
	private WalletSystem walletSystem;
	
	@Override
	public void start(Stage primaryStage) {
		walletSystem = new WalletSystem();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/MainView.fxml"));
			BorderPane root = (BorderPane) loader.load();
			MainViewController controller = loader.getController();
			controller.setMain(this);
			controller.refreshData();
			Scene scene = new Scene(root,800,700);
			scene.getStylesheets().add(getClass().getResource("../ui/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Finanzas Personales");
			primaryStage.getIcons().add(new Image("file:./resources/CoinLogo.png"));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void addTransaction(double value, String description, String type, LocalDate date) {
		walletSystem.addTransaction(value, description, type, date);
	}

	public void removeTransaction(Transaction transaction) {
		walletSystem.removeTransaction(transaction);		
	}
	
	public FilteredList<Transaction> getData() {
		return walletSystem.getData();
	}
	
	public double[] calculateBalance() {
		return walletSystem.calculateBalance();
	}
	
	double sum = 0;

	public void applyFilter(LocalDate minDate, LocalDate maxDate) {
		walletSystem.applyFilter(minDate, maxDate);
	}

	public void deleteFilter() {
		walletSystem.deleteFilter();
	}
}
