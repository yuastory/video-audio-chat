package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import common.SharedPreferences;

public class Mat2Image
{
    static
    {
        SharedPreferences.setLibPath();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    Mat mat = new Mat();

    BufferedImage getImage(Mat frame)
    {
        // Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else if (frame.channels() == 3)
            type = BufferedImage.TYPE_3BYTE_BGR;
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);
        return image;
    }

}