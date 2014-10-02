/**
 * 
 */
package cn.weathfold.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import cn.weathfold.critengine.CritEngine;
import cn.weathfold.critengine.scene.GUIScene;
import cn.weathfold.critengine.scene.Scene;

/**
 * ���ڲ��ԵĿͻ�������~
 * @author WeAthFolD
 *
 */
public class ClientRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display.setTitle("CritEngine Demo");
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		CritEngine.start(new GUIScene());
	}

}