package GUI;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

public class FormattedTextFields {
    public static UnaryOperator<TextFormatter.Change> integerFieldFormatter = (TextFormatter.Change c) -> {
        return (c.getControlNewText().matches("^[0-9]*$")) ? c : null;
    };

    public static UnaryOperator<TextFormatter.Change> doubleFieldFormatter = (TextFormatter.Change c) -> {
        return c.getControlNewText().matches("^[0-9]*(?:\\.[0-9]{0,2})?$") ? c : null;
    };

    public static StringConverter<Number> integerStringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number number) {
            return number.toString();
        }

        @Override
        public Number fromString(String s) {
            try {
                return Integer.valueOf(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    };

    public static StringConverter<Number> doubleStringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number n) {
            return String.format("%.2f", n);
        }

        @Override
        public Number fromString(String s) {
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    };
}
