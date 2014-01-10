/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import paczka2.GIU.inne.PupGlintFind;
import paczka2.TestProg;
import com.googlecode.javacv.cpp.opencv_core;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import paczka2.GetTime20;
import paczka2.Test1;

/**
 *
 * @author XXX
 */
public class MainWindow extends javax.swing.JFrame implements KeyListener {

    private boolean camera_ON = false;
    private boolean gazeThread_STOP = false;
    gazeTestRunner myRunner = new gazeTestRunner();
    Thread gazeTestThread = new Thread(myRunner);
    calibrationValuesRunner gVR = new calibrationValuesRunner();
    Thread calibValRunner = new Thread(gVR);
    //private PupilGlintFinder pupilGlintFinder = new PupilGlintFinder();
    private CameraCapture tp = new CameraCapture();
    private PupGlintFind pupilGlintFinder = new PupGlintFind();
    private CalibrationPanel cp = new CalibrationPanel();
    ResultPanel rp = new ResultPanel();
    private GazeTest gt = new GazeTest();
    private JFrame mainFrame = new JFrame();
    private JFrame gazeFrame = new JFrame();
    private JFrame PicturePanelFrame = new JFrame();
    private JFrame ResultPanelFrame = new JFrame();
    private Vector[] vecArray = new Vector[9];
    private ArrayList<Vector> list = new ArrayList<Vector>();
    private int vecCounter = -1;
    PicturesPanel pp = new PicturesPanel();
    private double[] coefficientXArray;
    private double[] coefficientYArray;
    private double[] pointsArray = new double[5];
    private double[] vectorXArray = new double[pointsArray.length];
    private double[] pointsArray2 = new double[5];
    private double[] vectorXArray2 = new double[pointsArray2.length];
    private Vector[] vecArray2 = new Vector[9];
    ;
    private int k = -1;
    private int vectorCounter = -1;
    private int[] vecXarray = new int[10];
    double scrW;
    double scrH;
    boolean CALIB_START;
    private boolean SAVED = false;
    private boolean GAZE_ESTIMATION = false;
    int frameCounter = -1;
    int tmpFrameCounter = -1;
    private boolean SHOW_RESULTS = false;
    int savedCounter = 0;
    private File selFile;
    private boolean g_POINT = false;
    private boolean r_POINTS = false;
    private boolean r_LINES = false;
    private boolean r_HEAT = false;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        Toolkit screenSize = Toolkit.getDefaultToolkit();
        Dimension scrSize = screenSize.getScreenSize();
        scrW = scrSize.getWidth();
        scrH = scrSize.getHeight();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    public void makeScreenShoot(int nr) {
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = null;
        try {
            capture = new Robot().createScreenCapture(screenRect);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ImageIO.write(capture, "bmp", new File("SavedImage" + nr + ".bmp"));

        } catch (Exception e) {
        }
    }

    class calibrationValuesRunner implements Runnable {

        int arrayCounter = -1;
        int counter = 0;
        int point = -1;
        boolean READY = false;
        double[] tmpArray = new double[10];
        double totalResult = 0;
        double xVector;
        double yVector;
        int tmpCounter = -1;
        //  double tpmArrayCompute = 0;

