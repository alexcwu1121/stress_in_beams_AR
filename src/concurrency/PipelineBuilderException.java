package concurrency;

/**
Class representing an exception thrown during the pipeline building process.
@author Owen Kulik
*/
public class PipelineBuilderException extends RuntimeException {
	PipelineBuilderException(String s){
		super(s);
	}

	PipelineBuilderException(String s, Throwable cause){
		super(s, cause);
	}
}