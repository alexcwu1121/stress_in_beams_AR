package util;

import java.lang.annotation.*;

/**Annotation which indicates that a constructor or method is internal, meaning that it is not to be used by the GUI.
@author Owen Kulik
*/

@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal{}