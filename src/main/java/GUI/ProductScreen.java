package GUI;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Part;
import model.Product;

import java.util.Optional;
import java.util.function.Consumer;

abstract public class ProductScreen {
    protected final ObservableList<Part> parts;
    protected final ObservableList<Product> products;
    @FXML
    protected Button cancelButton, saveButton, partsSearchButton, addPartButton, deleteAssociatedPartButton;
    @FXML
    protected Text title;
    @FXML
    protected TextField idField;
    @FXML
    protected TextField productNameField, invField, priceField, minField, maxField;
    @FXML
    protected TableView<Part> associatedPartsTableView;
    @FXML
    protected TableView<Part> partsSearchTableView;
    @FXML
    protected TableColumn<Part, Integer> idColumn, inventoryColumn, partsSearchIdColumn, partsSearchInventoryColumn;
    @FXML
    protected TableColumn<Part, String> nameColumn, partsSearchNameColumn;
    @FXML
    protected TableColumn<Part, Double> priceColumn, partsSearchPriceColumn;
    @FXML
    protected TextField partsSearchQueryField;
    @FXML
    private BorderPane container;

    protected ProductScreen(ObservableList<Product> products, ObservableList<Part> parts) {
        this.products = products;
        this.parts = parts;
    }

    protected abstract Product getProductModel();

    protected abstract void setProductModel(final Product p);

    @FXML
    protected void initialize() {
        // Tables
        initPartSearchTable();
        initAssociatedPartsTable();
        // Bind text fields to data
        initBindings();
        // Prohibit nonsensical input into text fields
        initInputMasks();
        // grey out Add button when there's no selection
        addPartButton.disableProperty().bind(Bindings.isNull(partsSearchTableView.getSelectionModel().selectedItemProperty()));
        // grey out Delete button when there's no selection
        deleteAssociatedPartButton.disableProperty().bind(Bindings.isNull(associatedPartsTableView.getSelectionModel().selectedItemProperty()));
    }

    private void initBindings() {
        final Product p = getProductModel();
        idField.setText(Integer.toString(p.getId()));
        saveButton.disableProperty().bind(Bindings.isEmpty(productNameField.textProperty()));
        Bindings.bindBidirectional(productNameField.textProperty(), p.nameProperty());
        Bindings.bindBidirectional(priceField.textProperty(), p.priceProperty(), FormattedTextFields.doubleStringConverter);
        Bindings.bindBidirectional(invField.textProperty(), p.stockProperty(), FormattedTextFields.integerStringConverter);
        Bindings.bindBidirectional(minField.textProperty(), p.minProperty(), FormattedTextFields.integerStringConverter);
        Bindings.bindBidirectional(maxField.textProperty(), p.maxProperty(), FormattedTextFields.integerStringConverter);
    }

    private void deinitBindings() {
        final Product p = getProductModel();
        Bindings.unbindBidirectional(productNameField.textProperty(), p.nameProperty());
        Bindings.unbindBidirectional(priceField.textProperty(), p.priceProperty());
        Bindings.unbindBidirectional(invField.textProperty(), p.stockProperty());
        Bindings.unbindBidirectional(minField.textProperty(), p.minProperty());
        Bindings.unbindBidirectional(maxField.textProperty(), p.maxProperty());
    }

    @Override
    protected void finalize() throws Throwable {
        deinitBindings();
    }

    private void initAssociatedPartsTable() {
        // bind the table columns to the products data store
        idColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        inventoryColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
        associatedPartsTableView.setItems(getProductModel().getAllAssociatedParts());
    }

