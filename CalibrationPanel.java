/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.Timer;
import sun.security.util.Length;

/**
 *
 * @author XXX
 */
public class CalibrationPanel extends javax.swing.JPanel implements ActionListener {

    Timer tm = new Timer(60, this);
    double scrW;
    double scrH;
    CvPoint centerCenter;
    CvPoint centerTop;
    CvPoint centerBottom;
    CvPoint leftCenter;
    CvPoint leftTop;
    CvPoint leftBottom;
    CvPoint rightCenter;
    CvPoint rightTop;
    CvPoint rightBottom;
    CvPoint centerTop25;
    CvPoint centerBottom45;
    CvPoint leftCenter25;
    CvPoint rightCenter45;
    int y;// = 20 + 40 + (int) (scrW / 5);
    int x;//=(int)( scrH / 2);
    CvPoint[] calibCord = new CvPoint[10];
    int counter = -1;
    int textCounter = -1;
    boolean CALIB_START = false;
    boolean POINT_READY = false;
    boolean AUTO = false;

    public void setAUTO(boolean AUTO) {
        this.AUTO = AUTO;
    }

    public boolean isPOINT_READY() {
        return POINT_READY;
    }

    public boolean isCALIB_START() {
        return CALIB_START;
    }

    public void setCalibCord(CvPoint[] calibCord) {
        this.calibCord = calibCord;
    }
    private int size = 40;

    public int getCounter() {
        return counter;
    }

    public int setDefaultSize() {
        return size = 50;
    }

    public CvPoint getCenterTop25() {
        return centerTop25;
    }

    public void setCenterTop25(CvPoint centerTop25) {
        this.centerTop25 = centerTop25;
    }

    public CvPoint getCenterBottom45() {
        return centerBottom45;
    }

    public void setCenterBottom45(CvPoint centerBottom45) {
        this.centerBottom45 = centerBottom45;
    }

    public CvPoint getLeftCenter25() {
        return leftCenter25;
    }

    public void setLeftCenter25(CvPoint leftCenter25) {
        this.leftCenter25 = leftCenter25;
    }

    public CvPoint getRightCenter45() {
        return rightCenter45;
    }

    public void setRightCenter45(CvPoint rightCenter45) {
        this.rightCenter45 = rightCenter45;
    }

    public CvPoint getCenterCenter() {
        return centerCenter;
    }

    public CvPoint getCenterTop() {
        return centerTop;
    }

    public CvPoint getCenterBottom() {
        return centerBottom;
    }

    public CvPoint getLeftCenter() {
        return leftCenter;
    }

    public CvPoint getLeftTop() {
        return leftTop;
    }

    public CvPoint getLeftBottom() {
        return leftBottom;
    }

    public CvPoint getRightCenter() {
        return rightCenter;
    }

    public CvPoint getRightTop() {
        return rightTop;
    }

    public CvPoint getRightBottom() {
        return rightBottom;
    }

    public double getScrW() {
        return scrW;
    }

    public void setScrW(double scrW) {
        this.scrW = scrW;
    }

    public double getScrH() {
        return scrH;
    }

    public void setScrH(double scrH) {
        this.scrH = scrH;
    }

    /**
     * Creates new form CalibrationPanel
     */
    public CalibrationPanel() {
        initComponents();
        Toolkit screenSize = Toolkit.getDefaultToolkit();
        Dimension scrSize = screenSize.getScreenSize();
        double width = scrSize.getWidth();
        double height = scrSize.getHeight();
        setScrH(height);
        setScrW(width);
        System.out.println("Calibration panel dimension " + scrSize);
        /// setSize(scrSize);
        //   setSize(500, 500);
        setVisible(true);

        x = 20;
        y = (int) (scrH / 2);

        tm.start();

    }

    private Ellipse2D getEllipseFromCenter(double x, double y, double width, double height) {
        double newX = x - width / 2.0;
        double newY = y - height / 2.0;

        Ellipse2D ellipse = new Ellipse2D.Double(newX, newY, width, height);

        return ellipse;
    }

