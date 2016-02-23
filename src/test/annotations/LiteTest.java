package test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All methods marked with annotation will be evaluated as candidates for
 * testing. For such methods to actually be queued as tests, they must meet
 * additional conditions as outlined in the TestDriver documentation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteTest {
    String reqId() default "";
}
