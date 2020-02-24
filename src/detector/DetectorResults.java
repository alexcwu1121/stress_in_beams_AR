package detector;

public class DetectorResults {
	private final Dictionary dict;
	private final List<Mat> corners;
	private final Mat ids;
	private final List<Mat> rejected;
	private final Mat rotationVectors;
	private final Mat translationVectors;

	DetectorResults(Dictionary dict, List<Mat> corners, Mat ids, List<Mat> rejected, Mat rotationVectors, Mat translationVectors){}

	public MarkerInformation getMarkerInformation(int id){}

}