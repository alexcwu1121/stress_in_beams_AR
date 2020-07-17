package userinterface;

import javax.swing.*;
import org.json.*;

public abstract class Option<Q, V>{
	private final String name;
	private final String message;
	private final Q defaultValue;
	private final Enactor<Q, V> enactor;
	private final Reader<Q, V> reader;

	public Option(String name, String message, Q defaultValue, Reader<Q, V> reader, Enactor<Q, V> enactor){
		this.name = name;
		this.message = message;
		this.defaultValue = defaultValue;
		this.enactor = enactor;
		this.reader = reader;
	}

	public abstract OptionEvaluator<Q> getEvaluator();

	public abstract void addToJSONObject(JSONObject obj);

	public abstract Q readFromJSONObject(JSONObject obj);

	public Q read(V readFrom){
		return this.reader.read(readFrom);
	}

	public void enact(Q value, V enactOn){
		this.enactor.enact(value, enactOn);
	}

	public void resetToDefault(V enactOn){
		this.enact(this.defaultValue, enactOn);
	}

	public String getName(){
		return this.name;
	}

	public String getMessage(){
		return this.message;
	}

	public Q getDefaultValue(){
		return this.defaultValue;
	}
}