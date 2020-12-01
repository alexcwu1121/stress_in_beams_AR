/**Contains the application's user interface. What follows are tutorials on how to use the GUI and the classes in this package.<br>

I just wrote a Simulation class. How do I get it on the GUI?<br>
You have to follow a couple of steps.
The GUI will get a copy of your Simulation by using a particular constructor in that Simulation.<br>
The first step is to choose which constructor the GUI will use.<br>
There must be an Option class for each of the parameter types. A list of Option classes can be found in UserInterfaceUtils.java.<br>
In addition, each parameter type must meet the requirements listed below.<br>
Once you have chosen the constructor to use, mark all other constructors in the class with the @Internal annotation. This signals to the GUI not to use them.<br>
Additionally, consider marking the paramters of the constructor with the @Description annotation, which will be the name that the paramter is displayed with on the GUI.<br>
(An example of this can be found in the SimpleSimulation class.)<br>

Next, edit the config/eligibleSimulations.json file to add the name of your simulation's class to the array. 
The class name must be fully-qualified (ie for a class MySimulation in package simulation, the fully qualified name is simulation.MySimulation.)<br>

Finally, the GUI needs to know default values for your Simulation's parameters. The final step is to specify these default values.<br>
This is done by making a default value config file.<br>
Go into the config folder and create a JSON file whose name is the same as your class (case-sensitive) with ".json" on the end.<br>
List the default values you'd like to use for the constructor in a JSON array.<br>
Since the default values are read from a JSON file, they must be representable in JSON. To satisfy this, the type must either:
<ol>
	<li>Be representable as a JSON literal. The types representable as a JSON literal are int, long, double, boolean, String, JSONObject and JSONArray.</li>
	<li>Have a constructor which takes JSONObject as its only parameter. This constructor will be used to construct the value.</li>
	<li>Have a static method called fromJSONObject which takes JSONObject as its only parameter and returns the parameter type or a subclass of the parameter type.
	This method will be called, and the returned value will be passed to the simulation.</li>
	<li>In addition to methods in the class, the user can specify an alternate parsing method in the config. This is useful for classes which are already written.
	How to do this is desribed below.</li>
</ol>
If multiple of these options are present, they are prioritized in the following order: Literal interpretation, specified parse method, fromJSONObject method, JSONObject constructor.
Once you have completed all of these steps, your simulation will display on the GUI.<br> 

To specify an alternate parsing method for a class, you must add key called "parseMethod" to the parameter. This key should point to a JSONObject with two keys:
"class", the fully-qualified name of the class which contains the alternate parsing method, and "method", the name of the alternate parsing method.<br>
Within the parameter, a key called "data" will point to JSON data passed to the parse method.<br>
For example, to use a method called fromJSON in class package.Example, the parameter would look like this: 
{"parseMethod" : {"class" : package.Example, "method" : fromJSON}, "data" : {...data which is passed to the parse method...}}
<br>

The rest of this package-info talks about the architecture of the userinterface package, and how to extend it.<br> <br>

The Skeleton Interface<br>
The Skeleton interface allows classes to statically specify GUI components in a single line of code.<br>
The principle behind the interface is that the specification must be static - therefore all instances of a GUI class should share a single copy of the same skeleton, 
and JComponent instances returned by the skeleton should not interfere with each other.<br>
The idea is that an implementing class of Skeleton will take in all of its options when it is constructed, 
then return identical components each time the getComponent method is called.<br>
Already included in this package are a few classes that implement Skeleton.<br>
If one of the Skeleton classes already satisfies your need, simply use that class.<br>
<br>
How do I make a Skeleton class?<br>
The typical Skeleton class looks something like this:<br>
{@code
public class MySkeleton<V> implements Skeleton<AComponent, V>{
	private final ... //Private final fields

	public MySkeleton(...){
		//Initialize fields
	}

	public AComponent getComponent(V enactOn){
		AComponent comp = new AComponent();
		//Initialize the component's settings
		return comp;
	}
}
}
<br>
What is the purpose of the V type parameter?<br>
The V type parameter may be used by implementing classes to perform type-specific operations such as adding StaticActionListeners with the same type parameter.<br>
If this type parameter is not needed, implementing classes may declare it with type Object and provide null to the getComponent method.<br>
<br>
Should I make my own skeleton class?<br>
Using a Skeleton class is always better than copying boilerplate code several times.<br>
If you're going to use the same GUI component several times, and there isn't currently a skeleton class for that component, then you should make a Skeleton class.<br>
If you're only using a certain component once, then you should still consider making a Skeleton class. It may come in handy later.<br>
<br>
There's already a skeleton class for the component I want, but it doesn't have enough options. What do I do?<br>
In this case, you have two options. The first is to define a new constructor for the Skeleton class which takes the required extra options.<br>
If you choose to define a new constructor, then make sure not to modify any existing constructors, as this could break existing code.<br>
The second option is to define a subclass of the already existing Skeleton. This is only recommended if the class itself will be a private internal class, as defining
multiple public Skeletons for the same JComponent would be confusing.<br>
For an example of this design pattern, see the StateMenuItemSkeleton class inside the StrengthsGUI class.<br>
<br>
<br>
The Option class and related classes<br>

The Option class is used to statically specify an application option.<br>
It has two type parameters. The first one, Q, represents the type of the option.<br>
The second one, V, represents the type of object that the option is being read from and written to.<br>
The Option class works by taking two functions at construction time: a reader and an enactor.<br>
The reader specifies how the option's current value can be read given the object that it reads from.<br>
The enactor specifies how to change the option's value given the new value and the object to enact on.<br>
The Option class itself is abstract; we'll talk later about how to create a concrete sublcass of Option.<br>
<br>
For now, suppose you have a boolean field called {@code setting} in class {@code GUIClass} which you want to make into an Option.<br>
Since the field is a boolean, you decide to use CheckboxOption.<br>
The Option declaration would look like this:<br>
{@code CheckboxOption<GUIClass> option = new CheckboxOption("name", "message", false, (gui) -> {
	return gui.setting;
}, (value, gui) -> {
	gui.setting = value;
});}
<br>
How do I write an Option subclass?<br>
Each option is represented to the user as a GUI component. For example, an option representing a boolean might be represented as a JCheckBox.<br>
To allow different option types to be represented as different components, the Option class has an abstract method called getEvaluator().<br>
This method returns a class of type OptionEvaluator.<br> 
The OptionEvaluator interface has two methods: getComponent(), which returns the component for the option, and evaluate(),
which returns the current option given the state of the evaluator component.<br>
Using the boolean option as an example, getComponent() would return the JCheckBox, and evaluate() would return whether the checkbox is checked.<br>
An example of how to write the getEvaluator() method is as follows, where OptionValue is the type of the option:<br>
{@code
public OptionEvaluator<OptionValue> getEvaluator(OptionValue currentValue){
	return new OptionEvaluator<OptionValue>(){
		private JComponent component;

		{
			//Set up the component
		}

		@Override
		public JComponent getComponent(){
			return this.component;
		}

		@Override
		public OptionValue evaluate(){
			//evaluate the component and return the current value.
		}
	};
}
}<br>
What is the InstanceOption class used for?<br>
The InstanceOption class stores an Option, OptionEvaluator, and an object that the Option applies to.<br>
InstanceOption remedies the fact that successive calls to the Option.getEvaluator() method return separate evaluators.<br>
Additionally, InstanceOption is useful in cases when options are being stored in a Collection object or being passed into a method,
 as otherwise there would be no guarantee that the object the option applies to is the same type that the option is written for.<br>
Methods that take in several Options should use the InstanceOption class for simplicity.
*/

package userinterface;