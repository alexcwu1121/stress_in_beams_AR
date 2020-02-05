package concurrency;

import java.util.*;

class PipelineRunnable<T, V> implements Runnable {
	private Node<T> inputNode;
	private PipelineFunction<T, V> function;
	private Node<V> outputNode;
	private Thread.UncaughtExceptionHandler handler;
	//Hardcoded value
	//private static final int FRAMERATE = 100;
	private int framerate;

	PipelineRunnable(Node<T> ii, PipelineFunction<T, V> pf, Node<V> oi, Thread.UncaughtExceptionHandler ueh, int fr){
		inputNode = ii;
		function = pf;
		outputNode = oi;
		framerate = fr;
		handler = ueh;
		if(ii != null){
			ii.pingAsConsumer(this);
		}
		if(oi != null){
			oi.pingAsProducer(this);
		}
	}

	public void run(){
		while(true){
			Collection<T> inputValue;
			try {
				inputValue = inputNode != null ? inputNode.withdraw(this) : null;
				Thread.sleep(framerate);
			} catch(InterruptedException e){
				return;
			}
			V outputValue;
			try{
				outputValue = function.execute(inputValue);
			} catch(Exception e){
				handler.uncaughtException(Thread.currentThread(), e);
				continue;
			}
			if(outputNode != null){
				try {
					outputNode.deposit(this, outputValue);
				} catch(InterruptedException e){
					return;
				}
			}
		}
	}

}