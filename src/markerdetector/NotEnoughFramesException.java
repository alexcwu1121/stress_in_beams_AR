package markerdetector;

/**Checked exception thrown by Calibrator objects when the calling method did not provide enough frames for calibration to occur.<br>
Instances of this class take int values representing the number of frames that the user captured and the number of frames that was required.<br>
These values can then be retrieved upon catching the exception.
@author Owen Kulik
*/

public class NotEnoughFramesException extends Exception{
	private final int framesCaptured;
	private final int framesRequired;

	/**Constructs a NotEnoughFramesException with the given frames captured and required values.
	@param framesCaptured the number of frames that were captured.
	@param framesRequired the number of frames required for calibration.
	@throws IllegalArgumentException if the frames captured or required values are negative.
	*/
	public NotEnoughFramesException(int framesCaptured, int framesRequired){
		this(framesCaptured, framesRequired, null);
	}

	/**Constructs a NotEnoughFramesException with the given frames captured and required values, and the given message.
	@param framesCaptured the number of frames that were captured.
	@param framesRequired the number of frames required for calibration.
	@param message the String exception message.
	@throws IllegalArgumentException if the frames captured or required values are negative.
	*/
	public NotEnoughFramesException(int framesCaptured, int framesRequired, String message){
		this(framesCaptured, framesRequired, message, null);
	}

	/**Constructs a NotEnoughFramesException with the given frames captured and required values, the given message, and the given cause.
	@param framesCaptured the number of frames that were captured.
	@param framesRequired the number of frames required for calibration.
	@param message the String exception message.
	@param cause the cause of this exception.
	@throws IllegalArgumentException if the frames captured or required values are negative.
	*/
	public NotEnoughFramesException(int framesCaptured, int framesRequired, String message, Throwable cause){
		super(message, cause);
		if(framesCaptured < 0 || framesRequired < 0){
			throw new IllegalArgumentException("Frames captured or required value was negative.");
		}
		this.framesCaptured = framesCaptured;
		this.framesRequired = framesRequired;
	}

	/**Returns this NotEnoughFramesException's number of frames captured.
	@return the framesCaptured value.
	*/
	public int framesCaptured(){
		return this.framesCaptured;
	}

	/**Returns this NotEnoughFramesException's number of frames required.
	@return the framesRequired value.
	*/
	public int framesRequired(){
		return this.framesRequired;
	}
}