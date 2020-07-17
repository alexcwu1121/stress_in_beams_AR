package userinterface;

@FunctionalInterface
public interface Reader<Q, V>{
	Q read(V readFrom);
}