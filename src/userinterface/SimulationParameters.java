package userinterface;

import java.util.*;
import simulation.*;
import java.lang.reflect.*;
import util.*;

/**Class representing the necessary data to construct a particular Simulation.<br>
This class can be used to construct eligible Simulations is a programmatic way.<br>
In order for a simulation to be eligible, it must meet the following requirements:<br>
<ol>
	<li>Have at least one public constructor which is not marked with the @internal annotation.</li>
</ol>
SimulationParameters is given a Class<? extends Simulation> at construction-time.<br>
SimulationParameters will then find the first public constructor of this class which is not marked with the @internal annotation.<br>
It will parse the paramters of this constructor and interpret them as the Simulation's parameters.<br>
Calls to the getParameterTypes method will return the types of the constructor's arguments.<br>
All parameters are initially marked as UNINITIALIZED.<br>
Other classes can then "fill in" these parameters with calls to the fillInParameter and fillInAllParameters methods.<br>
Once all paramters have been filled in, getSimulation can be called.<br>
This method will feed these parameters into the Simulation's constructor and return the resulting Simulation.<br>
*/

public class SimulationParameters {
	private final Class<? extends Simulation> simulation;
	private final List<Parameter> parameterTypes;
	private final List<Object> parameters;
	private Constructor<? extends Simulation> constructor;

	/**Value assigned to uninitialized parameters.<br>
	This value can be compared with the == operator.
	*/
	public static final Object UNINITIALIZED = new Uninitialized();

	//Used by the copy method.
	private SimulationParameters(Class<? extends Simulation> sim, List<Parameter> types, List<Object> params, Constructor<? extends Simulation> cons){
		this.simulation = sim;
		this.parameterTypes = types;
		this.parameters = params;
		this.constructor = cons;
	}

	/**Constructs a SimulationParameters class from the given class.
	@param simulation the Simulation class to base this SimulationParameters instance on.
	@throws IllegalArgumentException if the simulation does not have any public constructors which are not marked with the @internal annotation.
	*/
	@SuppressWarnings("unchecked")
	public SimulationParameters(Class<? extends Simulation> simulation){
		this.simulation = simulation;
		Constructor<?>[] constructors = simulation.getConstructors();
		for(Constructor<?> constructor : constructors){
			if(constructor.getAnnotation(Internal.class) == null){
				this.constructor = (Constructor<? extends Simulation>)constructor;
			}
		}
		if(this.constructor == null){
			throw new IllegalArgumentException(simulation.getSimpleName() + " has no non-internal constructors.");
		}
		this.parameterTypes = Arrays.asList(this.constructor.getParameters());
		this.parameters = new ArrayList<Object>();
		for(int i = 0; i < parameterTypes.size(); i++){
			this.parameters.add(UNINITIALIZED);
		}
	}

	/**Fills in the parameter at the given index (0-indexed) with the given Object.<br>
	If the parameter at the given index has already been filled in, replaces the value of that parameter.
	@param index the index of parameter to fill in
	@param value the value to fill in
	@throws IllegalArgumentException if index < 0 or index >= # of parameters, or value does not match the object type of the paramter at the given index.
	*/
	public void fillInParameter(int index, Object value){
		if(index < 0 || index >= this.parameterTypes.size()){
			throw new IllegalArgumentException("Index " + index + " out of range for " + this.parameterTypes.size() + " parameters.");
		}
		if(!assignable(this.parameterTypes.get(index).getType(), value.getClass())){
			throw new IllegalArgumentException(value.getClass().toString() + " is not assignable to " + parameterTypes.get(index).getType().toString());
		}
		this.parameters.set(index, value);
	}

	/**Fills in all parameters with the values in the given List.
	@param values the values to use
	@throws NullPointerException if values is null
	@throws IllegalArgumentException if values.size() != # of parameters or any value does not match the object type of the parameter at its index.
	*/
	public void fillInAllParameters(List<Object> values){
		if(values == null){
			throw new NullPointerException();
		}
		if(values.size() != this.parameterTypes.size()){
			throw new IllegalArgumentException("Number of provided values " + values.size() + " differs from expected number " + this.parameterTypes.size());
		}
		for(int i = 0; i < values.size(); i++){
			this.fillInParameter(i, values.get(i));
		}
	}

	/**Returns the Parameter class representing the parameter at the given index.
	@param index the parameter index
	@throws IllegalArgumentException if if index < 0 or index >= # of parameters
	@return the Parameter class representing the parameter at the given index.
	*/
	public Parameter getParameterType(int index){
		return this.parameterTypes.get(index);
	}