        @Override
        public void run() {

            for (int i = 0; i < 10; i++) {
                tmpArray[i] = 0;
            }

            while (CALIB_START) {

                READY = cp.isPOINT_READY();
//                System.out.println("READY");
                System.out.println("Redy " + READY);


                if (cp.isCALIB_START() == true) {
                    //counter++;
                    counter = cp.getCounter();

                    if (counter > 9) {
                        CALIB_START = false;
                        for (int i = 0; i < 5; i++) {
                            System.out.println("vectorXArray[" + i + "]" + vectorXArray[i]);
                            System.out.println("vectorYArray[" + i + "]" + vectorXArray2[i]);
                        }
                    }

                    System.out.println("Counter" + counter);

                    try {
                        xVector = tp.getPupilCord().x() - tp.getGlintCord().x();
                        yVector = tp.getGlintCord().y() - tp.getPupilCord().y();

                    } catch (Exception e) {
                        System.out.println(" Vector error");
                    }

                    if (tmpCounter < counter && arrayCounter > 11) {
                        tmpCounter = counter;
                        arrayCounter = -1;
                        System.out.println("####################ZMIANA#######");

                        for (int i = 0; i < 10; i++) {
                            totalResult += tmpArray[i];
                        }
                        totalResult = totalResult / 10;

                        if (counter <= 4) {
                            vectorXArray[counter] = totalResult;
                        } else if (counter > 4 && counter <= 9) {
                            int tmpE = counter - 5;
                            vectorXArray2[tmpE] = totalResult;
                        }
                        System.out.println("########## total results = " + totalResult + " int" + (int) totalResult + "#######");
                        totalResult = 0;
                    }

                    if (READY == true && counter <= 9) {

                        System.out.println("Wektory X=" + xVector + " Y=" + yVector);
                        arrayCounter++;
                        System.out.println("PKT" + counter + " element tablicy " + arrayCounter);

                        if (counter <= 4) {
                            tmpArray[arrayCounter % 10] = xVector;
                            System.out.println("Zapis element tablicy" + arrayCounter % 10 + " X:" + xVector);
                        } else if (counter > 4) {
                            tmpArray[arrayCounter % 10] = yVector;
                            System.out.println("Zapis element tablicy" + arrayCounter % 10 + " Y:" + yVector);

                        }

                    }


                }

                try {
                    Thread.sleep(80);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class gazeTestRunner implements Runnable {

        int xVecto;
        int yVector;

        public void run() {
            while (true && gazeThread_STOP == false) {
                System.out.println("Coordintaes :) ");
                vectorCounter++;
                System.out.println("Vector counter#################### " + vecCounter);

//                xVecto = pupilGlintFinder.getPupilCoordinates().x() - pupilGlintFinder.getGlintCoordinates().x();
//                yVector = pupilGlintFinder.getGlintCoordinates().y() - pupilGlintFinder.getPupilCoordinates().y();

                xVecto = tp.getPupilCord().x() - tp.getGlintCord().x();
                yVector = tp.getGlintCord().y() - tp.getPupilCord().y();


                System.out.println("Vector X " + xVecto);
                System.out.println("Vector Y " + yVector);

                double Dx = 0;
                for (int i = 0; i < coefficientXArray.length; i++) {
                    Dx += coefficientXArray[i] * Math.pow(xVecto, i);
                    System.out.println("y1(" + Dx + ")= + a[" + i + "][0] *math.pow" + Math.pow(3, i) + "(x[1],i)(" + 3 + "," + i + ")");
                }

                double Dy = 0;
                for (int i = 0; i < coefficientYArray.length; i++) {
                    Dy += coefficientYArray[i] * Math.pow(yVector, i);
                    System.out.println("y1(" + Dy + ")= + a[" + i + "][0] *math.pow" + Math.pow(3, i) + "(x[1],i)(" + 3 + "," + i + ")");
                }
                System.out.println(" Dx    =   " + Dx);
                System.out.println(" Dy    =   " + Dy);

                // x = wynik[0] * Math.pow(xVecto, bb)
                gt.setGazeX((int) Dx);
                gt.setGazeY((int) Dy);

                //FRAME COUNTER
                frameCounter++;
                System.out.println("Frame Counter: " + frameCounter);
                if (GAZE_ESTIMATION == true) {
                    EstimationResults er = new EstimationResults();
                    er.setX((int) Dx);
                    er.setY((int) Dy);
                    er.setTime(frameCounter);
                    pp.setG_point(g_POINT);
                    pp.setTmp_X((int) Dx);
                    pp.setTmp_Y((int) Dy);
                    pp.addEstimationPoint(er);
                    System.out.println("Point added");
                }

                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 51));

        jButton1.setText("Auto calibration");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Camera");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Settings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Save");
        jButton3.setActionCommand("Save ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Gaze test");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Manual calbration");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Gaze estimation");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Show results");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Select image file");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jCheckBox1.setText("G. point");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        jCheckBox2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jCheckBox2.setText("Points");
        jCheckBox2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox2StateChanged(evt);
            }
        });

        jCheckBox3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jCheckBox3.setText("Lines");
        jCheckBox3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox3StateChanged(evt);
            }
        });

        jCheckBox4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jCheckBox4.setText("Heat");
        jCheckBox4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox4StateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox2)
                            .addComponent(jCheckBox4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox2)
                                .addGap(4, 4, 4)
                                .addComponent(jCheckBox3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox4))
                            .addComponent(jCheckBox1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(62, 62, 62))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //   vectorCounter = -1;
