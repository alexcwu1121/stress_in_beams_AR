package userinterface;

import javax.swing.*;

@FunctionalInterface
public interface Skeleton<T extends JComponent, V> {
	T getComponent(V enactOn);
}