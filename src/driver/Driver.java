package driver;

import concurrency.*;
import imageprocessing.*;
import configs.*;
import java.lang.Math;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;
import java.util.*;

public class Driver{
	/*
		1. Initialize videocap
		2. Spawn threads for 
	*/
	private static VideoCap webcam = new VideoCap();
    private static JFrame jframe;
	private static JPanel contentPane;
    private static Mat matrix;
	public static void setMatrix(Mat mat) {matrix = mat;}

	public static void main(String[] args) throws InterruptedException {
        jframe = new JFrame(){
            @Override
            public void paint(Graphics g){
                if(matrix != null){
                    g.drawImage(getImage(matrix), 0, 0, this);
                }
            }
        };
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setBounds(0, 0, 1280, 720);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        jframe.setContentPane(contentPane);
        contentPane.setLayout(null);
        jframe.setVisible(true);
        //jframe.setTitle(Integer.toString(id));

        PipelineBuilder builder = Pipeline.builder();

        PipelineFunction<Void, Mat> take_image = (t) -> {
            //System.out.println("Going to get an image");
    		Mat m = webcam.getOneFrame();
            //System.out.println("Got an image");
    		return m;
		};
        PipelineFunction<Mat, Mat> process_top = (t) -> {
            List<Mat> mats = matCollectionToList(t);
            assert mats.size() == 1 : "Incorrect number of mats.";
            Mat src = mats.get(0);
            Mat filtered = LineImpose.filterWithClose(src, 130, 180);
            Mat out = LineImpose.mask(src, filtered);
            Mat lines = LineImpose.findLines(out);
            //lines = LineFilter.removeLines(lines);

            /*setMatrix(filtered);
            jframe.repaint();*/
            //add src and lines
            /*Mat destination = new Mat();
            org.opencv.core.Core.merge(Arrays.asList(src, lines), destination);
            //LineImpose.draw(t, lines);*/
            return lines;
		};
		PipelineFunction<Mat, Mat> process_bot = (t) -> {
    		List<Mat> mats = matCollectionToList(t);
            assert mats.size() == 1 : "Incorrect number of mats.";
            Mat src = mats.get(0);
            Mat filtered = LineImpose.filterWithClose(src, 165, 175);
            Mat out = LineImpose.mask(src, filtered);
            Mat lines = LineImpose.findLines(out);
            lines = LineFilter.removeLines(lines);

            /*setMatrix(filtered);
            jframe.repaint();*/
            //add src and lines
            /*Mat destination = new Mat();
            org.opencv.core.Core.merge(Arrays.asList(src, lines), destination);
            //LineImpose.draw(t, lines);*/
            return lines;
		};
        PipelineFunction<Mat, Mat> dummy = (t) -> {
            List<Mat> mats = matCollectionToList(t);
            assert mats.size() == 1 : "Incorrect number of mats.";
            return mats.get(0);
        };
		PipelineFunction<Mat, Void> cast_image = (t) -> {
			List<Mat> mats = matCollectionToList(t);
            int originalIndex = -1;
            for(int i = 0; i < mats.size(); i++){
                if(mats.get(i).channels() == 3){
                    originalIndex = i;
                    break;
                }
            }
            assert originalIndex != -1 : "No orignal matrix received.";
            Mat original = mats.get(originalIndex);
            for(int i = 0; i < mats.size(); i++){
                if(i == originalIndex){
                    continue;
                }
                LineImpose.draw(original, mats.get(i));
            }
			setMatrix(original);
            jframe.repaint();
			return null;
		};
        Thread.UncaughtExceptionHandler handler = (th, ex) ->{
            ex.printStackTrace();
            System.exit(1);
        };
        builder.setFramerate(50);
        builder.setDefaultUncaughtExceptionHandler(handler);
        builder.addThreads(Void.class, Mat.class, NodeBehavior.REPLACING, take_image);
        builder.addThreads(Mat.class, Mat.class, NodeBehavior.BLOCKING, process_top, process_bot, dummy);
        builder.addThreads(Mat.class, Void.class, cast_image);
		Pipeline p = builder.build();
		Thread.sleep(1000);
		p.start();
	}

	public void paint(Graphics g){
        g = contentPane.getGraphics();
        g.drawImage(getImage(matrix), 0, 0, jframe);
    }

	/**
    Not sure what this does to be honest.
    */
    private static BufferedImage getSpace(Mat mat) {
        int type = 0;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int w = mat.cols();
        int h = mat.rows();
        BufferedImage img;
        img = new BufferedImage(w, h, type);
        return img;
    }

    /**
    Returns a BufferedImage representation of a mat.
    @param lined The mat to convert.
    @return A BufferedImage represonting the mat.
    */
    private static BufferedImage getImage(Mat lined){
        // Initialize global img to buffered image
        BufferedImage img = getSpace(lined);
        WritableRaster raster = img.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        lined.get(0, 0, data);
        return img;
    }

    private static List<Mat> matCollectionToList(Collection<? extends Mat> t){
        List<Mat> answer = new LinkedList<Mat>();
        for(Mat element : t){
            answer.add(element.clone());
        }
        return answer;
    }   
}