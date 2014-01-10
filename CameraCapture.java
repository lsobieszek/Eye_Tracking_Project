/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
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
import static com.googlecode.javacv.cpp.opencv_photo.*;

/**
 *
 * @author XXX
 */
public class CameraCapture extends Thread {

    private IplImage img;
    private CvPoint glintCord;
    private CvPoint pupilCord;
    private int vector;
    private int vector2;

    public CvPoint getGlintCord() {
        return glintCord;
    }

    public CvPoint getPupilCord() {
        return pupilCord;
    }

    public void run() {
        cameraCapture();
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cameraCapture() {

        SettingModel sm = new SettingModel();

        try {
            sm = SettingsSaveAndLoad.loadEyeSettings("Eye");
            System.out.println("Settings file loaded");
        } catch (Exception e) {
            System.out.println(" Fille settings error");
        }



        CvCapture capture = cvCreateCameraCapture(0);
        CvMemStorage storage = CvMemStorage.create();
        CvMemStorage storage2 = CvMemStorage.create();
//        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 1050);
//        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 1400);
        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FPS, 30);
        //  cvSetCaptureProperty(capture, CV_CAP_PROP_FPS, 20);
        img = opencv_highgui.cvQueryFrame(capture);

        CanvasFrame frame = new CanvasFrame("Webcam");
        CanvasFrame frame2 = new CanvasFrame("Glint ");
        CanvasFrame frame3 = new CanvasFrame("Pupil");
        ///   CanvasFrame frameTest = new CanvasFrame("Test");

        IplImage gray = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        IplImage gray2 = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        IplImage mask = cvCreateImage(cvGetSize(gray), 8, 1);



        while (frame.isVisible() && (img = opencv_highgui.cvQueryFrame(capture)) != null) {

            CvSeq contours = new CvContour(null);
            CvSeq contours2 = new CvContour(null);

            cvCvtColor(img, gray, CV_BGR2GRAY);
            cvCvtColor(img, gray2, CV_BGR2GRAY);
            cvSmooth(gray, gray, CV_BLUR, sm.getGlintSmooth());
            cvErode(gray, gray, null, sm.getGlintDylate());

            cvAdaptiveThreshold(gray, gray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, sm.getGlintBlockSize(), sm.getGlintDif());
            cvFindContours(gray, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
            cvClearMemStorage(storage);


            cvClearMemStorage(storage2);

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

                    cvRectangle(mask, cvPoint(p1.x() - 5, p1.y() - 5), cvPoint(p2.x() + 5, p2.y() + 5), cvScalarAll(255), CV_FILLED, 8, 0);
                    cvInpaint(gray2.asCvMat(), mask.asCvMat(), gray2.asCvMat(), 4.0, CV_INPAINT_NS);

                    glintCord = cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2));

                }
            }

            cvSmooth(gray2, gray2, CV_BLUR, sm.getPupilSmooth());
            cvErode(gray2, gray2, null, sm.getPupilErode());
            cvAdaptiveThreshold(gray2, gray2, 255, CV_ADAPTIVE_THRESH_MEAN_C, sm.getBrightPupil(), sm.getPupilBlockSize(), sm.getPupilDif());
            cvFindContours(gray2, storage2, contours2, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
            frame3.showImage(gray2);


            if (!contours2.isNull()) {


                CvSeq ptr = new CvSeq();
                CvPoint p1 = new CvPoint(0, 0), p2 = new CvPoint(0, 0);

                for (ptr = contours2; ptr != null; ptr = ptr.h_next()) {
                    CvScalar color = CvScalar.BLUE;
                    CvRect sq = cvBoundingRect(ptr, 0);
                    p1.x(sq.x());
                    p2.x(sq.x() + sq.width());
                    p1.y(sq.y());
                    p2.y(sq.y() + sq.height());
                    if (sq.width() < sm.getPupilMaxSize() && sq.height() < sm.getPupilMaxSize()) {
                        if (sq.width() > sm.getPupilMinSize() && sq.height() > sm.getPupilMinSize()) {
                            vector = 0;
                            vector2 = 0;
                            try {
                                vector = (p1.x() + sq.width() / 2) - glintCord.x();
                                vector2 = glintCord.y() - (p1.y() + sq.height() / 2);
                            } catch (Exception e) {
                                System.out.println("bład obliczania wektora");
                            }
                            if (vector > -40 && vector < 80 && vector2 > -40 && vector2 < 80) {
                                cvCircle(img, cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2)), (int) 1, color, 2, CV_AA, 0);
                                cvLine(img, cvPoint((p1.x() + sq.width() / 2), 0), cvPoint((p1.x() + sq.width() / 2), 480), color, 1, 8, 0);
                                cvLine(img, cvPoint(0, (p1.y() + sq.height() / 2)), cvPoint(640, (p1.y() + sq.height() / 2)), color, 1, 8, 0);
                                pupilCord = cvPoint((p1.x() + sq.width() / 2), (p1.y() + sq.height() / 2));

                                for (int j = 0; j < ptr.total(); j++) {

                                    CvBox2D minEllipse = cvFitEllipse2(ptr);
                                    cvEllipseBox(img, minEllipse, CV_RGB(255, 255, 255), 1, 8, 0);
                                    int x = (int) minEllipse.center().x();
                                    int y = (int) minEllipse.center().y();

                                }

                                try {
                                    int vecX = pupilCord.x() - glintCord.x();
                                    int vecY = glintCord.y() - pupilCord.y();

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
