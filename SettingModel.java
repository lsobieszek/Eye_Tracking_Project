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
public class SettingModel implements Serializable {

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
    private int brightPupil;

    public int getInpaintSize() {
        return inpaintSize;
    }

    public int getBrightPupil() {
        return brightPupil;
    }

    public void setBrightPupil(int brightPupil) {
        this.brightPupil = brightPupil;
    }

    public void setInpaintSize(int inpaintSize) {
        this.inpaintSize = inpaintSize;
    }

    public int getGlintBlockSize() {
        return glintBlockSize;
    }

    public void setGlintBlockSize(int glintBlockSize) {
        this.glintBlockSize = glintBlockSize;
    }

    public int getGlintDif() {
        return glintDif;
    }

    public void setGlintDif(int glintDif) {
        this.glintDif = glintDif;
    }

    public int getGlintDylate() {
        return glintDylate;
    }

    public void setGlintDylate(int glintDylate) {
        this.glintDylate = glintDylate;
    }

    public int getGlintSmooth() {
        return glintSmooth;
    }

    public void setGlintSmooth(int glintSmooth) {
        this.glintSmooth = glintSmooth;
    }

    public int getPupilErode() {
        return pupilErode;
    }

    public void setPupilErode(int pupilErode) {
        this.pupilErode = pupilErode;
    }

    public int getPupilSmooth() {
        return pupilSmooth;
    }

    public void setPupilSmooth(int pupilSmooth) {
        this.pupilSmooth = pupilSmooth;
    }

    public int getPupilBlockSize() {
        return pupilBlockSize;
    }

    public void setPupilBlockSize(int pupilBlockSize) {
        this.pupilBlockSize = pupilBlockSize;
    }

    public int getPupilDif() {
        return pupilDif;
    }

    public void setPupilDif(int pupilDif) {
        this.pupilDif = pupilDif;
    }

    public int getPupilMaxSize() {
        return pupilMaxSize;
    }

    public void setPupilMaxSize(int pupilMaxSize) {
        this.pupilMaxSize = pupilMaxSize;
    }

    public int getPupilMinSize() {
        return pupilMinSize;
    }

    public void setPupilMinSize(int pupilMinSize) {
        this.pupilMinSize = pupilMinSize;
    }
}
