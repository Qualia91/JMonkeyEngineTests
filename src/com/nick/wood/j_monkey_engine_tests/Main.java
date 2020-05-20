package com.nick.wood.j_monkey_engine_tests;

import com.jme3.app.*;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.AppSettings;
import org.lwjgl.openvr.VROverlay;

public class Main {


	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.put(VRConstants.SETTING_VRAPI, VRConstants.SETTING_VRAPI_OPENVR_LWJGL_VALUE);
		settings.put(VRConstants.SETTING_ENABLE_MIRROR_WINDOW, true);
		settings.put(VRConstants.SETTING_SEATED_EXPERIENCE, true);



		VREnvironment env = new VREnvironment(settings);
		env.initialize();

		// Checking if the VR environment is well initialized
		// (access to the underlying VR system is effective, VR devices are detected).
		if (env.isInitialized()){
			VRAppState vrAppState = new VRAppState(settings, env);
			vrAppState.setMirrorWindowSize(200, 200);

			VRGame vrGame = new VRGame(vrAppState);
			vrGame.setLostFocusBehavior(LostFocusBehavior.Disabled);
			vrGame.setSettings(settings);
			vrGame.setShowSettings(false);
			vrGame.start();

		}
	}

}
