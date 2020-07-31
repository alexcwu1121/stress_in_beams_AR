package driver;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;
import org.opencv.core.*;
import java.awt.Graphics;
import markerdetector.*;
import simulation.*;
import java.util.*;

/**Simple JFrame class which runs a simulation.
*/

public class SimulationFrame extends JFrame {
	private List<Simulation> simulations;
	private JPanel contentPane;
	private Mat matrix;

	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1280, 720);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        setVisible(true);
	}

	/**Constructs a SimulationFrame using the given simulations.
	@param s The simulation to use.
	*/
	public SimulationFrame(Simulation... s){
		this(List.of(s));
	}

    public SimulationFrame(List<Simulation> s){
        this.simulations = List.copyOf(s);
    }

    /**Generates a mat for each simulation and merges them
    @param results The detector results to use.
    */
    public void simulateSeparate(DetectorResults results){
        this.matrix = new Mat();
        List<Mat> mats = new LinkedList<Mat>();
        for(Simulation simulation : this.simulations){
            mats.add(simulation.run(results));
        }
        Core.hconcat(mats, this.matrix);
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

	public void paint(Graphics g){
		if(matrix != null){
            g.drawImage(getImage(matrix), 0, 0, this);
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

