package GUI;

public class MissingInventoryNumberException extends Exception {
    public MissingInventoryNumberException(String errorMessage) {
        super(errorMessage);
    }
}
