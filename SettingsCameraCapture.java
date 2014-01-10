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
import static com.googlecode.javacv.cpp.opencv_photo.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

/**
 *
 * @author XXX
 */
public class SettingsCameraCapture extends Thread {

    private int glintBlockSize;
    private int glintDif;
    private int glintDylate;
    private int glintSmooth;
    private int pupilErode;
    private int pupilSmooth;
    private int pupilBlockSize;
    private int pupilDif;
    private int pupilMaxSize;
    private int pupilMinSize;
    private int inpaintSize;
    private int brightPupil = 1;
    private IplImage img;
    private CvPoint glintCord;
    private CvPoint pupilCord;
    private int vector;
    private int vector2;

    public void run() {
        cameraCapture();
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBrightPupil(int brightPupil) {
        this.brightPupil = brightPupil;
    }

    public void setInpaintSize(int inpaintSize) {
        this.inpaintSize = inpaintSize;
    }

    public void setGlintBlockSize(int glintBlockSize) {
        this.glintBlockSize = glintBlockSize;
    }

    public void setGlintDif(int glintDif) {
        this.glintDif = glintDif;
    }

    public void setGlintDylate(int glintDylate) {
        this.glintDylate = glintDylate;
    }

    public void setGlintSmooth(int glintSmooth) {
        this.glintSmooth = glintSmooth;
    }

    public void setPupilErode(int pupilErode) {
        this.pupilErode = pupilErode;
    }

    public void setPupilSmooth(int pupilSmooth) {
        this.pupilSmooth = pupilSmooth;
    }

    public void setPupilBlockSize(int pupilBlockSize) {
        this.pupilBlockSize = pupilBlockSize;
    }

    public void setPupilDif(int pupilDif) {
        this.pupilDif = pupilDif;
    }

    public void setPupilMaxSize(int pupilMaxSize) {
        this.pupilMaxSize = pupilMaxSize;
    }

    public void setPupilMinSize(int pupilMinSize) {
        this.pupilMinSize = pupilMinSize;
    }

    private void cameraCapture() {

        CvCapture capture = cvCreateCameraCapture(0);
        CvMemStorage storage = CvMemStorage.create();
        CvMemStorage storage2 = CvMemStorage.create();
        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FPS, 14);
        img = opencv_highgui.cvQueryFrame(capture);

        CanvasFrame frame = new CanvasFrame("Webcam");
        CanvasFrame frame2 = new CanvasFrame("Glint");
        CanvasFrame frame3 = new CanvasFrame("Pupil");
        CanvasFrame frame4Test = new CanvasFrame("Inpaint");

        IplImage gray = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        IplImage gray2 = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        IplImage grayCopy = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        IplImage mask = cvCreateImage(cvGetSize(gray), 8, 1);




        while (frame.isVisible() && (img = opencv_highgui.cvQueryFrame(capture)) != null) {

            CvSeq contours = new CvContour(null);
            CvSeq contours2 = new CvContour(null);

            cvCvtColor(img, gray, CV_BGR2GRAY);
            cvCvtColor(img, gray2, CV_BGR2GRAY);
            cvSmooth(gray, gray, CV_BLUR, glintSmooth);
            cvErode(gray, gray, null, glintDylate);

            cvAdaptiveThreshold(gray, gray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, glintBlockSize, glintDif);
            cvFindContours(gray, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
            cvClearMemStorage(storage);


            if (!contours.isNull()) {

                CvSeq ptr = new CvSeq();
                CvPoint p1 = new CvPoint(0, 0), p2 = new CvPoint(0, 0);

                for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
                    CvScalar color = CvScalar.RED;
                    CvRect sq = cvBoundingRect(ptr, 0);

                    p1.x(sq.x());
                    p2.x(sq.x() + sq.width());
                    p1.y(sq.y());
                    p2.y(sq.y() + sq.height());

                    cvCircle(img, cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2)), (int) 1, color, 2, CV_AA, 0);
                    cvLine(img, cvPoint((p1.x() + sq.width() / 2), 0), cvPoint((p1.x() + sq.width() / 2), 480), color, 1, 8, 0);
                    cvLine(img, cvPoint(0, (p1.y() + sq.height() / 2)), cvPoint(640, (p1.y() + sq.height() / 2)), color, 1, 8, 0);
                    cvRectangle(mask, cvPoint(p1.x() - inpaintSize, p1.y() - inpaintSize), cvPoint(p2.x() + inpaintSize, p2.y() + inpaintSize), cvScalarAll(255), CV_FILLED, 8, 0);
                    cvInpaint(gray2.asCvMat(), mask.asCvMat(), gray2.asCvMat(), 5.0, CV_INPAINT_TELEA);

                    frame4Test.showImage(gray2);
                    glintCord = cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2));
                    System.out.println("Glint :" + glintCord);

                }
            }

            cvSmooth(gray2, gray2, CV_BLUR, pupilSmooth);
            cvErode(gray2, gray2, null, pupilErode);
            cvAdaptiveThreshold(gray2, gray2, 255, CV_ADAPTIVE_THRESH_MEAN_C, brightPupil, pupilBlockSize, pupilDif);
            cvFindContours(gray2, storage2, contours2, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
            frame3.showImage(gray2);

            cvClearMemStorage(storage2);

            if (!contours2.isNull()) {

                System.out.println("Kontury dla źrenicy");

                CvSeq ptr = new CvSeq();
                CvPoint p1 = new CvPoint(0, 0), p2 = new CvPoint(0, 0);

                for (ptr = contours2; ptr != null; ptr = ptr.h_next()) {
                    CvScalar color = CvScalar.BLUE;
                    CvRect sq = cvBoundingRect(ptr, 0);
                    p1.x(sq.x());
                    p2.x(sq.x() + sq.width());
                    p1.y(sq.y());
                    p2.y(sq.y() + sq.height());
                    if (sq.width() < pupilMaxSize && sq.height() < pupilMaxSize) {
                        if (sq.width() > pupilMinSize && sq.height() > pupilMinSize) {
                            vector = 0;
                            vector2 = 0;
                            try {
                                vector = (p1.x() + sq.width() / 2) - glintCord.x();
                                vector2 = glintCord.y() - (p1.y() + sq.height() / 2);
                                System.out.println("Vector2 " + vector2);
                            } catch (Exception e) {
                                System.out.println("bład obliczania wektora");
                            }
                            System.out.println("#### WEKTOR #### " + vector);
                            if (vector > -40 && vector < 80 && vector2 > -40 && vector2 < 80) {
                                cvCircle(img, cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2)), (int) 1, color, 2, CV_AA, 0);
                                cvLine(img, cvPoint((p1.x() + sq.width() / 2), 0), cvPoint((p1.x() + sq.width() / 2), 480), color, 1, 8, 0);
                                cvLine(img, cvPoint(0, (p1.y() + sq.height() / 2)), cvPoint(640, (p1.y() + sq.height() / 2)), color, 1, 8, 0);
                                pupilCord = cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2));
                                System.out.println("Pupil Cord :" + pupilCord);

                                for (int j = 0; j < ptr.total(); j++) {
                                    CvBox2D minEllipse = cvFitEllipse2(ptr);
                                    //        System.out.println("AAA" + minEllipse);
                                    cvEllipseBox(img, minEllipse, CV_RGB(255, 255, 255), 1, 8, 0);
                                    //  System.out.println(" CENTER " + minEllipse.center().x());
                                    int x = (int) minEllipse.center().x();
                                    int y = (int) minEllipse.center().y();
                                    cvCircle(img, cvPoint(x, y), (int) 1, CV_RGB(0, 255, 255), 2, CV_AA, 0);
                                }



                                try {
                                    int vecX = pupilCord.x() - glintCord.x();
                                    int vecY = glintCord.y() - pupilCord.y();
                                    System.out.println("X value = " + vecX + "  Y value = " + vecY);

                                } catch (Exception e) {
                                    System.out.println("Bład obliczania wektora");
                                }
                            }
                        }
                    }
                }


            }

            frame2.showImage(gray);
            frame.showImage(img);
            cvZero(mask);
        }
        cvReleaseImage(mask);
        cvReleaseImage(gray2);
        cvReleaseImage(gray);
        cvReleaseCapture(capture);
        cvReleaseImage(img);

    }
}
