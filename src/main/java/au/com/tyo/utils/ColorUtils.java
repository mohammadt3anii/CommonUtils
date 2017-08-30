package au.com.tyo.utils;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 29/8/17.
 */

public class ColorUtils {

    public static int getIntFromColor(List<Float> color) {
        float[] array = new float[color.size()];
        for (int i = 0; i < array.length; ++i)
            array[i] = color.get(i);
        return getIntFromColor(array);
    }

    public static int getIntFromColor(float[] color) {
        float r, g, b, alpha;
        r = color[0];
        g = color[1];
        b = color[2];

        if (color.length == 3)
            alpha = 1;
        else
            alpha = color[3];
        return getIntFromColor(r, g, b, alpha);
    }

    public static int getIntFromColor(float red, float green, float blue, float alpha){
        int R = Math.round(255 * red);
        int G = Math.round(255 * green);
        int B = Math.round(255 * blue);
        int alphaInt = Math.round(255 * alpha);

        alphaInt = (alphaInt << 24) & 0xFF000000;
        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return alphaInt | R | G | B;
    }

    public static String toHexString(int color) {
        return String.format("#%08X", (0xFFFFFFFF & color));
    }
}
