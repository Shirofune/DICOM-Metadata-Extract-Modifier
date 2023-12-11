import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.TagUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args) {
        String inputDicomFilePath = "C:\\Salud\\eCieMaps\\workspaceIntelliji\\DICOM-Metadata-Extract\\EJEMPLO.dcm";
        String outputDicomFilePath = "C:\\Salud\\eCieMaps\\workspaceIntelliji\\DICOM-Metadata-Extract" +
                "\\EJEMPLO_MODIFICADO.dcm";
        try {


            DICOMMetadata dicomMetadata = readTagFromDICOMImage(inputDicomFilePath, Tag.PatientID);

            System.out.println(convertTagAddressToDecimal(dicomMetadata.getTagAddress()));
//            dicomMetadata.setValue("Nombre de Paciente modificado!");
//
//            ArrayList<DICOMMetadata> attributeList = new ArrayList<>();
//            attributeList.add(dicomMetadata);
//
//            modifyMetadata(inputDicomFilePath, outputDicomFilePath, attributeList);
//
//            System.out.println(readTagFromDICOMImage(inputDicomFilePath, Tag.PatientName).getValue());
//            System.out.println(readTagFromDICOMImage(outputDicomFilePath, Tag.PatientName).getValue());


        } catch (IOException e) {
            System.out.println("Error due to: " + e.getMessage());
        }
    }

    private static void readHeader(String inputPath) throws IOException {
        File file = new File(inputPath);
        DicomInputStream dis = new DicomInputStream(file);
        Attributes attributes = dis.readDataset();
        int[] tags = attributes.tags();
        System.out.println("Total tags found in the given dicom file: " + tags.length);
        for (int tag : tags) {
            String tagAddress = TagUtils.toString(tag);
            String vr = attributes.getVR(tag).toString();
            System.out.println("Tag Address: " + tagAddress + " VR: " + vr);
        }
        dis.close();
    }

    /**
     * Lee todos los valores de metadata de una imagen DICOM
     *
     * @param dicomFilePath
     * @throws IOException
     */
    private static void readMetadata(String dicomFilePath) throws IOException {
        File file = new File(dicomFilePath);
        DicomInputStream dis = new DicomInputStream(file);
        Attributes attributes = dis.readDataset();
        int[] tags = attributes.tags();
        System.out.println("Total tags found in dicom file: " + tags.length);
        for (int tag : tags) {
            String tagAddress = TagUtils.toString(tag);
            String vr = attributes.getVR(tag).toString();
            String tagValue = attributes.getString(tag);
            System.out.println("Tag Address: " + tagAddress + " VR: " + vr + " Value: " + tagValue);
        }
        dis.close();
    }


    /**
     * Modifica los atributos de una imágen DICOM original, y almacena la imágen modificada en otro sitio
     *
     * @param inputDicomPath
     * @param outputDicomPath
     * @param attributesToModify atributos a modificar de la ímagen DICOM, contiene su identificador, su valor VR y
     *                           el valor a modificar
     * @throws IOException
     */
    private static void modifyMetadata(String inputDicomPath, String outputDicomPath,
                                       ArrayList<DICOMMetadata> attributesToModify) throws IOException {
        File file = new File(inputDicomPath);
        DicomInputStream dis = new DicomInputStream(file);
        Attributes attributes = dis.readDataset();

        for (DICOMMetadata dicomTag : attributesToModify) {
            attributes.setString(dicomTag.getTagName(), dicomTag.getVR(), dicomTag.getValue());
        }

        DicomOutputStream dos = new DicomOutputStream(new File(outputDicomPath));
        attributes.writeTo(dos);
        dis.close();
        dos.close();
    }

    /**
     * Lee los valores de una etiqueta en concreto de una imágen DICOM
     *
     * @param imagePath Path a la imágen
     * @throws IOException
     */
    private static DICOMMetadata readTagFromDICOMImage(String imagePath, int tagName) throws IOException {
        File file = new File(imagePath);
        DICOMMetadata returnValue = null;
        DicomInputStream dis = new DicomInputStream(file);
        Attributes attributes = dis.readDataset();
        if (attributes.contains(tagName)) {
            returnValue = new DICOMMetadata();
            returnValue.setTagName(tagName);
            returnValue.setValue(attributes.getString(tagName));
            returnValue.setVr(attributes.getVR(tagName));
            returnValue.setTagAddress(Integer.toHexString(tagName));
        }
        dis.close();
        return returnValue;
    }

    /**
     * Convierte una tag address a su equivalente en decimal
     *
     * @param tagAddress
     * @return el equivalente en decimal
     */
    public static int convertTagAddressToDecimal(String tagAddress) {
        String hexNumber = tagAddress.replace("(", "").replace(")", "").replace(",", "");
        return Integer.parseInt(hexNumber, 16);
    }

}