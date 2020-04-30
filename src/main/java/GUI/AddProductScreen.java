package GUI;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.Part;
import model.Product;

public class AddProductScreen extends ProductScreen {
    private Product product;
    ;
    @FXML
    private Button addPartButton;

    public AddProductScreen(ObservableList<Product> products, ObservableList<Part> parts) {
        super(products, parts);
        // operate on a brand new Product type
        this.product = new Product("", 0.0, 0, 0, 0);
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
            checkInventory();
            checkProductPrice();
            if (p != null && Product.isValid(p)) {
                this.products.add(p);
                closeModalImpl();
            }
        } catch (ProductPriceException | ProductPartCountException | ZeroStockException | InventoryBoundsException | MinMaxMismatchException e) {
            Alert.display(e.getMessage());
        }
    }

    @Override
    public final Product getProductModel() {
        return product;
    }

    @Override
    public final void setProductModel(final Product p) {
        this.product = p;
    }

}
