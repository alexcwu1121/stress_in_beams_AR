package driver;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;
import org.opencv.core.*;
import java.awt.Graphics;

/**Simple JFrame class which runs a simulation.
*/

public class SimulationFrame extends JFrame {
	private Simulation simulation;
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

	/**Constructs a SimulationFrame using the given simulation.
	@param s The simulation to use.
	*/
	public SimulationFrame(Simulation s){
		simulation = s;
	}

	public void simulate(Mat base, Mat rotationMatrix, Mat translationMatrix){
		matrix = simulation.run(base, rotationMatrix, translationMatrix);
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

