package com.nick.wood.j_monkey_engine_tests;

import com.jme3.math.*;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.nick.wood.hla_sim.game.ViewObject;
import com.nick.wood.maths.objects.Quaternion;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.UUID;

public class JMEViewObject implements ViewObject {

	private final Spatial geom;
	private CameraNode cameraNode = null;

	public JMEViewObject(Spatial playerModel) {
		this.geom = playerModel;
	}

	public Spatial getGeom() {
		return geom;
	}

	@Override
	public void update(Vec3f newPos, Quaternion newOri) {
		geom.setLocalTranslation(newPos.getX(), newPos.getY(), newPos.getZ());
		com.jme3.math.Quaternion quaternion = new com.jme3.math.Quaternion((float) newOri.getJ(), (float) newOri.getK(), (float) newOri.getI(), (float) newOri.getS());
		geom.setLocalRotation(VRGame.cameraInitialRotation.mult(quaternion));
		if (cameraNode != null) {
			cameraNode.setLocalRotation(VRGame.cameraInitialRotation.mult(quaternion));
			cameraNode.setLocalTranslation(newPos.getX(), newPos.getY(), newPos.getZ());
		}
	}

	@Override
	public Object getID() {
		return geom.getName();
	}

	public void attachCamera(CameraNode cameraNode) {
		this.cameraNode = cameraNode;
	}

}
