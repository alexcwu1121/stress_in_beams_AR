package userinterface;

import org.opencv.core.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.Dimension;
import markerdetector.*;
import driver.*;
import java.util.*;
import java.awt.Graphics;

public class SimulationPanel extends JPanel {
	private List<Simulation> simulations;
	private Mat matrix;

	{
        //this.setBorder(new EmptyBorder(0, 0, 0, 0));
        //this.setLayout(null);
	}

    public SimulationPanel(List<Simulation> s){
        this.simulations = List.copyOf(s);
    }

    /**Constructs a mat for each simulation and stacks them in one image
    @param results The detector results to use.
    */
    public void simulate(DetectorResults results){
        this.matrix = new Mat();
        for(Simulation simulation : this.simulations){
            this.matrix = simulation.run(results);
            results = new DetectorResults(this.matrix, results);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return this.matrix == null ? new Dimension(0, 0) : new Dimension(this.matrix.cols(), this.matrix.rows());
    }

    @Override
	public void paintComponent(Graphics g){
        super.paintComponent(g);
		if(this.matrix != null && this.matrix.rows() != 0 && this.matrix.cols() != 0){
            g.drawImage(getImage(this.matrix), 0, 0, this);
        }
	}

	private static BufferedImage matToBufferedImage(Mat mat) {
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
        BufferedImage img = matToBufferedImage(lined);
        WritableRaster raster = img.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        lined.get(0, 0, data);
        return img;
    } 
}