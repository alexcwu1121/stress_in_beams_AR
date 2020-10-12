package userinterface;

/**Class which stores a SimulationParameters and a boolean. The boolean represents whether the Simulation is running, but has no immediate effect.
@author Owen Kulik
*/

public class OptionalSimulationParameters{
	private boolean running;
	private SimulationParameters params;

	/**Constructs an OptionalSimulationParameters from the given values.
	@param running Whether the Simulation is running
	@param params The simulation parameters. Cannot be null.
	@throws NullPointerException if params is null
	*/
	public OptionalSimulationParameters(boolean running, SimulationParameters params){
		if(params == null){
			throw new NullPointerException();
		}
		this.running = running;
		this.params = params;
	}

	/**Returns whether this simulation is running.
	@return whether this simulation is running.
	*/
	public boolean isRunning(){
		return this.running;
	}

	/**Returns the parameters stored in this OptionSimulationParameters.
	@return the parameters stored in this OptionSimulationParameters.
	*/
	public SimulationParameters getParameters(){
		return this.params;
	}

	/**Sets whether this simulation is running.
	@param running Whether the simulation is running.
	*/
	public void setRunning(boolean running){
		this.running = running;
	}

	/**Sets the SimulationParameters.
	@param params SimulationParameters to set to. Cannot be null.
	@throws NullPointerException if params is null.
	*/
	public void setParameters(SimulationParameters params){
		if(params == null){
			throw new NullPointerException();
		}
		this.params = params;
	}

	/**Returns a deep copy of this OptionSimulationParamters.<br>
	This method uses the SimulationParameters.copy() method to copy the SimulationParameters, meaning that they are shallow-copied.
	@return a deep copy of this OptionSimulationParamters.
	*/
	public OptionalSimulationParameters copy(){
		return new OptionalSimulationParameters(this.running, this.params.copy());
	}

	/**Return a hash code for this OptionSimulationParameters
	@return a hash code for this OptionSimulationParameters
	*/
	@Override
	public int hashCode(){
		return (running ? 1 : 0) + params.hashCode();
	}

	/**Returns a boolean indicating whether the given object is equal to this OptionalSimulationParameters.<br>
	They will be considered equal if the running boolean is the same and the parameters are equal.
	@param o the object to compare to.
	@return a boolean indicating whether the given object is equal to this OptionalSimulationParameters.
	*/
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof OptionalSimulationParameters)){
			return false;
		}
		OptionalSimulationParameters osp = (OptionalSimulationParameters)o;
		return this.running == osp.running && this.params.equals(osp.params);
	}

	/**Returns a String representation of this OptionalSimulationParameters.
	@return a String representation of this OptionalSimulationParameters.
	*/
	@Override
	public String toString(){
		return "userinterface.OptionalSimulationParameters: " + (running ? "Running" : "Not running") + " simulation with params " + this.params.toString();
	}
}