package asserts;

/**
 * LiteAsserts provides a few of the standard assertions. It does this independent of the
 * Assert class built into Java and JUnit. When these tests fail, they throw a LiteAssertFailedException.
 * <br>
 * All methods are run statically, so "import static LiteAsserts.*;" makes it easy to use the assertions.
 * All methods have an overload that allows the user to include a message that is printed is the assertion fails.
 */
public class LiteAsserts {

    public static void assertTrue(boolean condition, String message) throws LiteAssertFailedException {
        if(!condition) {
            assertFailed(message);
        }

    }

    public static void assertTrue(boolean condition) throws LiteAssertFailedException {
        assertTrue(condition, null);
    }

    public static void assertEquals(Object expected, Object actual, String message) throws LiteAssertFailedException {
        if(expected != null || actual != null) {
            if(expected == null || !expected.equals(actual)) {
                assertMatchFailed(expected, actual, message);
            }
        }
    }

    public static void assertEquals(Object expected, Object actual) throws LiteAssertFailedException {
        assertEquals(expected, actual, null);
    }

    public static void assertNotEquals(Object expected, Object actual, String message) throws LiteAssertFailedException {
        try {
            if (expected != null || actual != null) {
                if (expected == null || !expected.equals(actual)) {
                    assertMatchFailed(expected, actual, message);
                }
            }
        } catch (LiteAssertFailedException e){
            assertDifferFailed(expected, actual, message);
        }

    }

    public static void assertNotEquals(Object expected, Object actual) throws LiteAssertFailedException {
        assertNotEquals(expected, actual, null);
    }

    public static void assertNotNull(Object object) throws LiteAssertFailedException {
        assertNotNull(object, null);
    }

    public static void assertNotNull(Object object, String message) throws LiteAssertFailedException {
        assertTrue(object != null, message);
    }

    public static void assertNull(Object object) throws LiteAssertFailedException {
        if(object != null) {
            assertNull(object, "Expected: <null> but was: " + object.toString());
        }

    }

    public static void assertNull(Object object, String message) throws LiteAssertFailedException {
        assertTrue(object == null, message);
    }



    private static void assertMatchFailed(Object expected, Object actual, String message) throws LiteAssertFailedException {
        assertFailed(shouldMatchMessage(expected, actual, message));
    }

    private static void assertDifferFailed(Object expected, Object actual, String message) throws LiteAssertFailedException {
        assertFailed(shouldDifferMessage(expected, actual, message));
    }

    private static void assertFailed(String message) throws LiteAssertFailedException {
        if(message == null) {
            throw new LiteAssertFailedException();
        } else {
            throw new LiteAssertFailedException(message);
        }
    }

    private static String shouldMatchMessage(Object expected, Object actual, String message) {
        String formatted = "";
        if(message != null && message.length() > 0) {
            formatted = "\n" + message;
        }

        return "expected:<" + expected + "> but was:<" + actual + ">" + formatted;
    }

    private static String shouldDifferMessage(Object expected, Object actual, String message) {
        String formatted = "";
        if(message != null && message.length() > 0) {
            formatted = "\n" + message;
        }

        return "expected:<" + expected + "> to be different from:<" + actual + ">" +  formatted;
    }

}
