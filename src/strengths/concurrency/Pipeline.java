package concurrency;
/**
	Interface representing a group of threads, running concurrently. <br>
	The only way to get a Pipeline instance is with a PipelineBuilder.
	@author Owen Kulik
*/
public interface Pipeline {
	/**
	Returns a PipelineBuilder
	@return a PipelineBuiler
	*/
	public static PipelineBuilder builder(){
		return new PipelineBuilder();
	}

	/**
	Starts all threads in the pipeline.
	*/
	void start();

	/**
	Interrupts all threads in the pipeline.
	*/
	void interrupt();
}