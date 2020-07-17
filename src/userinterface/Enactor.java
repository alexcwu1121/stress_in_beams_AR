package userinterface;

@FunctionalInterface
public interface Enactor<Q, V>{
	void enact(Q value, V enactOn);
}