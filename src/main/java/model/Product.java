package model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product implements IndexedById {
    /// Auto-incremented ID counter
    private static int IdSequence = 0;
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private StringProperty name = new SimpleStringProperty();
    private DoubleProperty price = new SimpleDoubleProperty();
    private IntegerProperty stock = new SimpleIntegerProperty();
    private IntegerProperty min = new SimpleIntegerProperty();
    private IntegerProperty max = new SimpleIntegerProperty();

    private Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        if (IdSequence < id) {
            // update auto-increment counter to highest value
            IdSequence = id;
        }
        this.name.set(name);
        this.price.set(price);
        this.stock.set(stock);
        this.min.set(min);
        this.max.set(max);
    }

    public Product(String name, double price, int stock, int min, int max) {
        this(GenerateId(), name, price, stock, min, max);
    }

    /**
     * Copy constructor
     *
     * @param p Product to copy from
     */
    public Product(final Product p) {
        this(p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getMin(), p.getMax());
        for (final Part part : p.getAllAssociatedParts()) {
            this.addAssociatedPart(part);
        }
    }

    public static int GenerateId() {
        return ++IdSequence;
    }

    public static boolean isBlank(final Product p) {
        return p.getName().isEmpty();
    }

    /**
     * Verifies that the associated parts all cost no more than the price of the whole product
     *
     * @param p Product to check
     * @return whether the price of the cumulative parts is less than or equal to the price of the whole product
     */
    public static boolean hasSanePrice(final Product p) {
        double sum = 0.0;
        for (final Part part : p.getAllAssociatedParts()) {
            sum += part.getPrice();
        }
        return sum < p.getPrice();
    }

    public static boolean hasSaneMinMaxValues(final Product p) {
        return p.getMax() >= p.getMin();
    }

    public static boolean hasSaneInventoryValues(final Product p) {
        return p.getStock() >= p.getMin() && p.getStock() <= p.getMax();
    }

    public static boolean isValid(final Product p) {
        return !isBlank(p) && hasSanePrice(p);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public int getMin() {
        return min.get();
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMax() {
        return max.get();
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }

    public void addAssociatedPart(Part... parts) {
        for (final Part p : parts)
            associatedParts.add(p);
    }

    public boolean deleteAssociatedPart(Part selectedPart) {
        return associatedParts.remove(selectedPart);
    }
}
