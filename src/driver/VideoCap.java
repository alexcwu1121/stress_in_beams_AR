package driver;

import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.core.*;
/**
Small class which allows us to capture video from the webcam.

@author Nick i think 
@since 10/9/19
*/


public class VideoCap {

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private VideoCapture cap;

    /**
    Sets up the class to capture from the camera specified by cameraId.
    @param cameraId The ID of the camera to capture from.
    */
    public VideoCap(int cameraId){
        cap = new VideoCapture();
        cap.open(cameraId);
    }

    /**
    Sets up the class to capture from the camera with ID 0.
    Calling this constructor is exactly the same as calling VideoCap(0).
    */
    public VideoCap(){
        this(0);
    }

    /**
    Reads a frame from the camera and returns it in a Mat.
    @return Image taken from the camera, represented as a Mat.
    */
    public Mat getOneFrame() {
        Mat dst = new Mat();
        cap.read(dst);
        return dst;
    }
    
    /**
    Reads the frame rate from the camera and returns it.
    @return The integer frame rate of the camera.
    */
    public int getFrameRate() {
		return (int) cap.get(Videoio.CAP_PROP_FPS);
    }

    /**
    Reads the width of the camera feed and returns it.
    @return The integer width of the camera.
    */
	public int getFrameWidth() {
		return (int) cap.get(Videoio.CAP_PROP_FRAME_WIDTH);
	}
	
    /**
    Reads the height of the camera feed and returns it.
    @return The integer height of the camera.
    */
	public int getFrameHeight() {
		return (int) cap.get(Videoio.CAP_PROP_FRAME_HEIGHT);
	}
}