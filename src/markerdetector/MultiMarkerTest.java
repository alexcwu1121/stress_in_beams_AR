package markerdetector;

import org.opencv.core.*;
import org.opencv.aruco.*;
import java.util.List;
import util.*;

public class MultiMarkerTest {
	private MultiMarkerTest(){}

	private static void testValues(List<Double> markervecs, List<Double> offsets, List<Double> expected){
		if(markervecs.size() != 6){
			throw new IllegalArgumentException();
		}
		if(offsets.size() != 6){
			throw new IllegalArgumentException();
		}
		if(expected.size() != 6){
			throw new IllegalArgumentException();
		}
		Mat base = new Mat();
		Dictionary dict = Aruco.getPredefinedDictionary(0);
		Mat ids = new Mat(1, 1, CvType.CV_32SC1);
		ids.put(0, 0, 0);
		List<Mat> corners = List.of(new Mat());
		List<Mat> rejected = List.of();
		Mat rotation = new Mat(1, 1, CvType.CV_64FC3);
		rotation.put(0, 0, markervecs.get(0), markervecs.get(1), markervecs.get(2));
		Mat translation = new Mat(1, 1, CvType.CV_64FC3);
		translation.put(0, 0, markervecs.get(3), markervecs.get(4), markervecs.get(5));
		DetectorResults fakeResults = new DetectorResults(base, dict, ids, corners, rejected, rotation, translation);

		MultiMarkerBody zero = new MultiMarkerBody(new MarkerOffset(0, offsets.get(0), offsets.get(1), offsets.get(2), offsets.get(3), offsets.get(4), offsets.get(5)));
		Pair<Mat, Mat> body = zero.predictCenter(fakeResults);

		Mat testRotation = new Mat(3, 1, CvType.CV_64FC1);
		Mat testTranslation = new Mat(3, 1, CvType.CV_64FC1);
		testRotation.put(0, 0, expected.get(0));
		testRotation.put(1, 0, expected.get(1));
		testRotation.put(2, 0, expected.get(2));
		testTranslation.put(0, 0, expected.get(3));
		testTranslation.put(1, 0, expected.get(4));
		testTranslation.put(2, 0, expected.get(5));
		MarkerUtils.printmat(body.first());
		MarkerUtils.printmat(body.second());
		MarkerUtils.printmat(testRotation);
		MarkerUtils.printmat(testTranslation);
		assertThat(MarkerUtils.matEquals(body.first(), testRotation, 0.1));
		assertThat(MarkerUtils.matEquals(body.second(), testTranslation, 0.1));
	}

	private static void assertThat(boolean statement){
		if(!statement){
			throw new AssertionError();
		}
	}

	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		testValues(List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
	}
}