package com.nick.wood.j_monkey_engine_tests;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

import java.util.ArrayList;

public class Terrain {

	private TerrainQuad terrain;
	Material mat_terrain;

	public Terrain(AssetManager assetManager, Camera camera, Node rootNode, Quaternion rotation) {
		/** 1. Create terrain material and load four textures into it. */
		//mat_terrain = new Material(assetManager,
		//		"Common/MatDefs/Terrain/TerrainLighting.j3md");

		mat_terrain = new Material(assetManager,
				"Common/MatDefs/Terrain/Terrain.j3md");

		/** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
		//mat_terrain.setTexture("Alpha", assetManager.loadTexture(
		//		"Textures/Terrain/splat/alphamap.png"));
		mat_terrain.setTexture("Alpha", assetManager.loadTexture(
				"Textures/earthHeightMapSplat-0-0-39-28.png"));

		/** 1.2) Add GRASS texture into the red layer (Tex1). */
		Texture grass = assetManager.loadTexture(
				"Textures/red.jpg");
		grass.setWrap(Texture.WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 1);
		/** 1.3) Add DIRT texture into the green layer (Tex2) */
		Texture dirt = assetManager.loadTexture(
				"Textures/green.jpg");
		dirt.setWrap(Texture.WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 2);
		/** 1.4) Add ROAD texture into the blue layer (Tex3) */
		Texture rock = assetManager.loadTexture(
				"Textures/blue.jpg");
		rock.setWrap(Texture.WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 3);

		/** 1.2) Add GRASS texture into the red layer (Tex1). */
		//Texture grass = assetManager.loadTexture(
		//		"Textures/earthPic-0-0-39-28.png");
		//grass.setWrap(Texture.WrapMode.EdgeClamp);
		//grass.setMagFilter(Texture.MagFilter.Bilinear);
		//mat_terrain.setTexture("DiffuseMap", grass);

		/** 2. Create the height map */
		AbstractHeightMap heightmap = null;
		Texture heightMapImage = assetManager.loadTexture(
				"Textures/earthHeightMapFull-0-0-39-28.png");
		heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		//heightmap.setHeightScale(25);
		heightmap.load();

		/** 3. We have prepared material and heightmap.
		 * Now we create the actual terrain:
		 * 3.1) Create a TerrainQuad and name it "my terrain".
		 * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
		 * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
		 * 3.4) As LOD step scale we supply Vector3f(1,1,1).
		 * 3.5) We supply the prepared heightmap itself.
		 */
		int patchSize = 65;
		terrain = new TerrainQuad("my terrain", patchSize, 65, heightmap.getHeightMap());


		/** 4. We give the terrain its material, position & scale it, and attach it. */
		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, 0, 0);
		//terrain.setLocalScale(7827.148438f, 1, 7827.148438f);
		//terrain.setLocalScale(1, 1, 1);
		//terrain.setLocalScale(122.2991943359375f, 1, 122.2991943359375f);
		terrain.setLocalRotation(rotation);
		rootNode.attachChild(terrain);

		/** 5. The LOD (level of detail) depends on were the camera is: */
		TerrainLodControl control = new TerrainLodControl(terrain, camera);
		terrain.addControl(control);
	}
}