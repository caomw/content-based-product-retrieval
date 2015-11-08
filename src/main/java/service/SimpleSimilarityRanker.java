package service;

import net.semanticmetadata.lire.imageanalysis.opencvfeatures.CvSurfFeature;
import net.semanticmetadata.lire.impl.SimpleBuilder;
import similarity.ImageFeatureExtractor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SimpleSimilarityRanker {

    public static enum SimilarityFeature {
        FCTH,
        CEDD,
        TAMURA,
        SURF,
        SIFT,
        SIMPLE //slow, it's not recommended for now, by default use SURF
    }

    public static enum SimilarityCalculator {
        EUCLIDEAN_DISTANCE,
        ABSOLUTE_DIFF
    }

    public static ArrayList<ImageFeatureExtractor.ImageInImageDatabase> rankAndIndexImagesBasedOnInputtedImage(String fullFilePath, String imageDatabaseDirectory, SimilarityFeature similarityFeature, SimilarityCalculator similarityCalculator) {
        ArrayList<ImageFeatureExtractor.ImageInImageDatabase> databaseIndex = new ArrayList();

        try {
            String imageFilePath = fullFilePath;
            String imageDatabaseDirectoryName = imageDatabaseDirectory;

            double[] searchImageFeatureVector = selectFeatureVector(similarityFeature, imageFilePath);

            File directory = new File(imageDatabaseDirectoryName);

            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jpg") || name.endsWith(".jpeg");
                }
            };

            String[] fileNames = directory.list(filter);

            for (String fileName : fileNames) {

                double[] featureVector = selectFeatureVector(similarityFeature, imageDatabaseDirectoryName + fileName);
                double distanceToSearchImage = selectDistanceCalc(similarityCalculator, featureVector, searchImageFeatureVector);

                ImageFeatureExtractor.ImageInImageDatabase imageInImageDatabase = new ImageFeatureExtractor.ImageInImageDatabase();

                imageInImageDatabase.fileName = fileName;
                imageInImageDatabase.fcthFeatureVector = featureVector;
                imageInImageDatabase.distanceToSearchImage = distanceToSearchImage;

                databaseIndex.add(imageInImageDatabase);

            }

            Collections.sort(databaseIndex, new ImageFeatureExtractor.ImageComparator());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseIndex;
    }

    //default is TAMURA
    private static double[] selectFeatureVector(SimilarityFeature similarityFeature, String fullFilePath) throws IOException{
        switch (similarityFeature) {
            case FCTH: {
                return ImageFeatureExtractor.getFcthFeatureVector(fullFilePath);
            }
            case CEDD: {
                return ImageFeatureExtractor.getCeddFeatureVector(fullFilePath);
            }
            case SURF: {
                return ImageFeatureExtractor.getSurfFeatureVector(fullFilePath);
            }
            case SIFT: {
                return ImageFeatureExtractor.getSiftFeatureVector(fullFilePath);
            }
            case SIMPLE: {
                return ImageFeatureExtractor.getSimpleFeatureVector(fullFilePath, new CvSurfFeature(), SimpleBuilder.KeypointDetector.CVSURF, ImageFeatureExtractor.Version.V1);
            }
            case TAMURA: {
                return ImageFeatureExtractor.getTamuraFeatureVector(fullFilePath);
            }
            default: {
                return ImageFeatureExtractor.getTamuraFeatureVector(fullFilePath);
            }
        }
    }

    //similarity calculator
    private static double calculateEuclideanDistance(double[] vector1, double[] vector2) {

        double innerSum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            innerSum += Math.pow(vector1[i] - vector2[i], 2.0);
        }

        return Math.sqrt(innerSum);

    }

    //similarity calculator
    private static double calculateAbsoluteDifference(double[] vector1, double[] vector2) {

        double innerSum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            innerSum += Math.abs(vector1[i] - vector2[i]);
        }

        return innerSum;

    }

    //default euclidean
    private static double selectDistanceCalc(SimilarityCalculator similarityCalculator, double[] vector1, double[] vector2) {
        switch (similarityCalculator) {
            case EUCLIDEAN_DISTANCE: {
                return calculateEuclideanDistance(vector1, vector2);
            }
            case ABSOLUTE_DIFF: {
                return calculateAbsoluteDifference(vector1, vector2);
            }
            default: {
                return calculateEuclideanDistance(vector1, vector2);
            }
        }
    }
}
