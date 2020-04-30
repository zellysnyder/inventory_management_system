package GUI;

public class MissingPriceException extends Exception {
    public MissingPriceException(String errorMessage) {
        super(errorMessage);
    }
}
