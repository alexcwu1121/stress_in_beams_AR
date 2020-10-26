package userinterface;

import javax.swing.*;
import java.awt.event.*;

/**Skeleton class returning a JMenuItem object.<br>
At construction time, this class takes a message, a StaticActionListener representing the action that will be taken when this menu item is clicked,<br>
as well as an optional keyboard shortcut and an optional mnemonic.<br>
When the getComponent method is called, a new JMenuItem is constructed.<br>
This JMenuItem will have a single ActionListener, which will take the provided V type and apply the StaticActionListener to it.<br>
Specifically, the button will have the action listener {@code (action) -> staticListener.actionPerformed(action, enactOn)}<br> 
where staticListener is the StaticActionLIstner and enactOn is the provided V type.
@author Owen Kulik
*/

public class MenuItemSkeleton<V> implements Skeleton<JMenuItem, V>{
	private final String message;
	private final StaticActionListener<V> listener;
	private final KeyStroke accelerator;
	private final int mnemonic;

	/**Constructs a MenuItemSkeleton with the given message and StaticActionListener, and without a keyboard shortcut or mnemonic.
	@param message the menu item's message. null value indicates no message.
	@param listener the action that will be performed on clicking the menu item. null value indicates that no action will be performed.
	*/
	public MenuItemSkeleton(String message, StaticActionListener<V> listener){
		this(message, listener, null, -1);
	}

	/**Constructs a MenuItemSkeleton with the given message, StaticActionListener, keyboard shortcut and mnemonic.
	@param message the menu item's message. null value indicates no message.
	@param listener the action that will be performed on clicking the menu item. null value indicates that no action will be performed.
	@param accelerator the keyboard shortcut. null value indicates no shortcut.
	@param mnemonic the keyboard mnemonic. A value of -1 indicates no mnemonic.
	*/
	public MenuItemSkeleton(String message, StaticActionListener<V> listener, KeyStroke accelerator, int mnemonic){
		this.message = message;
		this.listener = listener;
		this.accelerator = accelerator;
		this.mnemonic = mnemonic;
	}

	/**Constructs and returns a JMenuItem from this skeleton.
	@param enactOn the object that menu item's action listeners will be run on.
	@return a JMenuItem from this skeleton.
	*/
	public JMenuItem getComponent(V enactOn){
		JMenuItem item = new JMenuItem(this.message);
		if(this.listener != null){
			item.addActionListener((action) -> this.listener.actionPerformed(action, enactOn));
		}
		if(this.accelerator != null){
			item.setAccelerator(this.accelerator);
		}
		if(this.mnemonic != -1){
			item.setMnemonic(this.mnemonic);
		}
		return item;
	}
}