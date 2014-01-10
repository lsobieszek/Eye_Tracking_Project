/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import java.io.Serializable;

/**
 *
 * @author XXX
 */
public class EstimationResults implements Serializable {

    private int x;
    private int y;
    private int time;

//    public EstimationResults(int x, int y, int time){
//        this.x = x;
//        this.y = y;
//        this.time = time;
//    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
