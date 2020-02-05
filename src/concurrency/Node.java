package concurrency;

import java.util.*;

interface Node<T>{
	void deposit(PipelineRunnable<?, T> pr, T t) throws InterruptedException;

	Collection<T> withdraw(PipelineRunnable<T, ?> pr) throws InterruptedException;

	void pingAsProducer(PipelineRunnable<?, T> pr);

	void pingAsConsumer(PipelineRunnable<T, ?> pr);
}