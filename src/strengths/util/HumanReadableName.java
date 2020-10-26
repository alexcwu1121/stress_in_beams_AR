package util;

import java.lang.annotation.*;

/**Annotation representing a human readable name for a class, constructor parameter, or method parameter.
@author Owen Kulik
*/

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HumanReadableName {
	String value();
}