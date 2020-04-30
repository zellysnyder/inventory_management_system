package GUI;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.InHouse;
import model.Outsourced;
import model.Part;

import java.util.Optional;

public abstract class PartScreen {
    @FXML
    protected RadioButton inHouseRadioButton;
    @FXML
    protected RadioButton outsourcedRadioButton;
    @FXML
    protected TextField idField;
    @FXML
    protected TextField nameField;
    @FXML
    protected TextField inventoryField;
    @FXML
    protected TextField priceField;
    @FXML
    protected TextField minField;
    @FXML
    protected TextField maxField;
    @FXML
    protected TextField machineIdField;
    @FXML
    protected TextField companyNameField;
    @FXML
    protected Button saveButton;
    @FXML
    protected Button cancelButton;
    protected ObservableList<Part> allParts;
    @FXML
    protected GridPane container;
    private int index_;
    private ToggleGroup productType;

    protected PartScreen(ObservableList<Part> allParts) {
        this.allParts = allParts;
    }

    protected abstract Part getPartModel();

    protected abstract void setPartModel(Part part);

    @FXML
    public void initialize() {
        final Part thisPart = getPartModel();

        initInputMasks();
        initDataBindings();

        if (thisPart instanceof Outsourced) {
            initBindingsForOutsourced();
        } else if (thisPart instanceof InHouse) {
            initBindingsForInHouse();
        }

        // Radio button logic

        // ensure that only one of either In House or Outsourced can be checked
        ToggleGroup tg = new ToggleGroup();
        inHouseRadioButton.setToggleGroup(tg);
        outsourcedRadioButton.setToggleGroup(tg);

        // show different fields based on which radio is toggled
        // react to change by converting to a different underlying Part subtype
        inHouseRadioButton.setOnAction(evt -> {
            final Part oldPart = getPartModel();
            setPartModel(new InHouse(oldPart.getId(), oldPart.getName(), oldPart.getPrice(), oldPart.getStock(), oldPart.getMin(), oldPart.getMax(), 0));
            togglePartType(InHouse.class);
        });
        outsourcedRadioButton.setOnAction(evt -> {
            final Part oldPart = getPartModel();
            setPartModel(new Outsourced(oldPart.getId(), oldPart.getName(), oldPart.getPrice(), oldPart.getStock(), oldPart.getMin(), oldPart.getMax(), ""));
            togglePartType(Outsourced.class);
        });


    }

