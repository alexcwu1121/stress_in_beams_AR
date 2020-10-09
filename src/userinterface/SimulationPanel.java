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
import util.*;

/**JPanel which displays running simulations.<br>
This class will store simulations which are either provided at construction-time or with the addSimulation method.<br>
This class remembers the parameters of Simulations which are not currently running.<br>
As a consequence of this, setting a Simulation to running or not running and adding or removing the Simulation are distinct pairs of operations.<br>
Upon setting a Simulation to not running, the parameters of this Simulation are still stored, and the Simulation can be immediately set to running again, and
a simulation can only be set to running if the panel already has a copy of its parameters.<br>
Adding a Simulation will cause that Simulation's parameters to be stored, but the Simulation will not be running by default.<br>
Removing a Simulation will cause that Simulation's parameters to be deleted, and the Simulation must be added again before it is set to running.<br>
This class cannot store multiple different versions of the same Simulation class, and attempting do so will result in an IllegalArgumentException.
@author Owen Kulik
*/

public class SimulationPanel extends JPanel {
    //This is evil. The only reason that I'm allowing myself to do this is because it's not part of the class' public API.
	private Map<Class<? extends Simulation>, Pair<Boolean, Pair<Simulation, SimulationParameters>>> simulationInformation = new HashMap<>();;
	private Mat matrix;

    /**Constructs a SimulationPanel with the provided simulations.
    @param s the initial list of simulations to display.
    @throws NullPointerException if s is null or any value in s is null.
    */
    public SimulationPanel(Collection<SimulationParameters> defaultSimulationParameters){
        for(SimulationParameters params : defaultSimulationParameters){
            this.putSimulation(params);
        }
    }

    /**Constructs a mat for each simulation and stacks them in one image.
    @param results The detector results to use.
    @throws NullPointerException if results is null.
    */
    public void simulate(DetectorResults results){
        this.matrix = results.baseImage();
        for(Map.Entry<Class<? extends Simulation>, Pair<Boolean, Pair<Simulation, SimulationParameters>>> entry : this.simulationInformation.entrySet()){
            if(entry.getValue().first()){
                this.matrix = entry.getValue().second().first().run(results);
                results = new DetectorResults(this.matrix, results);
            }
        }
    }

    /**Adds the given Simulation to this SimulationPanel's collection of Simulations, using the given parameters a default values.
    @param defaultValues the default parameters for this Simulation.
    @throws IllegalArgumentException if a Simulation of the same type is already stored in this SimulationPanel.
    @throws NullPointerException if defaultValues is null.
    */
    public void addSimulation(SimulationParameters defaultValues){
        if(this.hasSimulation(defaultValues.getSimulationClass())){
            throw new IllegalArgumentException("Panel already has an instance of " + defaultValues.getSimulationClass().toString());
        }
        this.putSimulation(defaultValues);
    }

    /**Removes the given Simulation from this SimulationPanel's collection of Simulations. Has no effect if this panel does not have a Simulation of this type.
    @param simulationClass the Simulation type to remove.
    @throws NullPointerException if simulationClass is null.
    */
    public void removeSimulation(Class<? extends Simulation> simulationClass){
        this.simulationInformation.remove(simulationClass);
    }

    /**Returns whether this panel has the given Simulation class.
    @param simulationClass the Simulation type to remove.
    @throws NullPointerException if simulationClass is null.
    @return whether this panel has the given Simulation class.
    */
    public boolean hasSimulation(Class<? extends Simulation> simulationClass){
        return this.simulationInformation.containsKey(simulationClass);
    }

    /**Returns the current SimulationParamters object for the given simulation class, or null if this panel does not have a Simulation of the given type.
    @param simulationClass the Simulation type to get paramters of.
    @throws NullPointerException if simulationClass is null.
    @return the current SimulationParamters object for the given simulation class
    */
    public SimulationParameters getParametersForSimulation(Class<? extends Simulation> simulationClass){
        return this.hasSimulation(simulationClass) ? this.simulationInformation.get(simulationClass).second().second() : null;
    }

    /**Replaces the SimulationParameters for the class that the SimulationParameters represents.
    @param params the parameters
    @throws IllegalArgumentException if the panel does not have a Simulation of this type.
    @throws NullPointerException if params is null.
    */
    public void replaceParameters(SimulationParameters params){
        if(!this.hasSimulation(params.getSimulationClass())){
            throw new IllegalArgumentException("Panel does not have an instance of " + params.getSimulationClass().toString());
        }
        Pair<Boolean, Pair<Simulation, SimulationParameters>> pair = this.simulationInformation.get(params.getSimulationClass());
        Pair<Boolean, Pair<Simulation, SimulationParameters>> answer = Pair.makePair(pair.first(), Pair.makePair(params.getSimulation(), params));
        this.simulationInformation.put(params.getSimulationClass(), answer);
    }

    /**Sets whether the Simulation for the given class is running.
    @param simulationClass the class to consider
    @param running whether to run the simulation
    @throws IllegalArgumentException if the panel does not have a Simulation of this type.
    @throws NullPointerException if simulationClass is null.
    */
    public void setRunning(Class<? extends Simulation> simulationClass, boolean running){
        if(!this.hasSimulation(simulationClass)){
            throw new IllegalArgumentException("Panel does not have an instance of " + simulationClass.toString());
        }
        Pair<Boolean, Pair<Simulation, SimulationParameters>> pair = this.simulationInformation.get(simulationClass);
        Pair<Boolean, Pair<Simulation, SimulationParameters>> answer = Pair.makePair(running, pair.second());
        this.simulationInformation.put(simulationClass, answer);
    }

    /**Returns whether this simulation is currently running.
    @param simulationClass the class to consider
    @throws IllegalArgumentException if the panel does not have a Simulation of this type.
    @throws NullPointerException if simulationClass is null.
    @return whether this simulation is currently running.
    */
    public boolean getRunning(Class<? extends Simulation> simulationClass){
        if(!this.hasSimulation(simulationClass)){
            throw new IllegalArgumentException("Panel does not have an instance of " + simulationClass.toString());
        }
        return this.simulationInformation.get(simulationClass).first();
    }

    /**If the given simulation class is currently running, returns that running simulation. Otherwise returns null.
    @param simulationClass the class to consider
    @throws IllegalArgumentException if the panel does not have a Simulation of this type.
    @throws NullPointerException if simulationClass is null.
    @return the running simulation.
    */
    @SuppressWarnings("unchecked")
    public <T extends Simulation> T getRunningSimulation(Class<T> simulationClass){
        if(!this.hasSimulation(simulationClass)){
            throw new IllegalArgumentException("Panel does not have an instance of " + simulationClass.toString());
        }
        boolean running = this.getRunning(simulationClass);
        return (T)(running ? this.simulationInformation.get(simulationClass).second().first() : null);
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

    private void putSimulation(SimulationParameters params){
        this.simulationInformation.put(params.getSimulationClass(), Pair.makePair(false, Pair.makePair(params.getSimulation(), params.copy())));
    }
}