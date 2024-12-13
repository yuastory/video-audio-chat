package video;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

import common.SharedPreferences;

public class VideoCap
{
    static
    {
        SharedPreferences.setLibPath();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public VideoCapture cap;
    Mat2Image           mat2Img = new Mat2Image();

    public VideoCap()
    {
        cap = new VideoCapture();
        cap.open(0);
    }

    public synchronized BufferedImage getOneFrame()
    {
        cap.read(mat2Img.mat);

        return mat2Img.getImage(mat2Img.mat);
    }
}