/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import com.googlecode.javacv.cpp.opencv_core;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 *
 * @author XXX
 */
public class ResultPanel extends javax.swing.JPanel implements ActionListener {

    Timer tm = new Timer(50, this);
    private static ArrayList<EstimationResults> resultList;
    private final double scrW;
    private final double scrH;
    private int counter;
    private int resultCounter;
    int x = 0;
    int y = 0;
    private int[] vecXArray = new int[8];
    private int[] vecYArray = new int[8];
    private int tmpXPoint;
    private int tmpYPoint;
    private int time = 0;
    int tmpCount = 0;
    int[] xArray = new int[2];
    int[] yArray = new int[2];
    opencv_core.CvPoint point = new opencv_core.CvPoint();
    private File file = null;
    private boolean r_POINTS = false;
    private boolean r_LINES = false;
    private boolean r_HEAT = false;

    /**
     * Creates new form ResultPanel
     */
    public ResultPanel() {
        initComponents();
        Toolkit screenSize = Toolkit.getDefaultToolkit();
        Dimension scrSize = screenSize.getScreenSize();
        scrW = scrSize.getWidth();
        scrH = scrSize.getHeight();

        setBackground(Color.black);
        readPicture();
    }
    private BufferedImage image = null;

    public void setR_POINTS(boolean r_POINTS) {
        this.r_POINTS = r_POINTS;
    }

    public void setR_LINES(boolean r_LINES) {
        this.r_LINES = r_LINES;
    }

    public void setR_HEAT(boolean r_HEAT) {
        this.r_HEAT = r_HEAT;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void readPicture() {
        try {
            if (file != null) {
                image = ImageIO.read(file);
            } else {
                image = ImageIO.read(new File("testowy.jpg"));
            }
        } catch (IOException ex) {
            System.out.println("Błąd pliku");
        }

    }

    public void resteCordinates() {
        x = 0;
        y = 0;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        if (resultCounter < 2) {
            System.out.println("TŁO KOLOR");
            g.setColor(Color.black);
            g.fillRect(0, 0, (int) scrW, (int) scrH);


        }
        //   super.paint(g);
        int listSize = resultList.size();
        System.out.println("Liste Size: " + listSize);

        int tmpX = (int) (scrW / 2) - image.getWidth() / 2;
        int tmpY = (int) (scrH / 2) - image.getHeight() / 2;
        EstimationResults result = new EstimationResults();
        if (resultCounter < 4) {
            g.drawImage(image, tmpX, tmpY, null);

            /// mapa ciepła zimno
            Color myColour = new Color(0, 0, 160, 40);
            g2.setColor(myColour);
            g2.fillRect(0, 0, (int) scrW, (int) scrH);
            ///
        }
        resultCounter++;
        //  int tmp = -1;

        System.out.println("    Counter:" + counter + "                 ### Licznik wyników: " + resultCounter);
        if (counter == 7) {
            counter = -1;
        }
//
        if (resultCounter < listSize) {
            result = resultList.get(resultCounter);
            x = result.getX();
            y = result.getY();
            time = result.getTime();
            System.out.println("RESULT X : Y  - " + x + " : " + y);

            counter++;
//
            vecXArray[counter] = x;
            vecYArray[counter] = y;
            tmpXPoint = vecXArray[0] + vecXArray[1] + vecXArray[2] + vecXArray[3] + vecXArray[4] + vecXArray[5] + vecXArray[6] + vecXArray[7];//+vecXArray[8]+vecXArray[9]+vecXArray[10]+vecXArray[11];
            tmpYPoint = vecYArray[0] + vecYArray[1] + vecYArray[2] + vecYArray[3] + vecYArray[4] + vecYArray[5] + vecYArray[6] + vecYArray[7];//+vecYArray[8]+vecYArray[9]+vecYArray[10]+vecYArray[11];
            tmpXPoint = tmpXPoint / 8;
            tmpYPoint = tmpYPoint / 8;
            //  g.drawOval(30, 30, 20, 20);
            System.out.println("TMP X && Y: " + tmpXPoint + " : " + tmpYPoint);
            //  super.paint(g);
            //  g.setColor(Color.red);
            point = opencv_core.cvPoint(tmpXPoint, tmpYPoint);
            System.out.println("####### POINT " + point);
            if (resultCounter % 2 == 0) {
                xArray[0] = point.x();
                yArray[0] = point.y();
            } else {
                xArray[1] = point.x();
                yArray[1] = point.y();
            }

            if (r_LINES == true) {

                if (resultCounter > 14) {

                    g2.setStroke(new BasicStroke(4));
                    g2.setColor(Color.BLUE);
                    g2.draw(new Line2D.Float(xArray[0], yArray[0], xArray[1], yArray[1]));
                    g.drawLine(xArray[0], yArray[0], xArray[1], yArray[1]);
                    System.out.println("####### Array" + xArray[0] + " " + yArray[0] + " next " + xArray[1] + " " + yArray[1]);
                }
            }

        }

        if (r_POINTS == true) {
            g2.setColor(Color.WHITE);
            //    g.drawOval(tmpXPoint, tmpYPoint, 10, 10);
            // g2.draw(getEllipseFromCenter(tmpXPoint, tmpYPoint, 5, 5));
            g2.draw(getEllipseFromCenter(tmpXPoint, tmpYPoint, 4, 4));
            g2.setColor(Color.BLUE);
            //   g2.draw(getEllipseFromCenter(tmpXPoint, tmpYPoint, 3, 3));
            g2.draw(getEllipseFromCenter(tmpXPoint, tmpYPoint, 2, 2));
        }




        int evektorX = xArray[0] - xArray[1];
        evektorX = Math.abs(evektorX);

        int evektorY = yArray[0] - yArray[1];
        evektorY = Math.abs(evektorY);

        if (r_HEAT == true) {

            Color myYellowColour = new Color(255, 255, 0, 15);
            g2.setColor(myYellowColour);
            g2.fillOval(tmpXPoint - 30, tmpYPoint - 30, 70, 70);

            if (evektorX < 4 && evektorY < 4) {

                Color myRedColour = new Color(255, 20, 0, 25);
                g2.setColor(myRedColour);
                g2.fillOval(tmpXPoint - 15, tmpYPoint - 15, 30, 30);
                System.out.println("Dodano czerwony" + evektorX + " " + evektorY);
            }
        }

    }

    private Ellipse2D getEllipseFromCenter(double x, double y, double width, double height) {
        double newX = x - width / 2.0;
        double newY = y - height / 2.0;

        Ellipse2D ellipse = new Ellipse2D.Double(newX, newY, width, height);

        return ellipse;
    }

    public static void loadEstimatedResults(String fileName) {

        try {
            resultList = VectorSaveAndLoad.loadEstimatedResults(fileName);
            System.out.println("Estimated results loaded.");
            for (EstimationResults s : resultList) {
                System.out.println(s.getX());
                System.out.println(s.getY());
                System.out.println(s.getTime());
                System.out.println("Loaded");
            }

        } catch (Exception e) {
            System.out.println("Estimated result load error");
            e.printStackTrace();
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

        setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();

        //   System.out.println("repaint");
    }
}
