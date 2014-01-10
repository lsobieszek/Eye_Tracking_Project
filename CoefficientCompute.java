/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

/**
 *
 * @author XXX
 */
public class CoefficientCompute {

    private double a;
    private double b;
    private double c;
    private double d;
    private double e;

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public double getE() {
        return e;
    }

    public void countCoefficient(double x1, double y1, double x2,
            double y2, double x3, double y3, double x4, double y4, double x5, double y5,
            double X1, double X2, double X3, double X4, double X5) {

        double den;
        double x22, x32, x42, x52;    // squared terms 
        double y22, y32, y42, y52;

        //inx = (int)x1;            // record eye tracker centering constants 
        //iny = (int)y1;
        a = X1;                    // first coefficient 
        X2 -= X1;
        X3 -= X1;       // center screen points 
        X4 -= X1;
        X5 -= X1;
        x2 -= x1;
        x3 -= x1;       // center eye tracker points 
        x4 -= x1;
        x5 -= x1;
        y2 -= y1;
        y3 -= y1;
        y4 -= y1;
        y5 -= y1;
        x22 = x2 * x2;
        x32 = x3 * x3;   // squared terms of biquadratic 
        x42 = x4 * x4;
        x52 = x5 * x5;
        y22 = y2 * y2;
        y32 = y3 * y3;
        y42 = y4 * y4;
        y52 = y5 * y5;

        den = -x2 * y3 * x52 * y42 - x22 * y3 * x4 * y52 + x22 * y5 * x4 * y32 - y22 * x42 * y3 * x5
                - x32 * y22 * x4 * y5 - x42 * x2 * y5 * y32 + x32 * x2 * y5 * y42 - y2 * x52 * x4 * y32
                + x52 * x2 * y4 * y32 + y22 * x52 * y3 * x4 + y2 * x42 * x5 * y32 + x22 * y3 * x5 * y42
                - x32 * x2 * y4 * y52 - x3 * y22 * x52 * y4 + x32 * y22 * x5 * y4 - x32 * y2 * x5 * y42
                + x3 * y22 * x42 * y5 + x3 * y2 * x52 * y42 + x32 * y2 * x4 * y52 + x42 * x2 * y3 * y52
                - x3 * y2 * x42 * y52 + x3 * x22 * y4 * y52 - x22 * y4 * x5 * y32 - x3 * x22 * y5 * y42;

        b = (-y32 * y2 * x52 * X4 - X2 * y3 * x52 * y42 - x22 * y3 * X4 * y52 + x22 * y3 * y42 * X5
                + y32 * y2 * x42 * X5 - y22 * x42 * y3 * X5 + y22 * y3 * x52 * X4 + X2 * x42 * y3 * y52
                + X3 * y2 * x52 * y42 - X3 * y2 * x42 * y52 - X2 * x42 * y5 * y32 + x32 * y42 * y5 * X2
                + X2 * x52 * y4 * y32 - x32 * y4 * X2 * y52 - x32 * y2 * y42 * X5 + x32 * y2 * X4 * y52
                + X4 * x22 * y5 * y32 - y42 * x22 * y5 * X3 - x22 * y4 * y32 * X5 + x22 * y4 * X3 * y52
                + y22 * x42 * y5 * X3 + x32 * y22 * y4 * X5 - y22 * x52 * y4 * X3 - x32 * y22 * y5 * X4) / den;

        c = (-x32 * x4 * y22 * X5 + x32 * x5 * y22 * X4 - x32 * y42 * x5 * X2 + x32 * X2 * x4 * y52
                + x32 * x2 * y42 * X5 - x32 * x2 * X4 * y52 - x3 * y22 * x52 * X4 + x3 * y22 * x42 * X5
                + x3 * x22 * X4 * y52 - x3 * X2 * x42 * y52 + x3 * X2 * x52 * y42 - x3 * x22 * y42 * X5
                - y22 * x42 * x5 * X3 + y22 * x52 * x4 * X3 + x22 * y42 * x5 * X3 - x22 * x4 * X3 * y52
                - x2 * y32 * x42 * X5 + X2 * x42 * x5 * y32 + x2 * X3 * x42 * y52 + x2 * y32 * x52 * X4
                + x22 * x4 * y32 * X5 - x22 * X4 * x5 * y32 - X2 * x52 * x4 * y32 - x2 * X3 * x52 * y42) / den;

        d = -(-x4 * y22 * y3 * X5 + x4 * y22 * y5 * X3 - x4 * y2 * X3 * y52 + x4 * y2 * y32 * X5
                - x4 * y32 * y5 * X2 + x4 * y3 * X2 * y52 - x3 * y22 * y5 * X4 + x3 * y22 * y4 * X5
                + x3 * y2 * X4 * y52 - x3 * y2 * y42 * X5 + x3 * y42 * y5 * X2 - x3 * y4 * X2 * y52
                - y22 * y4 * x5 * X3 + y22 * X4 * y3 * x5 - y2 * X4 * x5 * y32 + y2 * y42 * x5 * X3
                + x2 * y3 * y42 * X5 - y42 * y3 * x5 * X2 + X4 * x2 * y5 * y32 + y4 * X2 * x5 * y32
                - y42 * x2 * y5 * X3 - x2 * y4 * y32 * X5 + x2 * y4 * X3 * y52 - x2 * y3 * X4 * y52) / den;

        e = -(-x3 * y2 * x52 * X4 + x22 * y3 * x4 * X5 + x22 * y4 * x5 * X3 - x3 * x42 * y5 * X2
                - x42 * x2 * y3 * X5 + x42 * x2 * y5 * X3 + x42 * y3 * x5 * X2 - y2 * x42 * x5 * X3
                + x32 * x2 * y4 * X5 - x22 * y3 * x5 * X4 + x32 * y2 * x5 * X4 - x22 * y5 * x4 * X3
                + x2 * y3 * x52 * X4 - x52 * x2 * y4 * X3 - x52 * y3 * x4 * X2 - x32 * y2 * x4 * X5
                + x3 * x22 * y5 * X4 + x3 * y2 * x42 * X5 + y2 * x52 * x4 * X3 - x32 * x5 * y4 * X2
                - x32 * x2 * y5 * X4 + x3 * x52 * y4 * X2 + x32 * x4 * y5 * X2 - x3 * x22 * y4 * X5) / den;

    }
}
