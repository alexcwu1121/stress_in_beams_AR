package concurrency;

import java.util.*;
import java.util.concurrent.*;

//Withdrawal Value has representation exposure. 
//If one consumer thread modifies their instance of the withdrawn object, it may affect the instance being stored in node.
class SimpleNode<T> implements Node<T>{
	private int numProducers;
	private int numConsumers;

	private boolean producersHaveStarted = false;
	private boolean consumersHaveStarted = false;

	private volatile Map<PipelineRunnable<?, T>, T> producers = new HashMap<PipelineRunnable<?, T>, T>();
	private volatile Map<PipelineRunnable<T, ?>, Boolean> consumers = new HashMap<PipelineRunnable<T, ?>, Boolean>();

	SimpleNode(){
		numProducers = 0;
		numConsumers = 0;
	}

	//Shouldn't be public??? - kind of ok becuase Node, SimpleNode, and PipelineRunnable are all package private.
	public void pingAsProducer(PipelineRunnable<?, T> pr){
		if(producersHaveStarted){
			throw new IllegalStateException("Cannot ping as producer after producers have already started depositing.");
		}
		if(pr == null){
			throw new IllegalArgumentException("Must provide a valid PipelineRunnable.");
		}
		numProducers++;
		producers.put(pr, null);
	}

	public void pingAsConsumer(PipelineRunnable<T, ?> pr){
		if(pr == null){
			throw new IllegalArgumentException("Must provide a valid PipelineRunnable.");
		}
		if(consumersHaveStarted){
			throw new IllegalStateException("Cannot ping as consumer after consumers have already started withdrawing.");
		}
		numConsumers++;
		consumers.put(pr, true);
	}

	public void deposit(PipelineRunnable<?, T> pr, T t) throws InterruptedException {
		if(!producers.containsKey(pr)){
			throw new IllegalStateException("Producer did not ping node.");
		}
		if(t == null){
			throw new NullPointerException("Input was null");
		}
		while(producers.get(pr) != null){
			synchronized(this){
				wait();
			}
		}
		producersHaveStarted = true;
		producers.put(pr, t);
		if(!producers.containsValue(null)){
			consumers.replaceAll((k, v) -> {return false;});
			synchronized(this){
				notifyAll();
			}
		}
	}

	public Collection<T> withdraw(PipelineRunnable<T, ?> pr) throws InterruptedException {
		if(!consumers.containsKey(pr)){
			throw new IllegalStateException("Consumer did not ping node.");
		}
		while(consumers.get(pr) /*|| producers.containsValue(null)*/){
			synchronized(this){
				wait();
			}
		}
		consumersHaveStarted = true;
		List<T> answer = Collections.unmodifiableList(new CopyOnWriteArrayList<T>(producers.values()));
		consumers.put(pr, true);
		if(!consumers.containsValue(false)){
			producers.replaceAll((k, v) -> {return null;});
			synchronized(this){
				notifyAll();
			}
		}
		return answer;
	}
}