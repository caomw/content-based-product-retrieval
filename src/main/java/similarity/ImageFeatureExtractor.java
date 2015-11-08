package similarity;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.Tamura;
import net.semanticmetadata.lire.imageanalysis.opencvfeatures.CvSiftFeature;
import net.semanticmetadata.lire.imageanalysis.opencvfeatures.CvSurfFeature;
import net.semanticmetadata.lire.impl.CvSiftDocumentBuilder;
import net.semanticmetadata.lire.impl.CvSurfDocumentBuilder;
import net.semanticmetadata.lire.impl.SimpleBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;

public class ImageFeatureExtractor {

    public static enum Version {
        V1,
        V2
    }

    public static Double compareTwoImages() {
        return 0.0;
    }

    public static class ImageInImageDatabase {

        public String fileName;
        public double[] fcthFeatureVector;
        public double distanceToSearchImage;
    }

    public static class ImageComparator implements Comparator<ImageInImageDatabase> {

        @Override
        public int compare(ImageInImageDatabase object1, ImageInImageDatabase object2) {
            if (object1.distanceToSearchImage < object2.distanceToSearchImage) {
                return -1;
            } else if (object1.distanceToSearchImage > object2.distanceToSearchImage) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    //fuzzy color and texture histogram
    public static double[] getFcthFeatureVector(String fullFilePath) throws IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getFCTHDocumentBuilder(), new FCTH());
    }

    //color and edge directivity
    public static double[] getCeddFeatureVector(String fullFilePath) throws IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getCEDDDocumentBuilder(), new CEDD());
    }

    //scale invariant feature transform: local feature (ROI) - SIFT
    public static double[] getSiftFeatureVector(String fullFilePath) throws IOException {
        return getFeatureVector(fullFilePath, new CvSiftDocumentBuilder(), new CvSiftFeature());
    }

    //speeded up robust feature: local feature (ROI)- SURF
    public static double[] getSurfFeatureVector(String fullFilePath) throws IOException {
        return getFeatureVector(fullFilePath, new CvSurfDocumentBuilder(), new CvSurfFeature());
    }

    //Tamura algorithm
    public static double[] getTamuraFeatureVector(String fullFilePath) throws IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getTamuraDocumentBuilder(), new Tamura());
    }

    //Simple builder with various keypoint dectectors and features - SLOW, should be in parallel and create the index directly
    public static double[] getSimpleFeatureVector(String fullFilePath, LireFeature lireFeature, SimpleBuilder.KeypointDetector keypointDetector, Version version) throws IOException {
        if (version == Version.V1) {
            return getFeatureVector(fullFilePath, new SimpleBuilder(lireFeature, keypointDetector), lireFeature);
        } else {
            return getFeatureVectorV2(fullFilePath, new SimpleBuilder(lireFeature, keypointDetector), lireFeature);
        }
    }

    private static double[] getFeatureVector(String fullFilePath, DocumentBuilder builder, LireFeature lireFeature) throws IOException {
        FileInputStream inputStream = new FileInputStream(fullFilePath);
        Document document = builder.createDocument(inputStream, fullFilePath);
        inputStream.close();

        lireFeature.setByteArrayRepresentation(document.getFields().get(1).binaryValue().bytes);
        return lireFeature.getDoubleHistogram();
    }

    private static double[] getFeatureVectorV2(String fullFilePath, DocumentBuilder builder, LireFeature lireFeature) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(fullFilePath));
        Field[] fields = builder.createDescriptorFields(bufferedImage);

        lireFeature.setByteArrayRepresentation(fields[0].binaryValue().bytes);
        return lireFeature.getDoubleHistogram();
    }
}
