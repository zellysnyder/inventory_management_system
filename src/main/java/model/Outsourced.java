package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Outsourced extends Part {
    private StringProperty companyName = new SimpleStringProperty();

    public Outsourced(String name, double price, int stock, int min, int max, String companyName) {
        super(name, price, stock, min, max);
        this.companyName.set(companyName);
    }

    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName.set(companyName);
    }

    /**
     * Copy constructor
     *
     * @param p Other part object to copy from
     */
    public Outsourced(final Outsourced p) {
        this(p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getMin(), p.getMax(), p.getCompanyName());
    }

    public String getCompanyName() {
        return companyName.get();
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
    }

    public StringProperty companyNameProperty() {
        return this.companyName;
    }
}
