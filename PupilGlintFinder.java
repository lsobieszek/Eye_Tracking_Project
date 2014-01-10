/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import com.googlecode.javacv.cpp.opencv_core;

import OpenCvTests.*;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author XXX
 */
public class PupilGlintFinder extends Thread {

    private AtomicBoolean done = new AtomicBoolean(false);
    public CvPoint pupilCoordinates = cvPoint(0, 0);
    public CvPoint glintCoordinates = cvPoint(0, 0);
    private static int SCALE = 2;
    public static final String XML_FILE = "pupilBootom.xml";
    private static IplImage img; // Głowne
    private static IplImage imgCopy; // copia głównego
    private static IplImage roi;    // Roi oka
    private static IplImage imgGray; // Grey źródła
    private static IplImage roiGray;
    private static IplImage roiThresh;
    private static CvRect r;
    private static IplImage roiThresh2;
    private static IplImage glintRoi;

    public void run() {
        find();
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AtomicBoolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done.set(done);
    }

    public CvPoint getPupilCoordinates() {
        return pupilCoordinates;
    }

    public void setPupilCoordinates(CvPoint pupilCoordinates) {
        this.pupilCoordinates = pupilCoordinates;
    }

    public CvPoint getGlintCoordinates() {
        return glintCoordinates;
    }

    public void setGlintCoordinates(CvPoint glintCoordinates) {
        this.glintCoordinates = glintCoordinates;
    }

