package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class InHouse extends Part {
    private IntegerProperty machineId = new SimpleIntegerProperty();

    public InHouse(int id, String name, double price, int stock, int min, int max, int machineId) {
        super(id, name, price, stock, min, max);
        this.machineId.set(machineId);
    }

    public InHouse(String name, double price, int stock, int min, int max, int machineId) {
        super(name, price, stock, min, max);
        this.machineId.set(machineId);
    }

    /**
     * Copy constructor
     * @param p Other part object to copy from
     */
    public InHouse(final InHouse p) {
        this(p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getMin(), p.getMax(), p.getMachineId());
    }

    public int getMachineId() {
        return this.machineId.get();
    }

    public void setMachineId(int machineId) {
        this.machineId.set(machineId);
    }

    public IntegerProperty machineIdProperty() {
        return this.machineId;
    }
}
