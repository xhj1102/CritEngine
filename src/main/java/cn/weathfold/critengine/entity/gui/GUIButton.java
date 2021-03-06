/**
 * 
 */
package cn.weathfold.critengine.entity.gui;

import cn.weathfold.critengine.scene.Scene;

/**
 * @author WeAthFolD
 *
 */
public abstract class GUIButton extends GUIComponent {

	public GUIButton(Scene scene, double x, double y, double width, double height) {
		super(scene, x, y, width, height);
		this.guiListener.addKeyListening(-100);
	}
	
	public abstract void onButtonPressed();
	public abstract void onButtonFrame();
	public abstract void onButtonReleased();
	
	@Override
	protected void onKeyDown(int kid) {
		if(!isMouseInEntity())
			return;
		onButtonPressed();
	}
	
	@Override
	protected final void onKeyFrame(int kid) {
		if(!isMouseInEntity())
			return;
		onButtonFrame();
	}
	
	@Override
	protected final void onKeyUp(int kid) {
		if(!isMouseInEntity())
			return;
		onButtonReleased();
	}

}
