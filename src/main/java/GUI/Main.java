package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.InHouse;
import model.Outsourced;
import model.Part;
import model.Product;

import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
    private static ArrayList<Part> testParts = new ArrayList<Part>() {{
        add(new InHouse("in house part #1", 10.0, 20, 10, 100, 0));
        add(new InHouse("in house part #2", 10.0, 20, 10, 100, 0));
        add(new Outsourced("outsourced part #1", 10.0, 20, 10, 100, "company #1"));
        add(new InHouse("in house part #3", 10.0, 20, 10, 100, 0));
    }};

    private static ArrayList<Product> testProducts = new ArrayList<Product>() {{
        add(new Product("Product #1", 10.0, 20, 10, 100) {{
            addAssociatedPart(testParts.get(0));
        }});
        add(new Product("Product #2", 10.0, 20, 10, 100) {{
            addAssociatedPart(testParts.get(1), testParts.get(2));
        }});
        add(new Product("Product #3", 10.0, 20, 10, 100) {{
            addAssociatedPart(testParts.get(0), testParts.get(2));
        }});
    }};

    public static void main(String[] args) {
        launch(args);
    }

    public static ArrayList<Part> generateTestParts() {
        return testParts;
    }

    public static ArrayList<Product> generateTestProducts() {
        return testProducts;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("MainScreen.fxml");
        FXMLLoader ldr = new FXMLLoader(location);
        ldr.setController(new Controller(generateTestParts(), generateTestProducts()));
        Parent root = ldr.load();
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setScene(new Scene(root, 1500, 800));
        primaryStage.show();
    }
}
