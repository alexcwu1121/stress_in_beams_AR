package concurrency;

import java.util.*;

interface Interchange<T>{
	void deposit(T t) throws InterruptedException;

	List<T> withdraw() throws InterruptedException;

	void pingAsProducer(PipelineRunnable<?, T> pr);

	void pingAsConsumer(PipelineRunnable<T, ?> pr);
}