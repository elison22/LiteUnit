package test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All classes marked with this annotation will be scanned for tests.
 * The default behavior of the TestDriver is to ignore all classes without
 * this annotation, but that behavior can be overridden.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LiteClass {
}
