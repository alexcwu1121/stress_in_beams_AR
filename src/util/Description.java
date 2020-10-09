package util;

import java.lang.annotation.*;

/**Annotation representing a description for a class, constructor parameter, or method parameter.
@author Owen Kulik
*/

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
	String value();
}