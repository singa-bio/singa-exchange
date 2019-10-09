package bio.singa.exchange;

/**
 * @author cl
 */
public class IllegalConversionException  extends RuntimeException {

    public IllegalConversionException(String message) {
        super(message);
    }

    public IllegalConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}
