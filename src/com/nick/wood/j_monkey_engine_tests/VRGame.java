package com.nick.wood.j_monkey_engine_tests;

import com.jme3.app.*;
import com.jme3.app.state.AppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.vr.VRViewManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.*;
import com.jme3.scene.plugins.ogre.MeshLoader;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.jme3.util.VRGUIPositioningMode;
import com.jme3.util.VRGuiManager;
import com.nick.wood.hla_sim.game.RenderObject;
import com.nick.wood.hla_sim.hla.HlaListener;
import com.nick.wood.hla_sim.hla.HlaSendControl;
import com.nick.wood.hla_sim.hla.HlaSim;
import com.nick.wood.maths.objects.vector.Vec3f;
import cs.generatedcode.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VRGame extends SimpleApplication {

	private final VRAppState vrAppState;
	private HlaSim hlaSim;
	private HlaListener hlaListener;
	private HashMap<String, Geometry> uuidSceneGraphHashMap = new HashMap<>();
	private ArrayList<RenderObject> lastFrameRenderObjects = new ArrayList<>();
	private HlaSendControl hlaSendControl;
	private CameraNode cameraNode;

	final static com.jme3.math.Quaternion cameraInitialRotationX = new com.jme3.math.Quaternion().fromAngleNormalAxis(FastMath.PI/2.0f, Vector3f.UNIT_X);
	final static com.jme3.math.Quaternion cameraInitialRotationY = new com.jme3.math.Quaternion().fromAngleNormalAxis(FastMath.PI/2.0f, Vector3f.UNIT_Y);
	final static com.jme3.math.Quaternion cameraInitialRotation = cameraInitialRotationX.mult(cameraInitialRotationY);
	private Geometry leftHand;
	private Geometry rightHand;

	public VRGame(AppState... appStates) {
		super(appStates);
        this.vrAppState = (VRAppState) appStates[0];
	}

	@Override
	public void simpleInitApp() {

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);

		// hand wands
		leftHand = (Geometry)getAssetManager().loadModel("Models/vive_controller.j3o");
		rightHand = leftHand.clone();
		Material handMat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		handMat.setTexture("ColorMap", getAssetManager().loadTexture("Textures/vive_controller.png"));
		leftHand.setMaterial(handMat);
		rightHand.setMaterial(handMat);
		rootNode.attachChild(rightHand);
		rootNode.attachChild(leftHand);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/fakeLighting.j3md");
		mat.setColor("Color", ColorRGBA.Gray);

		//Box floor = new Box(1024, 1024, 100);
		//Geometry geometry = new Geometry("Floor", floor);
		//geometry.setLocalTranslation(-512, -512, 0);
		//geometry.setMaterial(mat);
		//rootNode.attachChild(geometry);

		vrAppState.setFrustrumNearFar(10, 1_000_000f);

		Spatial sky = SkyFactory.createSky(getAssetManager(), "Textures/Skysphere.jpg", SkyFactory.EnvMapType.EquirectMap);
		sky.setLocalRotation(cameraInitialRotation);
		rootNode.attachChild(sky);

		Terrain terrain = new Terrain(assetManager, getCamera(), rootNode, cameraInitialRotation);
		//DefaultTerrain terrain = new DefaultTerrain(assetManager, getCamera(), rootNode, cameraInitialRotation);

		this.cameraNode = new CameraNode("Camera", getCamera());

		cameraNode.setLocalRotation(cameraInitialRotation);
		//cameraNode.setLocalTranslation(0, 0, 10000);
		cameraNode.setLocalTranslation(0, 0, 50);
		rootNode.attachChild(cameraNode);

		//Spatial playerModel = assetManager.loadModel("Models/cockpitSimpleOne.obj");
		//Material hudMat = new Material(assetManager, "Common/MatDefs/Misc/fakeLighting.j3md");
		//hudMat.setColor("Color", ColorRGBA.Gray);
		//hudMat.getAdditionalRenderState().setDepthTest(false);
		//playerModel.setMaterial(hudMat);
		//playerModel.setLocalTranslation(0, 0, 6096);
		//rootNode.attachChild(playerModel);


		try {
			this.hlaListener = new HlaListener(1,
					1,
					"D:\\Software\\Programming\\projects\\pitchHLA\\rpr\\",
					"Fed",
					"Federation",
					60);
			this.hlaSendControl = new HlaSendControl(hlaListener);
			this.hlaSim = new HlaSim(hlaListener, () -> {
				Spatial playerModel = assetManager.loadModel("Models/cockpitSimpleOne.obj");
				JMEViewObject jmeViewObject = new JMEViewObject(playerModel);
				jmeViewObject.getGeom().setMaterial(mat);
				return jmeViewObject;
			});

			initKeys();

		} catch (HlaRtiException e) {
			e.printStackTrace();
		} catch (HlaInvalidLogicalTimeException e) {
			e.printStackTrace();
		} catch (HlaFomException e) {
			e.printStackTrace();
		} catch (HlaNotConnectedException e) {
			e.printStackTrace();
		} catch (HlaConnectException e) {
			e.printStackTrace();
		} catch (HlaInternalException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Custom Keybinding: Map named actions to inputs.
	 */
	private void initKeys() {
		// You can map one or several inputs to one named action
		inputManager.addMapping("w", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("s", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("a", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("d", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("q", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping("e", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
		AnalogListener movementListener = (name, value, tpf) -> {
			switch (name) {
				case "w":
					hlaSendControl.forwardLinear();
					break;
				case "s":
					hlaSendControl.backLinear();
					break;
				case "a":
					hlaSendControl.leftLinear();
					break;
				case "d":
					hlaSendControl.rightLinear();
					break;
				case "q":
					hlaSendControl.upLinear();
					break;
				case "e":
					hlaSendControl.downLinear();
					break;
				case "up":
					hlaSendControl.upPitch();
					break;
				case "down":
					hlaSendControl.downPitch();
					break;
				case "left":
					hlaSendControl.leftRoll();
					break;
				case "right":
					hlaSendControl.rightRoll();
					break;
				case "space":
					hlaSendControl.action();
					break;
				default:
					break;
			}
		};
		inputManager.addListener(movementListener, "w", "s", "a", "d",  "q", "e", "up", "down", "left", "right", "space");

	}

	private void handleWandInput(int index, Geometry geo) {
		if (vrAppState.getVRinput() == null) return;
		Quaternion q = vrAppState.getVRinput().getFinalObserverRotation(index);
		Vector3f v = vrAppState.getVRinput().getFinalObserverPosition(index);
		if (q != null && v != null) {
			geo.setCullHint(Spatial.CullHint.Dynamic); // make sure we see it
			//geo.setLocalTranslation(cameraNode.getLocalTranslation().mult(v));
			//geo.setLocalRotation(cameraNode.getLocalRotation().mult(q));
			geo.setLocalTranslation(v);
			geo.setLocalRotation(q);
		}
	}

	@Override
	public void simpleUpdate(float tpf) {

		handleWandInput(0, leftHand);
		handleWandInput(1, rightHand);

		ArrayList<RenderObject> thisFrameRenderObjects = hlaSim.getGERenderObjects();

		for (RenderObject renderObject : thisFrameRenderObjects) {
			renderObject.updateView();
			JMEViewObject jmeViewObject = (JMEViewObject) renderObject.getView();
			if (!rootNode.getChildren().contains(jmeViewObject.getGeom())) {
				if (renderObject.getEntityIdentifierStruct().entityNumber == 0 && renderObject.getEntityIdentifierStruct().federateIdentifier.applicationID == 2) {
					jmeViewObject.attachCamera(cameraNode);
				}
				rootNode.attachChild(jmeViewObject.getGeom());
			}
		}

		// remove destroyed render objects
		for (RenderObject lastFrameRenderObject : lastFrameRenderObjects) {
			if (!thisFrameRenderObjects.contains(lastFrameRenderObject)) {
				JMEViewObject jmeViewObject = (JMEViewObject) lastFrameRenderObject.getView();
				rootNode.getChildren().remove(jmeViewObject.getGeom());
				if (lastFrameRenderObject.getEntityIdentifierStruct().entityNumber == 0 && lastFrameRenderObject.getEntityIdentifierStruct().federateIdentifier.applicationID == 2) {
					jmeViewObject.attachCamera(null);
				}
			}
		}

		lastFrameRenderObjects = thisFrameRenderObjects;

	}

	@Override
	public void simpleRender(RenderManager rm) {


	}
}
