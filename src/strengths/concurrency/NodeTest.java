package concurrency;

public class NodeTest{
	static int total = 0;
	static int mistakes = 0;
	public static void main(String[] args) throws InterruptedException {
		PipelineBuilder builder = Pipeline.builder();
		
		PipelineFunction<Void, String> firstLambdaPt1 = (t) -> {
			return "ran";
		};
		PipelineFunction<Void, String> firstLambdaPt2 = (t) -> {
			return "successfully";
		};
		PipelineFunction<String, Integer> secondLambdaPt1 = (t) -> {
			StringBuilder sb = new StringBuilder();
			for(String s : t){
				sb.append(s + " ");
			}
			//System.out.println(t);
			if(sb.length() != 17){
				System.out.println(sb);
			}
			//System.out.println(sb.length());
			return sb.length();
		};
		PipelineFunction<String, Integer> secondLambdaPt2 = (t) -> {
			StringBuilder sb = new StringBuilder();
			for(String s : t){
				sb.append(s + " bruh moment ");
			}
			//System.out.println(t);
			if(sb.length() != 41){
				System.out.println(sb);
			}
			return sb.length();
		};
		PipelineFunction<Integer, Void> thirdLambda = (t) -> {
			int length = 0;
			//System.out.println(t.get(0));
			for(Integer i : t){
				length += i;
			}
			System.out.println(t);
			if(length != 58){
				mistakes++;
			}
			total++;
			return null;
		};

		Thread.UncaughtExceptionHandler logAndContinue = (th, ex) -> {
			ex.printStackTrace();
		};
		Thread.UncaughtExceptionHandler logAndExit = (th, ex) -> {
			ex.printStackTrace();
			System.exit(0);
		};
		builder.setDefaultUncaughtExceptionHandler(logAndContinue);
		builder.addThreads(Void.class, String.class, firstLambdaPt1, firstLambdaPt2);
		builder.addThreads(String.class, Integer.class, logAndExit, secondLambdaPt1, secondLambdaPt2);
		builder.addThreads(Integer.class, Void.class, thirdLambda);
		Pipeline p = builder.build();
		Thread.sleep(1000);
		p.start();
		Thread.sleep(110);
		p.interrupt();
		System.out.println((double)mistakes/total);
	}	
}