    public void paint(Graphics g) {
        textCounter++;

        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        int fontSize = 46;

        Font font = new Font("Serif", Font.PLAIN, fontSize);
        g2.setFont(font);


        if (textCounter > 0 && textCounter < 40 && AUTO == true) {
            g2.drawString("calibration will start at: ", (int) scrW / 2 - 200, (int) scrH / 2 + fontSize / 3);

        } else if (textCounter > 40 && textCounter < 60 && AUTO == true) {
            g2.drawString("5", (int) scrW / 2 + 30, (int) scrH / 2 + fontSize / 3);
        } else if (textCounter > 60 && textCounter < 80 && AUTO == true) {
            g2.drawString("4", (int) scrW / 2 + 30, (int) scrH / 2 + fontSize / 3);
        } else if (textCounter > 80 && textCounter < 100 && AUTO == true) {
            g2.drawString("3", 20 + 40 + (int) (scrW / 5) + 30, (int) scrH / 2 + fontSize / 3);
        } else if (textCounter > 100 && textCounter < 120 && AUTO == true) {
            g2.drawString("2", 40, (int) scrH / 2 + fontSize / 3);
        } else if (textCounter > 120 && textCounter < 140 && AUTO == true) {
            g2.drawString("1", 40, (int) scrH / 2 + fontSize / 3);
        } else if (textCounter > 140 && textCounter < 160 && AUTO == true) {
            CALIB_START = true;
        }
        size--;

        // System.out.println("Size ############## " + size);



        if (size < 2 && counter < calibCord.length + 1 && CALIB_START == true) {
            counter++;

            if (counter < calibCord.length) {
                setDefaultSize();
                x = calibCord[counter].x();
                y = calibCord[counter].y();
            }
        }

        if (size < 20) {
            POINT_READY = true;
        } else {
            POINT_READY = false;
        }


        if (counter > 10) {
            g2.drawString("calibration compleated: ", (int) scrW / 2 - 200, (int) scrH / 2 + fontSize / 3);
            tm.stop();
        }


        g2.setColor(Color.red);
        g2.fill(getEllipseFromCenter(x, y, size, size));
        g2.setColor(Color.black);
        g2.draw(getEllipseFromCenter(x, y, size, size));/// Srodek środek



        g2.draw(getEllipseFromCenter(scrW / 2, scrH / 2, 10, 10));/// Srodek środek
        g2.draw(getEllipseFromCenter(scrW / 2, scrH / 2, 4, 4));
        centerCenter = opencv_core.cvPoint((int) scrW / 2, (int) scrH / 2);

        g2.draw(getEllipseFromCenter(scrW / 2, 40 + (scrH / 5), 10, 10));/// Srodek góra 2/5
        g2.draw(getEllipseFromCenter(scrW / 2, 40 + (scrH / 5), 4, 4));
        centerTop25 = opencv_core.cvPoint((int) scrW / 2, 40 + (int) (scrH / 5));

        g2.draw(getEllipseFromCenter(scrW / 2, 20, 10, 10));/// Srodek góra
        g2.draw(getEllipseFromCenter(scrW / 2, 20, 4, 4));
        centerTop = opencv_core.cvPoint((int) scrW / 2, 20);

        g2.draw(getEllipseFromCenter(scrW / 2, scrH - 40 - (scrH / 5), 10, 10));/// Srodek dół 4/5
        g2.draw(getEllipseFromCenter(scrW / 2, scrH - 40 - (scrH / 5), 4, 4));
        centerBottom45 = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 40 - (int) (scrH / 5));

        g2.draw(getEllipseFromCenter(scrW / 2, scrH - 20, 10, 10));/// Srodek dół
        g2.draw(getEllipseFromCenter(scrW / 2, scrH - 20, 4, 4));
        centerBottom = opencv_core.cvPoint((int) scrW / 2, (int) scrH - 20);

        g2.draw(getEllipseFromCenter(20 + 40 + (scrW / 5), scrH / 2, 10, 10));/// Lewo środek 2/5
        g2.draw(getEllipseFromCenter(20 + 40 + (scrW / 5), scrH / 2, 4, 4));
        leftCenter25 = opencv_core.cvPoint(20 + 40 + (int) (scrW / 5), (int) scrH / 2);

        g2.draw(getEllipseFromCenter(20, scrH / 2, 10, 10));/// Lewo środek
        g2.draw(getEllipseFromCenter(20, scrH / 2, 4, 4));
        leftCenter = opencv_core.cvPoint(20, (int) scrH / 2);

        g2.draw(getEllipseFromCenter(20, 20, 10, 10));/// Lewo góra
        g2.draw(getEllipseFromCenter(20, 20, 4, 4));
        leftTop = opencv_core.cvPoint(20, 20);

        g2.draw(getEllipseFromCenter(20, scrH - 20, 10, 10));/// Lewo dół
        g2.draw(getEllipseFromCenter(20, scrH - 20, 4, 4));
        leftBottom = opencv_core.cvPoint(20, (int) scrH - 20);

        g2.draw(getEllipseFromCenter(scrW - 20, scrH / 2, 10, 10));/// Prawo środek
        g2.draw(getEllipseFromCenter(scrW - 20, scrH / 2, 4, 4));
        rightCenter = opencv_core.cvPoint((int) scrW - 20, (int) scrH / 2);


        g2.draw(getEllipseFromCenter(scrW - 20 - 40 - (scrW / 5), scrH / 2, 10, 10));/// Prawo środek 4/5
        g2.draw(getEllipseFromCenter(scrW - 20 - 40 - (scrW / 5), scrH / 2, 4, 4));
        rightCenter45 = opencv_core.cvPoint((int) scrW - 20 - 40 - (int) (scrW / 5), (int) scrH / 2);


        g2.draw(getEllipseFromCenter(scrW - 20, 20, 10, 10));/// Prawo góra
        g2.draw(getEllipseFromCenter(scrW - 20, 20, 4, 4));
        rightTop = opencv_core.cvPoint((int) scrW - 20, 20);

        g2.draw(getEllipseFromCenter(scrW - 20, scrH - 20, 10, 10));/// Prawo dół
        g2.draw(getEllipseFromCenter(scrW - 20, scrH - 20, 4, 4));
        rightBottom = opencv_core.cvPoint((int) scrW - 20, (int) scrH - 20);



    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

    }
}
