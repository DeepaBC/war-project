package ejava.examples.restintro.dmv.lic.dto;

import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the physical details used to decribe a resident
 * or driver.
 */
@XmlType(name="PhysicalDetailsType", namespace=DrvLicRepresentation.DRVLIC_NAMESPACE, propOrder={
        "sex", "height", "weight", "hairColor", "eyeColor"
})
public class PhysicalDetails {
    public enum HairColor{ BROWN, BLONDE, GREY, BALD };
    public enum EyeColor { GREEN, BLUE, BROWN };
    public enum Sex { M, F };
    private Sex sex;
    private int height;
    private int weight;
    private HairColor hairColor;
    private EyeColor eyeColor;

    public Sex getSex() {
        return sex;
    }
    public void setSex(Sex sex) {
        this.sex = sex;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public HairColor getHairColor() {
        return hairColor;
    }
    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }
    public EyeColor getEyeColor() {
        return eyeColor;
    }
    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }
}