    private void initPartSearchTable() {
        // parts search has all the parts
        partsSearchIdColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        partsSearchNameColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        partsSearchInventoryColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        partsSearchPriceColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));
        FilteredList<Part> partResults = new FilteredList<>(this.parts);
        partsSearchTableView.setItems(partResults);
        partsSearchTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // react to search input
        Consumer<String> handleQueryChange = (String s) -> {
            partResults.setPredicate((Part p) -> p.getName().toLowerCase().contains(s.toLowerCase()));
        };
        // perform search on every keystroke
        partsSearchQueryField.textProperty().addListener((observableValue, s, t1) -> {
            handleQueryChange.accept(t1);
        });
        partsSearchButton.setOnAction(evt -> {
            handleQueryChange.accept(partsSearchQueryField.getText());
        });
    }

    protected abstract void save();

    /**
     * Implement several validations (section "J" Set 1 in Requirements)
     * - preventing the minimum field from having a value above the maximum field
     * - preventing the maximum field from having a value below the minimum field
     * - ensuring that a product must always have at least one part
     * - ensuring that a product must have a name, price, and inventory level (default 0)
     * - entering an inventory value that exceeds the minimum or maximum value for that part or product
     *
     * @throws ProductPartCountException
     * @throws InventoryBoundsException
     * @throws MinMaxMismatchException
     * @throws MissingNameException
     * @throws MissingPriceException
     * @throws MissingInventoryNumberException
     */
    protected void checkInventory() throws ProductPartCountException, InventoryBoundsException, MinMaxMismatchException, MissingNameException, MissingPriceException, MissingInventoryNumberException {
        final Product p = getProductModel();
        // ensuring that a product must have a name, price, and inventory level (default 0)
        if (productNameField.getText().isEmpty() || p.getName().isEmpty())
            throw new MissingNameException("You must enter a name for this product.");
        if (priceField.getText().isEmpty() || Double.isNaN(p.getPrice()) || p.getPrice() == 0.0)
            throw new MissingPriceException("You must enter a price for this product. It can't be free.");
        if (invField.getText().isEmpty())
            throw new MissingInventoryNumberException("You must enter an inventory stock count.");
        // preventing the minimum field from having a value above the maximum field
        // preventing the maximum field from having a value below the minimum field
        if (!Product.hasSaneMinMaxValues(p))
            throw new MinMaxMismatchException(p.getMin(), p.getMax());
        // ensuring that a product must always have at least one part
        if (p.getAllAssociatedParts().size() < 1)
            throw new ProductPartCountException("There must be at least one part associated with the product");
        // entering an inventory value that exceeds the minimum or maximum value for that part or product
        if (!Product.hasSaneInventoryValues(p))
            throw new InventoryBoundsException(String.format("Stock amount (%d) must be at least the minimum (%d) and no more than the maximum (%d)", p.getStock(), p.getMin(), p.getMax()));
    }

    /**
     * Implement a validation from Section "J" Set 2:
     * - ensuring that the price of a product cannot be less than the cost of the parts
     *
     * @throws ProductPriceException
     */
    protected void checkProductPrice() throws ProductPriceException {
        // J.  Write code to implement exception controls with custom error messages for one requirement out of each of the following sets (pick one from each):
        //  ensuring that the price of a product cannot be less than the cost of the parts
        final Product p = getProductModel();
        if (!Product.hasSanePrice(p)) {
            throw new ProductPriceException();
        }
    }

    @FXML
    private void closeModal() {
        // J.  Write code to implement exception controls with custom error messages for one requirement out of each of the following sets (pick one from each):
        // - including a confirm dialogue for all “Delete” and “Cancel” buttons
        Optional<ButtonType> confirmation = Alert.confirm("This will cancel your current operation");
        confirmation.ifPresent(a -> {
            if (a == ButtonType.OK) {
                closeModalImpl();
            }
        });
    }

    protected void closeModalImpl() {
        Stage s = (Stage) container.getScene().getWindow();
        s.close();
    }

    @FXML
    private void addAssociatedPart() {
        final Part selectedPart = partsSearchTableView.getSelectionModel().getSelectedItem();
        final Product thisProduct = this.getProductModel();
        if (selectedPart != null && thisProduct != null)
            thisProduct.addAssociatedPart(selectedPart);
    }

    @FXML
    private void deleteAssociatedPart() {
        // J.  Write code to implement exception controls with custom error messages for one requirement out of each of the following sets (pick one from each):
        // - including a confirm dialogue for all “Delete” and “Cancel” buttons
        final Optional<ButtonType> confirmation = Alert.confirm("This will remove the selected part from being associated with this product.");
        confirmation.ifPresent(a -> {
            if (a == ButtonType.OK) {
                getSelectedAssociatedPart().ifPresent((final Part p) -> {
                    getProductModel().deleteAssociatedPart(p);
                });
            }
        });
    }

    private Optional<Part> getSelectedAssociatedPart() {
        return Optional.ofNullable(this.associatedPartsTableView.getSelectionModel().getSelectedItem());
    }

    private void initInputMasks() {
        // set up input masks
        invField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
        priceField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.doubleFieldFormatter));
        minField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
        maxField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
    }
}
