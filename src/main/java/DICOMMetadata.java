import org.dcm4che3.data.VR;

public class DICOMMetadata {
    private int tagName;
    private VR vr;
    private String value;
    private String tagAddress;

    public int getTagName() {
        return tagName;
    }

    public VR getVR() {
        return vr;
    }

    public String getValue() {
        return value;
    }

    public void setVr(VR vr) {
        this.vr = vr;
    }

    public void setTagName(int tagName) {
        this.tagName = tagName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }
}