    private void find() {
        //CvCapture capture = cvCreateFileCapture("eye2.avi");
        CvCapture capture = cvCreateCameraCapture(0);
        CvMemStorage storage = CvMemStorage.create();

        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
        cvSetCaptureProperty(capture, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FPS, 10);

        img = opencv_highgui.cvQueryFrame(capture);
        imgGray = IplImage.create(img.width(), img.height(), img.depth(), 1);
        imgCopy = IplImage.create(img.width(), img.height(), img.depth(), img.nChannels());
        roi = IplImage.create(img.width(), img.height(), img.depth(), img.nChannels());



        CanvasFrame frame = new CanvasFrame("Webcam");
        CanvasFrame frame2 = new CanvasFrame("Image copy");
        //  CanvasFrame frame3 = new CanvasFrame("Roi");
        //  CanvasFrame frame4 = new CanvasFrame("Roi Gray");

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));

        CvSeq faces = null;

        CvRect rect;

        int count = 1;

        int frameCount = 0;


        while (frame.isVisible() && (img = opencv_highgui.cvQueryFrame(capture)) != null) {



            count++;
            System.out.println("Klatka " + count);

            imgCopy = cvCloneImage(img);

            cvCvtColor(img, imgGray, CV_BGR2GRAY);
            cvAdaptiveThreshold(imgGray, imgGray, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY, 71, 7);

            if (count % 2 == 0) {
                faces = cvHaarDetectObjects(imgGray, cascade, storage, 1.2, SCALE, CV_HAAR_DO_CANNY_PRUNING);
                cvClearMemStorage(storage);
                r = new CvRect(cvGetSeqElem(faces, 1));
                frameCount = faces.total();
            } else {
                faces = null;
                frameCount = 0;
                r = null;
            }

            for (int i = 0; i < frameCount; i++) {

                cvRectangle(img, cvPoint(r.x(), r.y()), cvPoint((r.x() + r.width()), (r.y() + r.height())), CvScalar.RED, 1, CV_AA, 0);
                rect = cvRect(r.x(), r.y(), r.width(), r.height());
                cvSetImageROI(imgCopy, rect);
                roi = cvCreateImage(cvGetSize(imgCopy), imgCopy.depth(), imgCopy.nChannels());
                roiGray = IplImage.create(roi.width(), roi.height(), roi.depth(), 1);
                cvCopy(imgCopy, roi);
                cvResetImageROI(roi);

                glintRoi = IplImage.create((roi.width() / 10) * 8, (roi.height() / 10) * 8, 8, 1);
                cvCvtColor(roi, roiGray, CV_BGR2GRAY);
                cvSmooth(roiGray, roiGray, CV_BLUR, 6);
                roiThresh = IplImage.create(roi.width(), roi.height(), roi.depth(), 1);
                roiThresh2 = IplImage.create(roi.width(), roi.height(), roi.depth(), 1);
                cvAdaptiveThreshold(roiGray, roiThresh, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY_INV, 67, 9);
                cvAdaptiveThreshold(roiGray, roiThresh2, 255, CV_ADAPTIVE_THRESH_MEAN_C, CV_THRESH_BINARY_INV, 9, 5);
                cvReleaseImage(roi);
            }

            if (roiThresh != null && count % 2 == 0) {

                /////////////////////// Źrenica

                try {
                    CvSeq contours = new CvContour(null);
                    cvFindContours(roiThresh, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
                    cvClearMemStorage(storage);
                    CvSeq ptr = new CvSeq();
                    CvPoint p1 = new CvPoint(0, 0), p2 = new CvPoint(0, 0);
                    for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
                        CvScalar color = CvScalar.RED;
                        CvRect sq = cvBoundingRect(ptr, 0);
                        double radius = sq.width() / 2;
                        p1.x(sq.x());
                        p2.x(sq.x() + sq.width());
                        p1.y(sq.y());
                        p2.y(sq.y() + sq.height());


                        if (sq.width() < 65 && sq.height() < 65) {
                            if (sq.width() > 40 && sq.height() > 40) {

                                cvCircle(roiGray, cvPoint(p1.x() + sq.width() / 2, p1.y() + sq.height() / 2), (int) 1, color, 2, CV_AA, 0);
                                cvLine(roiGray, cvPoint(p1.x() + sq.width() / 2, 0), cvPoint(p1.x() + sq.width() / 2, 480), color, 1, 8, 0);
                                cvLine(roiGray, cvPoint(0, p1.y() + sq.height() / 2), cvPoint(640, p1.y() + sq.height() / 2), color, 1, 8, 0);

                                setPupilCoordinates(cvPoint(p1.x() + sq.width() / 2, p1.y() + sq.height() / 2));
                            }
                            if (p1.y() + sq.height() / 2 > 100) {
                            }
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Brak konturów");
                }


                //////////////////////////////  Glint


                try {

                    CvSeq ptr2 = new CvSeq();
                    CvPoint p21 = new CvPoint(0, 0), p22 = new CvPoint(0, 0);
                    CvSeq contours2 = new CvContour(null);
                    cvFindContours(roiThresh2, storage, contours2, Loader.sizeof(CvContour.class), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint(0, 0));
                    cvClearMemStorage(storage);
                    for (ptr2 = contours2; ptr2 != null; ptr2 = ptr2.h_next()) {
                        CvScalar color = CvScalar.WHITE;
                        CvRect sq = cvBoundingRect(ptr2, 0);
                        double radius = sq.width() / 2;
                        p21.x(sq.x());
                        p22.x(sq.x() + sq.width());
                        p21.y(sq.y());
                        p22.y(sq.y() + sq.height());



                        if (sq.width() > 15 && sq.height() > 15) {

                            cvCircle(roiGray, cvPoint(p21.x() + sq.width() / 2, p21.y() + sq.height() / 2), 2, CvScalar.BLUE, 2, CV_AA, 0);
                            cvLine(roiGray, cvPoint(p21.x() + sq.width() / 2, 0), cvPoint(p21.x() + sq.width() / 2, 480), color, 1, 8, 0);
                            cvLine(roiGray, cvPoint(0, p21.y() + sq.height() / 2), cvPoint(640, p21.y() + sq.height() / 2), color, 1, 8, 0);
                            setGlintCoordinates(cvPoint(p21.x() + sq.width() / 2, p21.y() + sq.height() / 2));
                        }
                        if (p21.y() + sq.height() / 2 > 50) {
                        }
                    }

                } catch (Exception e) {
                }


                frame2.showImage(roiGray);
            }

            frame.showImage(img);

            cvReleaseImage(imgCopy);

            if (done.get()) {
                break;
            }

        }

        cvReleaseCapture(capture);
        cvReleaseImage(img);
        cvReleaseImage(imgGray);
        frame.dispose();
        frame2.dispose();
//        frame3.dispose();
//        frame4.dispose();
    }
}
