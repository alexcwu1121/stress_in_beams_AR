package concurrency;

import java.util.*;

//Withdrawal Value has representation exposure. 
//If one consumer thread modifies their instance of the withdrawn object, it may affect the instance being stored in interchange.
public class SimpleInterchange<T> implements Interchange<T>{
	private int numProducers;
	private int numConsumers;
	private List<T> depositValues;
	private int depositIndex = 0;
	private int withdrawalIndex = 0;

	private boolean producersGreenLight;
	private boolean consumersGreenLight;

	private boolean producersHaveStarted = false;
	private boolean consumersHaveStarted = false;

	SimpleInterchange(){
		numProducers = 0;
		numConsumers = 0;
		producersGreenLight = true;
		consumersGreenLight = false;
		depositValues = new ArrayList<T>(numProducers);
	}

	//Shouldn't be public??? - kind of ok becuase Interchange, SimpleInterchange, and PipelineRunnable are all package private.
	public void pingAsProducer(PipelineRunnable<?, T> pr){
		if(producersHaveStarted){
			throw new IllegalStateException("Cannot ping as producer after producers have already started depositing.");
		}
		if(pr == null){
			throw new IllegalArgumentException("Must provide a valid PipelineRunnable.");
		}
		numProducers++;
	}

	public void pingAsConsumer(PipelineRunnable<T, ?> pr){
		if(pr == null){
			throw new IllegalArgumentException("Must provide a valid PipelineRunnable.");
		}
		if(consumersHaveStarted){
			throw new IllegalStateException("Cannot ping as consumer after consumers have already started withdrawing.");
		}
		numConsumers++;
	}

	public void deposit(T t) throws InterruptedException {
		boolean hasChanged = false;
		while(!producersGreenLight){
			synchronized(this){
				wait();
			}
		}
		producersHaveStarted = true;
		depositValues.add(depositIndex, t);
		depositIndex = (depositIndex + 1) % numProducers;
		if(depositIndex == 0){
			consumersGreenLight = true;
			producersGreenLight = false;
			synchronized(this){
				notifyAll();
			}
		}
	}

	public List<T> withdraw() throws InterruptedException {
		while(!consumersGreenLight){
			synchronized(this){
				wait();
			}
		}
		consumersHaveStarted = true;
		List<T> answer = Collections.unmodifiableList(depositValues);
		withdrawalIndex = (withdrawalIndex + 1) % numConsumers;
		if(withdrawalIndex == 0){
			depositValues = new ArrayList<T>(numProducers);
			producersGreenLight = true;
			consumersGreenLight = false;
			synchronized(this){
				notifyAll();
			}
		}
		return answer;
	}
}