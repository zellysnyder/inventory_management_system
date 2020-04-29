package GUI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.Part;
import model.Product;

public class AddProductScreen extends ProductScreen {
    private Product thisProduct = new Product("", 0.0, 0, 0, 0);
    ;
    @FXML
    private Button addPartButton;

    public AddProductScreen(ObservableList<Product> products, ObservableList<Part> parts) {
        super(products, parts);
    }

    @FXML
    protected void initialize() {
        super.initialize();
        super.title.setText("Add Product");
    }

    @Override
    @FXML
    public final void save() {
        try {
            final Product p = getProductModel();
            checkProductCount();
            checkProductPrice();
            this.products.add(p);
        } catch (ProductPartCountException | ProductPriceException e) {
            Alert.display(e.getMessage());
        }
    }

    @Override
    public final Product getProductModel() {
        return thisProduct;
    }


}