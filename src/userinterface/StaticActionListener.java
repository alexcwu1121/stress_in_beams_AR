package userinterface;

import java.awt.*;
import java.awt.event.*;

@FunctionalInterface
public interface StaticActionListener<V>{
	void actionPerformed(ActionEvent action, V enactOn);
}