	/**Returns Parameter classes representing all parameters in this SimulationParameters.
	@return Parameter classes representing all parameters in this SimulationParameters.
	*/
	public List<Parameter> getAllParameterTypes(){
		return List.copyOf(this.parameterTypes);
	}

	/**Returns the current value of the parameter at the given index, or UNINITIALIZED if it has not been initialized yet.
	@param index the parameter index
	@throws IllegalArgumentException if if index < 0 or index >= # of parameters
	@return the current value of the parameter at the given index.
	*/
	public Object getParameter(int index){
		return this.parameters.get(index);
	}

	/**Returns all the current values of all parameters as a List.
	@return the current value of the parameter at the given index.
	*/
	public List<Object> getAllParameters(){
		return new ArrayList<Object>(this.parameters);
	}

	/**Returns the class that this SimulationParameters refers to.
	@return the class that this SimulationParameters refers to.
	*/
	public Class<? extends Simulation> getSimulationClass(){
		return this.simulation;
	}

	/**Returns the number of parameters in this SimulationParameters.
	@return the number of parameters in this SimulationParameters.
	*/
	public int numberOfParameters(){
		return this.parameterTypes.size();
	}

	/**Constructs and returns the Simulation using the parameter values currently held by this SimulationParameters.<br>
	This method passes on any exceptions thrown by the Simulation's constructor.
	@throws IllegalStateException if any parameters are still UNINITIALIZED.
	@throws RuntimeException if the Simulation's constructor throws an exception.
	@return the constructed Simulation.
	*/
	public Simulation getSimulation(){
		for(Object o : this.parameters){
			if(o == UNINITIALIZED){
				throw new IllegalStateException("A parameter was uninitialized.");
			}
		}
		try{
			return this.constructor.newInstance(this.parameters.toArray(new Object[0]));
		} catch(ReflectiveOperationException e){
			throw new RuntimeException("Underlying constructor threw an exception.", e);
		}
	}

	/**Returns a copy of this SimulationParamters.<br>
	Note: This method returns a shallow copy. As such, this may produce problems if Simulation objects produced have representation exposure.
	@return a copy of this SimulationParamters.
	*/
	public SimulationParameters copy(){
		return new SimulationParameters(this.simulation, new ArrayList<Parameter>(this.parameterTypes), new ArrayList<Object>(this.parameters), this.constructor);
	}

	/**Returns a hash code for this SimulationParamters.
	@return a hash code for this SimulationParamters.
	*/
	@Override
	public int hashCode(){
		return this.simulation.hashCode() + this.parameters.hashCode();
	}

	@Override
	/**Returns a boolean indicating whether this SimulationParamters is equal to the given object.<br>
	This method will return true if the given object is a SimulationParamters which represents the same Simulation class and has the same parameters.
	@param o The object to compare to
	@return a boolean indicating whether this SimulationParamters is equal to the given object.
	*/
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof SimulationParameters)){
			return false;
		}
		SimulationParameters sp = (SimulationParameters)o;
		return this.simulation.equals(sp.simulation) && this.parameters.equals(sp.parameters);
	}

	@Override
	/**Returns a String representation of this SimulationParamters.
	@return a String representation of this SimulationParamters.
	*/
	public String toString(){
		return "Parameters for " + this.simulation.getSimpleName() + ": " + this.parameters.toString();
	}

	private static boolean assignable(Class<?> first, Class<?> second){
		if(first.isAssignableFrom(second)){
			return true;
		}
		if (first.equals(Integer.class) || first.equals(int.class)) {
            return second.equals(Integer.class) || second.equals(int.class);
        } else if (first.equals(Float.class) || first.equals(float.class)) {
            return second.equals(Float.class) ||  second.equals(float.class);
        } else if (first.equals(Double.class) || first.equals(double.class)) {
            return second.equals(Double.class) ||  second.equals(double.class);
        } else if (first.equals(Character.class) || first.equals(char.class)) {
            return second.equals(Character.class) ||  second.equals(char.class);
        } else if (first.equals(Long.class) || first.equals(long.class)) {
            return second.equals(Long.class) ||  second.equals(long.class);
        } else if (first.equals(Short.class) || first.equals(short.class)) {
            return second.equals(Short.class) ||  second.equals(short.class);
        } else if (first.equals(Boolean.class) || first.equals(boolean.class)) {
            return second.equals(Boolean.class) ||  second.equals(boolean.class);
        } else if (first.equals(Byte.class) || first.equals(byte.class)) {
            return second.equals(Byte.class) ||  second.equals(byte.class);
        }
        return false;
        //return true;
	}

	private static class Uninitialized{
		@Override
		public String toString(){
			return "UNINITIALIZED";
		}
	}
}