//        k = -1;
        cp = new CalibrationPanel();
        SAVED = false;
        calibValRunner = new Thread(gVR);
        CALIB_START = true;

        opencv_core.CvPoint[] calibArray = new opencv_core.CvPoint[10];

        calibArray[0] = opencv_core.cvPoint(20, (int) scrH / 2);
        calibArray[1] = opencv_core.cvPoint(20 + 40 + (int) (scrW / 5), (int) scrH / 2);
        calibArray[2] = opencv_core.cvPoint((int) scrW / 2, (int) scrH / 2);
        calibArray[3] = opencv_core.cvPoint((int) scrW - 20 - 40 - (int) (scrW / 5), (int) scrH / 2);;
        calibArray[4] = opencv_core.cvPoint((int) scrW - 20, (int) scrH / 2);

        calibArray[5] = opencv_core.cvPoint((int) scrW / 2, 20);
        calibArray[6] = opencv_core.cvPoint((int) scrW / 2, 40 + (int) (scrH / 5));
        calibArray[7] = opencv_core.cvPoint((int) scrW / 2, (int) scrH / 2);
        calibArray[8] = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 40 - (int) (scrH / 5));
        calibArray[9] = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 20);

        mainFrame.setUndecorated(true);
        mainFrame.getContentPane().add(cp);
        mainFrame.setLocation(0, 0);
        mainFrame.pack();
        mainFrame.setSize(new Dimension((int) cp.getScrW(), (int) cp.getScrH()));

        mainFrame.setVisible(true);
        cp.setAUTO(true);
        cp.addKeyListener(this);
        cp.setFocusable(true);
        cp.setCalibCord(calibArray);


        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        calibValRunner.start();

        //Test1.start();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {

            //tp.setSTART(true);
            tp.start();
//            pupilGlintFinder.setCAMERA_ON(true);
            // pupilGlintFinder.start();
        } else {
            //   tp.setSTART(false);
            // pupilGlintFinder.setDone(true);
        }

        System.out.println("Camera state ON = " + camera_ON);
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new SettingPanel().setVisible(true);
        //  new Settings().setVisible(true);

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        double[] x = new double[pointsArray.length];

        x = vectorXArray;


        double[] y = new double[pointsArray.length];

        y[0] = cp.leftCenter.x();
        y[1] = cp.leftCenter25.x();
        y[2] = cp.centerCenter.x();
        y[3] = cp.rightCenter45.x();
        y[4] = cp.rightCenter.x();

        for (int i = 0; i < pointsArray.length; i++) {
            System.out.println("Vector X array[" + i + "]=" + x[i]);
            System.out.println("Point x[" + i + "]=" + y[i]);
        }
        coefficientXArray = new double[pointsArray.length];
        coefficientXArray = PolynomianCompute.getCoefficients(x, y);


        double[] x1 = new double[pointsArray.length];

        x1 = vectorXArray2;


        double[] y1 = new double[pointsArray.length];

        y1[0] = cp.centerTop.y();
        y1[1] = cp.centerTop25.y();
        y1[2] = cp.centerCenter.y();
        y1[3] = cp.centerBottom45.y();
        y1[4] = cp.centerBottom.y();


        for (int i = 0; i < pointsArray.length; i++) {
            System.out.println("Vector Y array[" + i + "]=" + x1[i]);
            System.out.println("Point y[" + i + "]=" + y1[i]);
        }

        coefficientYArray = new double[pointsArray.length];
        coefficientYArray = PolynomianCompute.getCoefficients(x1, y1);
        SAVED = true;

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        tmpFrameCounter = -1;
        frameCounter = -1;

        if (SAVED == true) {

            gazeThread_STOP = false;
            gazeFrame = new JFrame();
            gt = new GazeTest();
            gazeTestThread = new Thread(myRunner);
            camera_ON = true;
        }


        if (camera_ON == true && gazeThread_STOP == false && SAVED == true) {
            gazeTestThread.start();
        }

        gazeFrame.setUndecorated(true);
        gazeFrame.getContentPane().add(gt);
        gazeFrame.setLocation(0, 0);
        gazeFrame.pack();
        gazeFrame.setSize(new Dimension((int) cp.getScrW(), (int) cp.getScrH()));

        gazeFrame.setVisible(true);
        gt.addKeyListener(this);
        gt.setFocusable(true);

        gazeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

