package lib;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.*;
import net.semanticmetadata.lire.imageanalysis.opencvfeatures.CvSiftFeature;
import net.semanticmetadata.lire.imageanalysis.opencvfeatures.CvSurfFeature;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.CvSiftDocumentBuilder;
import net.semanticmetadata.lire.impl.CvSurfDocumentBuilder;
import net.semanticmetadata.lire.impl.SimpleBuilder;
import net.semanticmetadata.lire.indexing.parallel.ParallelIndexer;
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

    //fuzzy color and texture histogram
    public static double[] getFcthFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getFCTHDocumentBuilder(), new FCTH());
    }

    //color and edge directivity
    public static double[] getCeddFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getCEDDDocumentBuilder(), new CEDD());
    }

    //scale invariant feature transform: local feature (ROI) - SIFT
    public static double[] getSiftFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, new CvSiftDocumentBuilder(), new CvSiftFeature());
    }

    //speeded up robust feature: local feature (ROI)- SURF
    public static double[] getSurfFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, new CvSurfDocumentBuilder(), new CvSurfFeature());
    }

    //Tamura algorithm
    public static double[] getTamuraFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, DocumentBuilderFactory.getTamuraDocumentBuilder(), new Tamura());
    }

    private static double[] getFeatureVector(String fullFilePath, DocumentBuilder builder, LireFeature lireFeature) throws FileNotFoundException, IOException {
        FileInputStream inputStream = new FileInputStream(fullFilePath);
        Document document = builder.createDocument(inputStream, fullFilePath);
        inputStream.close();

        lireFeature.setByteArrayRepresentation(document.getFields().get(1).binaryValue().bytes);
        return lireFeature.getDoubleHistogram();
    }

    public static double[] getSimpleFeatureVector(String fullFilePath) throws FileNotFoundException, IOException {
        return getFeatureVector(fullFilePath, new SimpleBuilder(), new SimpleColorHistogram());
    }

    private static double[] getGenericFeatureVector(String fullFilePath, DocumentBuilder builder) throws FileNotFoundException, IOException {

        return null;
    }

    //Searching Images with Mpeg-7 (& Mpeg-7 like) Powered Localized descriptors: SIMPLE is a collection od SCD, CLD, EHD, CEDD
    public static void getSimpleFeatureVector(int numThreads, String indexDirectory, String imageDirectory) throws FileNotFoundException, IOException {
        ParallelIndexer parallelIndexer = new ParallelIndexer(numThreads, indexDirectory, imageDirectory);
        parallelIndexer.addBuilders(new ChainedDocumentBuilder());
        parallelIndexer.run();
    }

    public static double calculateEuclideanDistance(double[] vector1, double[] vector2) {

        double innerSum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            innerSum += Math.pow(vector1[i] - vector2[i], 2.0);
        }

        return Math.sqrt(innerSum);

    }
}
