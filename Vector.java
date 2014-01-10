/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.OpenCVFrameGrabber;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import java.io.Serializable;

/**
 *
 * @author XXX
 */
public class Vector implements Serializable {

    private int index;
    private int glintX;
    private int glintY;
    private int pupilX;
    private int pupilY;
    private double vectorX;
    private double vectorY;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getGlintX() {
        return glintX;
    }

    public void setGlintX(int glintX) {
        this.glintX = glintX;
    }

    public int getGlintY() {
        return glintY;
    }

    public void setGlintY(int glintY) {
        this.glintY = glintY;
    }

    public int getPupilX() {
        return pupilX;
    }

    public void setPupilX(int pupilX) {
        this.pupilX = pupilX;
    }

    public int getPupilY() {
        return pupilY;
    }

    public void setPupilY(int pupilY) {
        this.pupilY = pupilY;
    }

    public double getVectorX() {
        return vectorX;
    }

    public void setVectorX(double vectorX) {
        this.vectorX = vectorX;
    }

    public double getVectorY() {
        return vectorY;
    }

    public void setVectorY(double vectorY) {
        this.vectorY = vectorY;
    }
}
