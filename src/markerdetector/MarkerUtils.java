package markerdetector;

import util.*;
import org.opencv.core.*;
import org.json.*;

/**Class containing various utility methods for use in marker detection.
@author Owen Kulik
*/

//TODO: add dot product and scalar product methods, add putSafe

public class MarkerUtils {
	private MarkerUtils(){}

	/**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are exactly equal, or if both mats are null.
	@return a boolean indicating whether the mats are exactly equal.
	*/
	public static boolean matEquals(Mat first, Mat second){
		return matEquals(first, second, 0.0);
	}

	/**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are equal within the given tolerance, or if both mats are null.
	@return a boolean indicating whether the mats are equal within the given tolerance.
	*/
	public static boolean matEquals(Mat first, Mat second, double tolerance){
		if(first == null && second == null){
			return true;
		}
		if(first == second){
			return true;
		}
		if(first.rows() != second.rows() || first.cols() != second.cols() || first.channels() != second.channels()){
			return false;
		}
		for(int i = 0; i < first.rows(); i++){
			for(int j = 0; j < first.cols(); j++){
				for(int k = 0; k < first.channels(); k++){
					if(Math.abs(first.get(i, j)[k] - second.get(i, j)[k]) > tolerance){
						return false;
					}
				}
			}
		}
		return true;
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

	/**Returns the dot product of the given mats.
	@param first the first matrix
	@param second the second matrix
	@throws NullPointerException if any parameter is null.
	@throws IllegalArgumentException if first and second are different sizes.
	@return the dot product of the given mats.
	*/
	public static Mat dotMultiply(Mat first, Mat second){
		if(first.rows() != second.rows() || first.cols() != second.cols() || first.channels() != second.channels()){
			throw new IllegalArgumentException("Matrix dimensions did not match.");
		}
		Mat answer = new Mat(first.rows(), first.cols(), first.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					data[k] = first.get(i, j)[k] * second.get(i, j)[k];
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

	/**Multiples the given mat by the given scalar value and returns the result.
	@throws NullPointerExcpetion if any parameter is null.
	@return the result of the scalar multiplication.
	*/
	public static Mat scalarMultiply(Mat mat, double scalar){
		Mat answer = new Mat(mat.rows(), mat.cols(), mat.type());
		for(int i = 0; i < answer.rows(); i++){
			for(int j = 0; j < answer.cols(); j++){
				double[] data = new double[answer.channels()];
				for(int k = 0; k < answer.channels(); k++){
					data[k] = mat.get(i, j)[k] * scalar;
				}
				answer.put(i, j, data);
			}
		}
		return answer;
	}

	/**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are exactly equal, or if both mats are null.
	@return a boolean indicating whether the mats are exactly equal.
	*/
	public static boolean matEquals(Mat first, Mat second){
		return matEquals(first, second, 0.0);
	}

	/**Returns true if and only if the given matrices are the same dimensions and 
	each value of the given matrices are equal within the given tolerance, or if both mats are null.
	@return a boolean indicating whether the mats are equal within the given tolerance.
	*/
	public static boolean matEquals(Mat first, Mat second, double tolerance){
		if(first == null && second == null){
			return true;
		}
		if(first == second){
			return true;
		}
		if(first.rows() != second.rows() || first.cols() != second.cols() || first.channels() != second.channels()){
			return false;
		}
		for(int i = 0; i < first.rows(); i++){
			for(int j = 0; j < first.cols(); j++){
				for(int k = 0; k < first.channels(); k++){
					if(Math.abs(first.get(i, j)[k] - second.get(i, j)[k]) > tolerance){
						return false;
					}
				}
			}
		}
		return true;
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
		System.out.println(MarkerUtils.matToString(m));
	}

	/**Returns a String representation of the provided Mat, including all data within the mat.
	@return a String representation of the provided Mat.
	*/
	public static String matToString(Mat m){
		if(m == null){
			return "null";
		}
		StringBuilder sb = new StringBuilder(m.toString());
		sb.append("\n");
        for(int i = 0; i < m.rows(); i++){
            for(int j = 0; j < m.cols(); j++){
            	double[] data = m.get(i, j);
            	for(int k = 0; k < data.length; k++){
            		sb.append(m.get(i, j)[k]);
            		if(k != data.length - 1){
            			sb.append("->");
            		}
            	}
            	if(j != m.cols() - 1){
            		sb.append(", ");
            	}
                
            }
            sb.append("\n");
        }
        return sb.toString();
	}

	/**Converts a mat to a JSON format which can be read back by jsonToMat.<br> 
	Note that this function is currently only capable of storing mats with one channel.
	@param m The mat to store.
	@throws NullPointerException if m is null.
	@return a JSONObject representing the given mat.
	*/
	public static JSONObject matToJson(Mat m){
        JSONObject answer = new JSONObject();
        answer.put("type", m.type());
        answer.put("rows", m.rows());
        answer.put("cols", m.cols());
        JSONArray data = new JSONArray();
        for(int i = 0; i < m.rows(); i++){
            for(int j = 0; j < m.cols(); j++){
                data.put(m.get(i, j)[0]);
            }
        }
        answer.put("data", data);
        return answer;
    }

    /**Parses and returns a mat from the given JSON object.<br>
    The JSON object must have the format of objects created by the matToJson function.<br>
    Note that these functions are only capable of parsing mats with one channel.
    @param obj the JSON object
    @throws NullPointerException if obj is null
    @throws JSONException if the JSON object is missing a field
    @return the parsed mat.
    */
    public static Mat jsonToMat(JSONObject obj){
        int rows = obj.getInt("rows");
        int columns = obj.getInt("cols");
        int type = obj.getInt("type");
        Mat answer = new Mat(rows, columns, type);
        JSONArray ja = obj.getJSONArray("data");
        for(int i = 0; i < ja.length(); i++){
            answer.put(i/columns, i%columns, ja.getDouble(i));
        }
        return answer;
    }
}