//       vectorCounter = -1;
//        k = -1;
        SAVED = false;
        cp = new CalibrationPanel();
        //  calibValRunner = new Thread(gVR);
        //    CALIB_START = true;

        opencv_core.CvPoint[] calibArray = new opencv_core.CvPoint[10];

        calibArray[0] = opencv_core.cvPoint(20, (int) scrH / 2);
        calibArray[1] = opencv_core.cvPoint(20 + 40 + (int) (scrW / 5), (int) scrH / 2);
        calibArray[2] = opencv_core.cvPoint((int) scrW / 2, (int) scrH / 2);
        calibArray[3] = opencv_core.cvPoint((int) scrW - 20 - 40 - (int) (scrW / 5), (int) scrH / 2);;
        calibArray[4] = opencv_core.cvPoint((int) scrW - 20, (int) scrH / 2);

        calibArray[5] = opencv_core.cvPoint((int) scrW / 2, 20);
        calibArray[6] = opencv_core.cvPoint((int) scrW / 2, 40 + (int) (scrH / 5));
        calibArray[7] = opencv_core.cvPoint((int) scrW / 2, (int) scrH / 2);
        calibArray[8] = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 40 - (int) (scrH / 5));
        calibArray[9] = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 20);


        //mainFrame.setTitle("main window title");
        mainFrame.setUndecorated(true);
        mainFrame.getContentPane().add(cp);
        mainFrame.setLocation(0, 0);
        mainFrame.pack();
        mainFrame.setSize(new Dimension((int) cp.getScrW(), (int) cp.getScrH()));

        mainFrame.setVisible(true);
        cp.addKeyListener(this);
        cp.setFocusable(true);
        cp.setCalibCord(calibArray);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  calibValRunner.start();

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        pp = new PicturesPanel();
        pp.setFile(selFile);

        if (SAVED == true) {
            SHOW_RESULTS = false;
            gazeThread_STOP = false;
            gazeFrame = new JFrame();
            gt = new GazeTest();
            gazeTestThread = new Thread(myRunner);
            camera_ON = true;
        }

        tmpFrameCounter = -1;
        frameCounter = -1;


        GAZE_ESTIMATION = true;
        PicturePanelFrame.setUndecorated(true);
        PicturePanelFrame.getContentPane().add(pp);
        PicturePanelFrame.setLocation(0, 0);
        PicturePanelFrame.pack();

        PicturePanelFrame.setSize(new Dimension((int) scrW, (int) scrH));
        pp.setImage();
        pp.addKeyListener(this);
        pp.setFocusable(true);
        pp.setVisible(true);
        pp.tm.start();
        PicturePanelFrame.setVisible(true);
        PicturePanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gazeTestThread = new Thread(myRunner);

        if (camera_ON == true && gazeThread_STOP == false) {
            gazeTestThread.start();
            System.out.println("Gaze estimation : ON");
        } else {
            System.out.println("Camera capture error");
        }

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        rp = new ResultPanel();
        rp.setFile(selFile);
        SHOW_RESULTS = true;

//        //        Sprawidzić czy jest zbadane
        ResultPanelFrame.setUndecorated(true);
        ResultPanelFrame.getContentPane().add(rp);
        ResultPanelFrame.setLocation(0, 0);
        ResultPanelFrame.pack();
        ResultPanelFrame.setSize(new Dimension((int) 1280, (int) 800));

        ImageLabel label = new ImageLabel(new ImageIcon("fig2.jpg"));
        label.setLocation(29, 37);
        rp.setR_HEAT(r_HEAT);
        rp.setR_LINES(r_LINES);
        rp.setR_POINTS(r_POINTS);
        rp.readPicture();
        rp.setBackground(Color.black);
        rp.add(label);
        rp.addKeyListener(this);
        rp.setFocusable(true);
        rp.setVisible(true);
        rp.loadEstimatedResults("EstimationResults");
        rp.tm.start();
        // pp.loadEstimatedResults("EstimationResults");

        ResultPanelFrame.setVisible(true);
        ResultPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

        String filename = File.separator + "tmp";
        JFileChooser fc = new JFileChooser(new File(filename));

        // Show open dialog
        fc.showOpenDialog(null);
        selFile = fc.getSelectedFile();

