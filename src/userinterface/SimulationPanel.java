package userinterface;

import org.opencv.core.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.Dimension;
import markerdetector.*;
import driver.*;
import simulation.*;
import java.util.*;
import java.awt.Graphics;

/**JPanel which displays running simulations.<br>
This class can run multiple simulations at once. These simulations are layered on top of one another.<br>
If no simulations are provided, simply draws any provided frames.
@author Owen Kulik
*/

public class SimulationPanel extends JPanel {
	private List<Simulation> simulations;
	private Mat matrix;

    /**Constructs a SimulationPanel with the provided simulations.
    @param s the initial list of simulations to display.
    @throws NullPointerException if s is null or any value in s is null.
    */
    public SimulationPanel(List<Simulation> s){
        this.simulations = new LinkedList<Simulation>(List.copyOf(s));
    }

    /**Constructs a mat for each simulation and stacks them in one image.
    @param results The detector results to use.
    @throws NullPointerException if results is null.
    */
    public void simulate(DetectorResults results){
        this.matrix = results.baseImage();
        for(Simulation simulation : this.simulations){
            this.matrix = simulation.run(results);
            results = new DetectorResults(this.matrix, results);
        }
    }

    /**Returns a set of class objects representing every simulation that is currently running.
    @return the set.
    */
    public Set<Class<? extends Simulation>> getRunningSimulations(){
        Set<Class<? extends Simulation>> answer = new HashSet<Class<? extends Simulation>>();
        for(Simulation simulation : this.simulations){
            answer.add(simulation.getClass());
        }
        return answer;
    }

    /**Adds this simulation to this panel's running simulations.
    @param s the simulation to add
    @throws NullPointerException if s is null.
    */
    public void addSimulation(Simulation s){
        if(s == null){
            throw new NullPointerException();
        }
        List<Simulation> simulations = new LinkedList<Simulation>(this.simulations);
        simulations.add(s);
        this.simulations = simulations;
    }

    /**Removes all simulations of the given class type from this SimulationPanel. Has no effect if cl is null.
    @param cl the class to remove simulations of.
    */
    public void removeSimulation(Class<? extends Simulation> cl){
        List<Simulation> simulations = new LinkedList<Simulation>(this.simulations);
        for(int i = 0; i < simulations.size(); i++){
            if(simulations.get(i).getClass() == cl){
                simulations.remove(i);
                i--;
            }
        }
        this.simulations = simulations;
    }

    /**Returns this panel's preferred size, which is the size of the most recently provided input matrix.
    @return this panel's preferred size.
    */
    @Override
    public Dimension getPreferredSize() {
        return this.matrix == null ? new Dimension(0, 0) : new Dimension(this.matrix.cols(), this.matrix.rows());
    }

    /**Paints the most recent simulation results on the panel.
    @param g the graphics object to use.
    @throws NullPointerException if g is null.
    */
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