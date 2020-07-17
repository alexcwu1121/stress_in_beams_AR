package userinterface;

import javax.swing.*;
import java.awt.event.*;

public class MenuItemSkeleton<V> implements Skeleton<JMenuItem, V>{
	private final String message;
	private final StaticActionListener<V> listener;
	private final KeyStroke accelerator;
	private final int mnemonic;

	public MenuItemSkeleton(String message, StaticActionListener<V> listener){
		this(message, listener, null, -1);
	}

	public MenuItemSkeleton(String message, StaticActionListener<V> listener, KeyStroke accelerator, int mnemonic){
		this.message = message;
		this.listener = listener;
		this.accelerator = accelerator;
		this.mnemonic = mnemonic;
	}

	public JMenuItem getComponent(V enactOn){
		JMenuItem item = new JMenuItem(this.message);
		item.addActionListener((action) -> this.listener.actionPerformed(action, enactOn));
		if(this.accelerator != null){
			item.setAccelerator(this.accelerator);
		}
		if(this.mnemonic != -1){
			item.setMnemonic(this.mnemonic);
		}
		return item;
	}
}