//        // Show save dialog
//        fc.showSaveDialog(null);
//        selFile = fc.getSelectedFile();

        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged

        if (jCheckBox1.isSelected()) {
            g_POINT = true;
        } else {
            g_POINT = false;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1StateChanged

    private void jCheckBox2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox2StateChanged

        if (jCheckBox2.isSelected()) {
            r_POINTS = true;
        } else {
            r_POINTS = false;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2StateChanged

    private void jCheckBox3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox3StateChanged

        if (jCheckBox3.isSelected()) {
            r_LINES = true;
        } else {
            r_LINES = false;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox3StateChanged

    private void jCheckBox4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox4StateChanged

        if (jCheckBox4.isSelected()) {
            r_HEAT = true;
        } else {
            r_HEAT = false;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox4StateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int i = e.getKeyCode();

        System.out.println("############ code key " + i);

        if (i == 27) {


            mainFrame.remove(cp);
            mainFrame.dispose();
            gazeFrame.dispose();
            ResultPanelFrame.dispose();
            PicturePanelFrame.dispose();
            rp.tm.stop();

            gazeThread_STOP = true;
            CALIB_START = false;
            //   pp.resetCordinates();


            if (GAZE_ESTIMATION == true && SHOW_RESULTS == false) {
                pp.saveEstimationResults();

            }

            GAZE_ESTIMATION = false;

            System.out.println("Gaze estimation : OFF\n Calibration start : OFF\n Gaze capture : OFF");

            System.out.println("Escape");
        }
        if (i == 32) {
            vecCounter++;
            //    System.out.println("Spacja " + vecArray + 1);
            opencv_core.CvPoint glint = pupilGlintFinder.getGlintCoordinates();
            opencv_core.CvPoint pupil = pupilGlintFinder.getPupilCoordinates();
            System.out.println(" Glint =" + glint + " Pupil = " + pupil);

            Vector vc = new Vector();

            vc.setIndex(vecCounter);

            vc.setPupilX(pupil.x());
            vc.setPupilY(pupil.y());

            vc.setGlintX(glint.x());
            vc.setGlintY(glint.y());

            vc.setVectorX(pupil.x() - glint.x());
            vc.setVectorY(pupil.y() - glint.y());

//            double xVector = pupilGlintFinder.getPupilCoordinates().x() - pupilGlintFinder.getGlintCoordinates().x();
//            double yVector = pupilGlintFinder.getGlintCoordinates().y()-pupilGlintFinder.getPupilCoordinates().y() ;

            double xVector = tp.getPupilCord().x() - tp.getGlintCord().x();
            double yVector = tp.getGlintCord().y() - tp.getPupilCord().y();

            if (vecCounter >= 0 && vecCounter <= 4) {
                vectorXArray[vecCounter] = xVector;
                vecArray[vecCounter] = vc;
                System.out.println("######################VC:" + vc);
            }

            System.out.println("Zapisano wektor nr[" + i + "] :" + vc);

            if (vecCounter == 4) { // 8 dla 9ciu 
                // mainFrame.setVisible(false);
                VectorSaveAndLoad.saveVectorCoordinates(vecArray, "TestVector");
                System.out.println("Koniec kalibracji");
                //  vecCounter = -1;
                System.out.println("Obliczono współczyniki dla X");
            }


            if (vecCounter > 4 && vecCounter <= 9) {
                k++;
                System.out.println("K wynois " + k);
                vectorXArray2[k] = yVector;
                System.out.println("Wektor Y wynosi:" + yVector);
                vecArray2[k] = vc;
                System.out.println("######################VC:" + vc);
                System.out.println("Dodanie ");
            }

            if (vecCounter == 9) { // 8 dla 9ciu 
                mainFrame.setVisible(false);
                VectorSaveAndLoad.saveVectorCoordinates(vecArray, "TestVector2");
                System.out.println("Koniec kalibracji");
                vecCounter = -1;
                System.out.println("Obliczono współczyniki");
            }

        }
        if (i == 83) {
            savedCounter++;
            makeScreenShoot(savedCounter);

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
