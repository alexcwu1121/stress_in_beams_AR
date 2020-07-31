package markerdetector;

public class NotEnoughFramesException extends Exception{
	private final int framesCaptured;
	private final int framesRequired;

	public NotEnoughFramesException(int framesCaptured, int framesRequired){
		this(framesCaptured, framesRequired, null);
	}

	public NotEnoughFramesException(int framesCaptured, int framesRequired, String message){
		this(framesCaptured, framesRequired, message, null);
	}

	public NotEnoughFramesException(int framesCaptured, int framesRequired, String message, Throwable cause){
		super(message, cause);
		this.framesCaptured = framesCaptured;
		this.framesRequired = framesRequired;
	}

	public int framesCaptured(){
		return this.framesCaptured;
	}

	public int framesRequired(){
		return this.framesRequired;
	}
}