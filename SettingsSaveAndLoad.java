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

/**
 *
 * @author XXX
 */
public class SettingsSaveAndLoad {

    public static void saveEyESettings(SettingModel settings, String fileName) {

        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream(fileName + ".settings"));
            xmlEncoder.writeObject(settings);
            xmlEncoder.close();

        } catch (Exception e) {
            System.out.println("Settings save error");
            e.printStackTrace();
        }
    }

    public static SettingModel loadEyeSettings(String fileName) throws FileNotFoundException {

        XMLDecoder dec = new XMLDecoder(new FileInputStream(fileName + ".settings"));
        SettingModel b = (SettingModel) dec.readObject();
        return b;

    }
}
