package markerdetector;

import util.*;
import org.opencv.core.*;
import org.opencv.calib3d.*;

/**Class containing various utility methods for use in marker detection.
@author Owen Kulik
*/

public class MarkerUtils {
	private MarkerUtils(){}

	/**Converts the provided rvec and tvec into 3D coordinates from the camera's perspective.<br>
	Note that this function takes in 1x1 mats with three channels and returns 3x1 mats with one channel.<br>
	The return mats may be "wrong" in the sense that they do not work with Opencv library functions, this has not been tested yet.<br>
	If this is the case, it is an easy fix.
	@param rvec the rotation vector.
	@param tvec the translation vector.
	@throws NullPointerException if any parameter is null.
	@return A pair of mats. The first mat is the rotation vector, the second mat is the translation vector.
	*/
	public static Pair<Mat, Mat> get3DCoords(Mat rvec, Mat tvec){
		Mat r = new Mat();
		Mat translationVector3D = new Mat(3, 1, CvType.CV_64FC1);
		Mat rotationVector3D = new Mat();
		Calib3d.Rodrigues(rvec, r);
		r = r.t();
		double value1 = -((r.get(0, 0)[0] * tvec.get(0, 0)[0]) + (r.get(0, 1)[0] * tvec.get(0, 0)[1]) + (r.get(0, 2)[0] * tvec.get(0, 0)[2]));
		double value2 = -((r.get(1, 0)[0] * tvec.get(0, 0)[0]) + (r.get(1, 1)[0] * tvec.get(0, 0)[1]) + (r.get(1, 2)[0] * tvec.get(0, 0)[2]));
		double value3 = -((r.get(2, 0)[0] * tvec.get(0, 0)[0]) + (r.get(2, 1)[0] * tvec.get(0, 0)[1]) + (r.get(2, 2)[0] * tvec.get(0, 0)[2]));
		translationVector3D.put(0, 0, value1);
		translationVector3D.put(1, 0, value2);
		translationVector3D.put(2, 0, value3);
		Calib3d.Rodrigues(r, rotationVector3D);
		return new Pair<Mat, Mat>(rotationVector3D, translationVector3D);
	}

	/**Performs cross multiplication of the two matrices and returns the result.<br>
	The matrices must have the same number of channels.<br>
	The ith channel of the result matrix will contain the cross product of the ith channel of the first and second matrices.
	@throws NullPointerException if either argument is null.
	@throws IllegalArgumentException if first.cols() != second.rows() or if the matrices have different numbers of channels.
	@return the result of the cross multiplication.
	*/
	public static Mat crossMultiply(Mat first, Mat second){
		if(first.cols() != second.rows()){
			throw new IllegalArgumentException("First matrix columns and second matrix rows do not match.");
		}
		if(first.channels() != second.channels()){
			throw new IllegalArgumentException("Matrices had different numbers of channels.");
		}
		Mat answer = new Mat(first.rows(), second.cols(), first.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					for(int l = 0; l < first.cols(); l++){
						data[k] += first.get(i, l)[k] * second.get(l, j)[k];
					}
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

	/**Returns a copy of the provided mat, or null if the provided mat is null.
	@param m The mat to copy.
	@return a copy of the provided mat, or null if the provided mat is null.
	*/
	public static Mat copyof(Mat m){
		if(m == null){
			return null;
		}
		Mat answer = new Mat();
		m.copyTo(answer);
		return answer;
	}

	/**Prints the provided mat.<br> 
	Note that ", " indicates the next value in the row, whereas "->" indicates a value in the same column but in the next channel.
	@param m The mat to print.
	*/
	public static void printmat(Mat m){
		System.out.println(m);
		if(m == null){
			return;
		}
        for(int i = 0; i < m.rows(); i++){
            for(int j = 0; j < m.cols(); j++){
            	double[] data = m.get(i, j);
            	for(int k = 0; k < data.length; k++){
            		System.out.print(m.get(i, j)[k]);
            		if(k != data.length - 1){
            			System.out.print("->");
            		}
            	}
            	if(j != m.cols() - 1){
            		System.out.print(", ");
            	}
                
            }
            System.out.println();
        }
	}
}