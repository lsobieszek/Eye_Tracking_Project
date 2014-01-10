/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EyeTracking;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 *
 * @author XXX
 */
public class VectorSaveAndLoad {

    public static void saveVectorCoordinates(Vector[] vecArray, String fileName) {

        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream(fileName + ".vec"));
            xmlEncoder.writeObject(vecArray);
            xmlEncoder.close();

        } catch (Exception e) {
            System.out.println("VectorSave error");
            e.printStackTrace();
        }
    }

    public static Vector[] loadVectorCoordinates(String fileName) throws FileNotFoundException {

        XMLDecoder dec = new XMLDecoder(new FileInputStream(fileName + ".vec"));
        Vector[] b = (Vector[]) dec.readObject();
        return b;

    }

    public static void saveEstimationResults(ArrayList<EstimationResults> er, String fileName) {

        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream(fileName + ".vec"));
            xmlEncoder.writeObject(er);
            xmlEncoder.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Estimation Resaults save error ");
        }

    }

    public static ArrayList<EstimationResults> loadEstimatedResults(String fileName) throws FileNotFoundException {

        XMLDecoder dec = new XMLDecoder(new FileInputStream(fileName + ".vec"));
        ArrayList<EstimationResults> b = (ArrayList<EstimationResults>) dec.readObject();
        return b;

    }
}
