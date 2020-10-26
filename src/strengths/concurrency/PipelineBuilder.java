package concurrency;

import java.util.*;

/**
Class which builds a pipeline.
Only way for classes outside the concurrency package to get a Pipeline. <br>
The pipeline consists of groups of threads connected by nodes. <br>
Each time the addThreads method is called, these threads are added, separating two nodes. <br>
The input type of the first thread group and the output type of the last thread group must be Void. <br>
When a thread group with an output type of Void is added to the PipelineBuilder, the builder becomes closed. <br>
Once a builder is closed, no more thread groups can be added to it. <br>
A builder must be closed in order to be build into a pipeline. <br>
@author Owen Kulik
*/
public class PipelineBuilder{
	private Class<?> nextType;
	private boolean closed;
	private Thread.UncaughtExceptionHandler defaultHandler;
	private NodeBehavior defaultBehavior = NodeBehavior.BLOCKING;

	private List<Node<?>> nodes;
	private List<PipelineRunnable<?, ?>> runnables;
	private int framerate = 0;

	PipelineBuilder(){
		nextType = Void.class;
		closed = false;
		nodes = new LinkedList<Node<?>>();
		runnables = new LinkedList<PipelineRunnable<?, ?>>();
	}

	/**
	Adds a group of threads representing the passed functions to the pipeline, using the specified uncaughtExceptionHandler and node behavior. <br>
	The threads added with one call to this function represent one step in the pipeline. <br>
	The input and output types of each function must match. <br>
	The input and output types must be passed in as Class objects. <br>
	@param inputType A class object representing the input type of the passed functions.
	@param outputType A class object representing the output type of the passed functions.
	@param ueh An uncaghtExceptionHandler which will be used for all threads added in this method call.
	@param nb The node behavior to use for the next node in the pipeline.
	@param threads The functions to add.
	@throws PipelineBuilderException if no functions are passed, the builder is closed, or the input type of this call does not match the output type of the last call.
	@return this, for method call chaining.
	*/
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final <T, V> PipelineBuilder addThreads(Class<T> inputType, Class<V> outputType, Thread.UncaughtExceptionHandler ueh, NodeBehavior nb, PipelineFunction<T, V>... threads){
		if(threads.length == 0){
			throw new PipelineBuilderException("threads.length was 0.");
		}
		if(this.isClosed()){
			throw new PipelineBuilderException("Cannot add threads to a closed builder.");
		}
		if(!inputType.equals(nextType)){
			throw new PipelineBuilderException("Input type did not match last output type: Expected " + nextType.toString() + ", got " + inputType.toString() + ".");
		}
		nextType = outputType;
		if(nextType.equals(Void.class)){
			closed = true;
		}

		//Whether the pipelineBuilder is closed must be determined by this point in the method.
		//This unchecked cast is safe because we verified it at the beginning of the method using the inputType and nextType variables.
		Node<T> inputNode = nodes.size() > 0 ? (Node<T>)nodes.get(nodes.size() - 1) : null;
		Node<V> outputNode = null; 
		if(this.isClosed()){
			outputNode = null;
		} else {
			if(nb == NodeBehavior.BLOCKING){
				outputNode = new SimpleNode<V>();
			} else if(nb == NodeBehavior.REPLACING){
				outputNode = new ReplacingNode<V>();
			}
			nodes.add(outputNode);
		}
		int framerate = inputType.equals(Void.class) ? this.framerate : 0;
		for(PipelineFunction<T, V> pf : threads){
			runnables.add(new PipelineRunnable<T, V>(inputNode, pf, outputNode, ueh, framerate));
		}
		return this;
	}

