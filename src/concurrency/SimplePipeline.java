package concurrency;

import java.util.*;

class SimplePipeline implements Pipeline {
	private List<Thread> threads;

	SimplePipeline(List<Thread> t){
		threads = t;
	}

	public void start(){
		for(Thread t : threads){
			t.start();
		}
	}

	public void interrupt(){
		for(Thread t : threads){
			t.interrupt();
		}
	}
}