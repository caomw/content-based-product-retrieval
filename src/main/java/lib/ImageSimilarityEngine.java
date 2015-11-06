package lib;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import org.apache.lucene.document.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;

public class ImageSimilarityEngine {

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

    public static double[] getFCTHFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {

        DocumentBuilder builder = DocumentBuilderFactory.getFCTHDocumentBuilder();
        FileInputStream istream = new FileInputStream(fullFilePath);
        Document doc = builder.createDocument(istream, fullFilePath);
        istream.close();

        FCTH fcthDescriptor = new FCTH();
        fcthDescriptor.setByteArrayRepresentation(doc.getFields().get(1).binaryValue().bytes);

        return fcthDescriptor.getDoubleHistogram();

    }

    public static double calculateEuclideanDistance(double[] vector1, double[] vector2) {

        double innerSum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            innerSum += Math.pow(vector1[i] - vector2[i], 2.0);
        }

        return Math.sqrt(innerSum);

    }
}
