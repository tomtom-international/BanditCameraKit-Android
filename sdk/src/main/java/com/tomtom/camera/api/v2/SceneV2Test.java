package com.tomtom.camera.api.v2;

import com.google.gson.Gson;
import com.tomtom.camera.api.model.Scene;

import junit.framework.TestCase;

/**
 * Created by licina on 1/22/16.
 */
public class SceneV2Test extends TestCase {

    public static final String MODE_UNDERWATER = "{\n" +
            "        \"mode\": \"underwater\",\n" +
            "        \"brightness\": 128,\n" +
            "        \"contrast\": 128,\n" +
            "        \"hue\": 128,\n" +
            "        \"saturation\": 180,\n" +
            "        \"sharpness\": 128\n" +
            "     }\n";

    public static final String MODE_AUTO = "    {\n" +
            "        \"mode\": \"auto\",\n" +
            "            \"brightness\": 128,\n" +
            "            \"contrast\": 128,\n" +
            "            \"hue\": 128,\n" +
            "            \"saturation\": 128,\n" +
            "            \"sharpness\": 128\n" +
            "    }\n";

    public static final String MODE_BRIGHT = "    {\n" +
            "        \"mode\": \"bright\",\n" +
            "            \"brightness\": 128,\n" +
            "            \"contrast\": 140,\n" +
            "            \"hue\": 128,\n" +
            "            \"saturation\": 140,\n" +
            "            \"sharpness\": 150\n" +
            "    }\n";

    public static final String MODE_NIGHT = "    {\n" +
            "        \"mode\": \"night\",\n" +
            "            \"brightness\": 128,\n" +
            "            \"contrast\": 128,\n" +
            "            \"hue\": 128,\n" +
            "            \"saturation\": 110,\n" +
            "            \"sharpness\": 117\n" +
            "    }\n";


    public void testSceneParsing() {
        Scene scene = new Gson().fromJson(MODE_UNDERWATER, SceneV2.class);
        assertEquals(scene.getSceneMode(), Scene.Mode.UNDERWATER);
        assertEquals(scene.getBrightness(), 128);
        assertEquals(scene.getContrast(), 128);
        assertEquals(scene.getHue(), 128);
        assertEquals(scene.getSaturation(), 180);
        assertEquals(scene.getSharpness(), 128);

        scene = new Gson().fromJson(MODE_AUTO, SceneV2.class);
        assertEquals(scene.getSceneMode(), Scene.Mode.AUTO);
        assertEquals(scene.getBrightness(), 128);
        assertEquals(scene.getContrast(), 128);
        assertEquals(scene.getHue(), 128);
        assertEquals(scene.getSaturation(), 128);
        assertEquals(scene.getSharpness(), 128);

        scene = new Gson().fromJson(MODE_BRIGHT, SceneV2.class);
        assertEquals(scene.getSceneMode(), Scene.Mode.BRIGHT);
        assertEquals(scene.getBrightness(), 128);
        assertEquals(scene.getContrast(), 140);
        assertEquals(scene.getHue(), 128);
        assertEquals(scene.getSaturation(), 140);
        assertEquals(scene.getSharpness(), 150);

        scene = new Gson().fromJson(MODE_NIGHT, SceneV2.class);
        assertEquals(scene.getSceneMode(), Scene.Mode.NIGHT);
        assertEquals(scene.getBrightness(), 128);
        assertEquals(scene.getContrast(), 128);
        assertEquals(scene.getHue(), 128);
        assertEquals(scene.getSaturation(), 110);
        assertEquals(scene.getSharpness(), 117);
    }
}