	/**
	Adds a group of threads representing the passed functions to the pipeline, using the default uncaughtExceptionHandler and node behavior.
	The threads added with one call to this function represent one step in the pipeline.
	The input and output types of each function must match.
	The input and output types must be passed in as Class objects.
	@param inputType A class object representing the input type of the passed functions.
	@param outputType A class object representing the output type of the passed functions.
	@param threads The functions to add.
	@throws PipelineBuilderException if a default uncaughtExceptionHandler has not been specified, no functions are passed, 
			the builder is closed, or the input type of this call does not match the output type of the last call.
	@return this, for method call chaining.
	*/
	@SafeVarargs
	public final <T, V> PipelineBuilder addThreads(Class<T> inputType, Class<V> outputType, PipelineFunction<T, V>... threads){
		if(defaultHandler == null){
			throw new PipelineBuilderException("Cannot add threads without an UncaughtExceptionHandler before a default Handler has been set");
		}
		return this.addThreads(inputType, outputType, defaultHandler, defaultBehavior, threads);
	}

	/**
	Adds a group of threads representing the passed functions to the pipeline, using the specified uncaughtExceptionHandler and the default node behavior. <br>
	The threads added with one call to this function represent one step in the pipeline. <br>
	The input and output types of each function must match. <br>
	The input and output types must be passed in as Class objects. <br>
	@param inputType A class object representing the input type of the passed functions.
	@param outputType A class object representing the output type of the passed functions.
	@param ueh An uncaghtExceptionHandler which will be used for all threads added in this method call.
	@param threads The functions to add.
	@throws PipelineBuilderException if no functions are passed, the builder is closed, or the input type of this call does not match the output type of the last call.
	@return this, for method call chaining.
	*/
	@SafeVarargs
	public final <T, V> PipelineBuilder addThreads(Class<T> inputType, Class<V> outputType, Thread.UncaughtExceptionHandler ueh, PipelineFunction<T, V>... threads){
		return this.addThreads(inputType, outputType, ueh, defaultBehavior, threads);
	}

	/**
	Adds a group of threads representing the passed functions to the pipeline, using the default uncaughtExceptionHandler and the specified node behavior. <br>
	The threads added with one call to this function represent one step in the pipeline. <br>
	The input and output types of each function must match. <br>
	The input and output types must be passed in as Class objects. <br>
	@param inputType A class object representing the input type of the passed functions.
	@param outputType A class object representing the output type of the passed functions.
	@param nb The node behavior to use for the next node in the pipeline.
	@param threads The functions to add.
	@throws PipelineBuilderException if no functions are passed, the builder is closed, or the input type of this call does not match the output type of the last call.
	@return this, for method call chaining.
	*/
	@SafeVarargs
	public final <T, V> PipelineBuilder addThreads(Class<T> inputType, Class<V> outputType, NodeBehavior nb, PipelineFunction<T, V>... threads){
		return this.addThreads(inputType, outputType, defaultHandler, nb, threads);
	}

	/**
	Sets the default uncaughtExceptionHandler.
	All threads will use this uncaughtExceptionHandler unless otherwise specified in the addThreads call.
	@param ueh The uncaughtExceptionHandler
	@return this, for method call chaining.
	*/
	public PipelineBuilder setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler ueh){
		defaultHandler = ueh;
		return this;
	}

	/**
	Sets the framerate.
	The framrate is the amount of time that each thread in the first step of the pipeline waits between method calls.
	@param f the framerate
	@return this, for method call chaining.
	*/
	public PipelineBuilder setFramerate(int f){
		framerate = f;
		return this;
	}

	/**
	Whether this PipelineBuilder is closed.
	@return whether this PipelineBuilder is closed.
	*/
	public boolean isClosed(){
		return closed;
	}

	/**
	Builds the pipeline and returns it.
	@return the pipeline.
	*/
	public Pipeline build(){
		if(!this.isClosed()){
			throw new PipelineBuilderException("Cannot build a pipeline if builder is not closed.");
		}
		List<Thread> threads = new LinkedList<Thread>();
		for(Runnable r : runnables){
			threads.add(new Thread(r));
		}
		return new SimplePipeline(threads);
	}
}