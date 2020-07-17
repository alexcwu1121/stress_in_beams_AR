package userinterface;

import javax.swing.*;

public interface OptionEvaluator<Q>{
	JComponent getComponent();

	Q evaluate();
}