    /**
     * Set up input masks--prevent incorrectly formatted input
     */
    private void initInputMasks() {
        inventoryField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
        priceField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.doubleFieldFormatter));
        minField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
        maxField.setTextFormatter(new TextFormatter<String>(FormattedTextFields.integerFieldFormatter));
    }

    private void initDataBindings() {
        final Part thisPart = getPartModel();
        // set up data bindings
        idField.setText(Integer.toString(thisPart.getId()));
        // grey out the Save button if the part is blank
        saveButton.disableProperty().bind(Bindings.isEmpty(nameField.textProperty()));
        Bindings.bindBidirectional(nameField.textProperty(), thisPart.nameProperty());
        Bindings.bindBidirectional(inventoryField.textProperty(), thisPart.stockProperty(), FormattedTextFields.integerStringConverter);
        Bindings.bindBidirectional(priceField.textProperty(), thisPart.priceProperty(), FormattedTextFields.doubleStringConverter);
        Bindings.bindBidirectional(minField.textProperty(), thisPart.minProperty(), FormattedTextFields.integerStringConverter);
        Bindings.bindBidirectional(maxField.textProperty(), thisPart.maxProperty(), FormattedTextFields.integerStringConverter);
    }

    private void deinitDataBindings() {
        final Part p = getPartModel();
        Bindings.unbindBidirectional(nameField.textProperty(), p.nameProperty());
        Bindings.unbindBidirectional(inventoryField.textProperty(), p.stockProperty());
        Bindings.unbindBidirectional(priceField.textProperty(), p.priceProperty());
        Bindings.unbindBidirectional(minField.textProperty(), p.minProperty());
        Bindings.unbindBidirectional(maxField.textProperty(), p.maxProperty());
    }

    private void togglePartType(Class<? extends Part> newType) {
        if (newType.equals(InHouse.class)) {
            deinitDataBindings();
            deinitBindingsForOutsourced();
            initDataBindings();
            initBindingsForInHouse();
        } else if (newType.equals(Outsourced.class)) {
            deinitDataBindings();
            deinitBindingsForInHouse();
            initDataBindings();
            initBindingsForOutsourced();
        }
    }

    protected void initBindingsForInHouse() {
        final Part p = getPartModel();
        if (p instanceof InHouse)
            Bindings.bindBidirectional(machineIdField.textProperty(), ((InHouse) p).machineIdProperty(), FormattedTextFields.integerStringConverter);
    }

    protected void deinitBindingsForInHouse() {
        final Part p = getPartModel();
        if (p instanceof InHouse)
            Bindings.unbindBidirectional(machineIdField.textProperty(), ((InHouse) p).machineIdProperty());
    }

    protected void initBindingsForOutsourced() {
        final Part p = getPartModel();
        if (p instanceof Outsourced)
            Bindings.bindBidirectional(companyNameField.textProperty(), ((Outsourced) p).companyNameProperty());
    }

    protected void deinitBindingsForOutsourced() {
        final Part p = getPartModel();
        if (p instanceof Outsourced)
            Bindings.bindBidirectional(companyNameField.textProperty(), ((Outsourced) p).companyNameProperty());
    }

    @FXML
    protected abstract void save();

    /**
     * Implement several validations (section "J" Set 1 in Requirements)
     * - preventing the minimum field from having a value above the maximum field
     * - preventing the maximum field from having a value below the minimum field
     * - entering an inventory value that exceeds the minimum or maximum value for that part or product
     *
     * @throws InventoryBoundsException
     * @throws ZeroStockException
     * @throws MinMaxMismatchException
     * @throws MissingInventoryNumberException
     * @throws MissingPriceException
     * @throws MissingNameException
     */
    protected void checkInventory() throws InventoryBoundsException, ZeroStockException, MinMaxMismatchException, MissingInventoryNumberException, MissingPriceException, MissingNameException {
        final Part p = getPartModel();
        if (nameField.getText().isEmpty() || p.getName().isEmpty())
            throw new MissingNameException("You must enter a name for this part");
        if (priceField.getText().isEmpty() || Double.isNaN(p.getPrice()))
            throw new MissingPriceException("You must enter a price for this part");
        if (inventoryField.getText().isEmpty())
            throw new MissingInventoryNumberException("You must enter an inventory stock count.");
        if (p.getStock() == 0)
            throw new ZeroStockException();
        // preventing the minimum field from having a value above the maximum field
        // preventing the maximum field from having a value below the minimum field
        if (!Part.hasSaneMinMaxValues(p))
            throw new MinMaxMismatchException(p.getMin(), p.getMax());
        // entering an inventory value that exceeds the minimum or maximum value for that part or product
        if (!Part.hasSaneInventoryValues(p))
            throw new InventoryBoundsException(String.format("Stock amount (%d) must be at least the minimum (%d) and no more than the maximum (%d)", p.getStock(), p.getMin(), p.getMax()));
    }

    @FXML
    protected void closeModal() {
        // J.  Write code to implement exception controls with custom error messages for one requirement out of each of the following sets (pick one from each):
        // - including a confirm dialogue for all “Delete” and “Cancel” buttons
        Optional<ButtonType> confirmation = Alert.confirm("This will cancel your current operation.");
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

    protected boolean isInHouse() {
        return getPartModel() instanceof InHouse;
    }

    protected boolean isOutsourced() {
        return getPartModel() instanceof Outsourced;
    }

    @Override
    protected void finalize() {
        deinitDataBindings();
        if (getPartModel() instanceof Outsourced)
            deinitBindingsForOutsourced();
        if (getPartModel() instanceof InHouse)
            deinitBindingsForInHouse();
    }
}
