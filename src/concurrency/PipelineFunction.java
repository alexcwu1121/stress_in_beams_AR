package concurrency;

import java.util.*;

/**
Functional interface representing a function to be added to the pipeline.
T is the input type to the function, V is the output type.
*/
public interface PipelineFunction<T, V>{
	/**
	The function to execute. <br>
	The input is provided as a Collection of T. If the input value is Void, then the input will be null. <br>
	If the output type is Void, the function must return null. Otherwise it must NOT return null. <br>
	If a PipelineFunction with a non-Void output type returns null, the program will crash. <br>
	Additionally, the function MUST not modify any values in the input collection. <br>
	If it does so, it may affect the input values for the other threads. <br>
	Since the function does not declare any checked exceptions, the function body cannot throw any checked exceptions. <br>
	If you really want to throw a checked exception, you can just wrap it in an unchecked exceptinon. <br>
	*/
	V execute(Collection<T> t);
}