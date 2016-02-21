package asserts;

/**
 * Created by brandt on 2/18/16.
 */
public class LiteAssertFailedException extends Exception {

    public LiteAssertFailedException() {}

    public LiteAssertFailedException(String message) {
        super(message);
    }

    public LiteAssertFailedException(Throwable cause) {
        super(cause);
    }

    public LiteAssertFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
