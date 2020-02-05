package concurrency;

public class InterchangeTest{
	public static void main(String[] args) throws InterruptedException {
		PipelineBuilder builder = Pipeline.builder();
		PipelineFunction<Void, String> firstLambdaPt1 = (t) -> {
			return "ran ";
		};
		PipelineFunction<Void, String> firstLambdaPt2 = (t) -> {
			return "successfully";
		};
		PipelineFunction<String, Void> secondLambda = (t) -> {
			StringBuilder sb = new StringBuilder();
			for(String s : t){
				sb.append(s);
			}
			System.out.println(sb);
			return null;
		};
		builder.addThreads(Void.class, String.class, firstLambdaPt1, firstLambdaPt2).addThreads(String.class, Void.class, secondLambda);
		Pipeline p = builder.build();
		p.start();
		Thread.sleep(2000);
		p.interrupt();
	}	
}