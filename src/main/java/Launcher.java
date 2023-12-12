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

        String workingDir = System.getProperty("user.dir");
        String inputDicomFilePath = workingDir + "/EJEMPLO.dcm";
        String outputDicomFilePath = workingDir + "/EJEMPLO_MODIFICADO.dcm";


        try {

            // Lee todos los metadatos de una imágen DICOM
            ArrayList<DICOMMetadata> allMetadata = readMetadata(inputDicomFilePath);
            for (DICOMMetadata metadata : allMetadata) {
                System.out.println("Tag Address: " + metadata.getTagAddress() + " VR: " + metadata.getVR() + " Value" + ":" + " " + metadata.getValue());
            }

            System.out.println("----------------------------------------");

            // Lee un atributo específico de una imágen DICOM
            DICOMMetadata dicomMetadata = readTagFromDICOMImage(inputDicomFilePath, Tag.PatientID);
            System.out.println("Tag: " + dicomMetadata.getTagAddress() + " Value: " + dicomMetadata.getValue());


            System.out.println("----------------------------------------");

            // Modifica atributos de una imágen DICOM

            // El VR y el Tag Name se recuperan con el read, aquí solo cambiamos el valor.
            dicomMetadata = readTagFromDICOMImage(inputDicomFilePath, Tag.PatientID);
            dicomMetadata.setValue("ID DE PACIENTE MODIFICADO");

            // Es un array, si hace falta modificar varios atributos, se añaden más objetos con distintos atributos
            ArrayList<DICOMMetadata> attributeList = new ArrayList<>();
            attributeList.add(dicomMetadata);

            dicomMetadata = readTagFromDICOMImage(inputDicomFilePath, Tag.PatientName);
            dicomMetadata.setValue("NOMBRE DE PACIENTE MODIFICADO");
            attributeList.add(dicomMetadata);

            modifyMetadata(inputDicomFilePath, outputDicomFilePath, attributeList);

            System.out.println("Original");
            System.out.println("Patient ID " + readTagFromDICOMImage(inputDicomFilePath, Tag.PatientID).getValue());
            System.out.println("Patient Name " + readTagFromDICOMImage(inputDicomFilePath, Tag.PatientName).getValue());
            System.out.println("Modificado");
            System.out.println("Patient ID " + readTagFromDICOMImage(outputDicomFilePath, Tag.PatientID).getValue());
            System.out.println("Patient Name " + readTagFromDICOMImage(outputDicomFilePath, Tag.PatientName).getValue());


        } catch (IOException e) {
            System.out.println("Error due to: " + e.getMessage());
        }
    }


    /**
     * Lee y devuelve todos los metadatos de una imágen DICOM
     *
     * @param dicomFilePath Path a la imágen DICOM
     * @return Un Array con todos los datos de la imágen. Array vacío si no hay datos.
     * @throws IOException Si la fila no se encuentra
     */
    private static ArrayList<DICOMMetadata> readMetadata(String dicomFilePath) throws IOException {
        File file = new File(dicomFilePath);
        DicomInputStream dis = new DicomInputStream(file);
        Attributes attributes = dis.readDataset();
        int[] tags = attributes.tags();

        ArrayList<DICOMMetadata> allMetadata = new ArrayList<>();

        for (int tag : tags) {
            DICOMMetadata metadata = new DICOMMetadata();
            metadata.setTagAddress(TagUtils.toString(tag));
            metadata.setVr(attributes.getVR(tag));
            metadata.setValue(attributes.getString(tag));
            metadata.setTagName(convertTagAddressToDecimal(metadata.getTagAddress()));
            allMetadata.add(metadata);
        }

        dis.close();
        return allMetadata;
    }


    /**
     * Modifica los atributos de una imágen DICOM original, y almacena la imágen modificada en otro sitio
     *
     * @param inputDicomPath     Path a la imágen original
     * @param outputDicomPath    Path para guardar la imágen modificada
     * @param attributesToModify atributos a modificar de la ímagen DICOM, contiene su identificador, su valor VR y
     *                           el valor a modificar
     * @throws IOException Si no es capaz de encontrar la fila a abrir
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
     * Lee los valores asociados a una etiqueta específica de una imágen DICOM
     *
     * @param imagePath Path a la imágen DICOM que leer
     * @param tagName   Valor en decimal del TagAddress que se quiere leer (Se puede usar Tag. para obtener una lista
     *                  con los valores)
     * @return Una entidad con los metadatos, si existe. Si no existen, devuelve null.
     * @throws IOException Si no es capaz de abrir la imágen DICOM
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
     * @param tagAddress Valor hexadecimal del Tag Address
     * @return el equivalente en decimal
     */
    public static int convertTagAddressToDecimal(String tagAddress) {
        String hexNumber = tagAddress.replace("(", "").replace(")", "").replace(",", "");
        return Integer.parseInt(hexNumber, 16);
    }

}