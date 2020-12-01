package userinterface;

import javax.swing.*;
import java.io.*;

/**Option class used for Files.<br>
This option is represented as a JButton, which, when pressed, opens a JFileChooser.
@author Owen Kulik
*/

public class FileOption<V> extends Option<File, V>{
	/**Constructs a FileOption from the given values.<br> 
	@param name the option's name. Can be null.
	@param message the option's message. This is what is displayed to users explaining what the option is, it should be human-readable. Can be null.
	@param defaultValue the option's default value. Can NOT be null.
	@param reader the function which reads an option's current value.
	@param enactor the function which enacts an option value.
	@throws NullPointerException if reader, enactor or DefaultValue is null.
	*/
	public FileOption(String name, String message, File defaultValue, Reader<File, V> reader, Enactor<File, V> enactor){
		super(name, message, defaultValue, reader, enactor);
	}

	/**Returns an OptionEvaluator for this FileOption.<br>
	The OptionEvaluator's getComponent method will always return an instance of JButton.<br>
	This button will open a JFileChooser upon being clicked.<br>
	The OptionEvaluator's evaluate method will return the file selected in the JFileChooser. It will never return null.
	@param currentValue the initial value for the JTextField.
	@throws NullPointerException if currentValue is null.
	@return an OptionEvaluator for this FileOption.
	*/
	public OptionEvaluator<File> getEvaluator(File currentValue){
		if(currentValue == null){
			throw new NullPointerException();
		}
		return new OptionEvaluator<File>(){
			private JButton button;
			private JFileChooser chooser;
			private File chosen;

			{
				this.button = new JButton("Choose file...");
				this.chooser = new JFileChooser();
				this.chooser.setSelectedFile(currentValue);
				this.chosen = currentValue;

				this.button.addActionListener((action) -> {
					int returnVal = this.chooser.showDialog(null, "Choose");
					if(returnVal == JFileChooser.APPROVE_OPTION){
						this.chosen = chooser.getSelectedFile();
					}
				});
			}

			@Override
			public JComponent getComponent(){
				return this.button;
			}

			@Override
			public File evaluate(){
				return this.chosen;
			}
		};
	}
}