import lib.ImageSimilarityEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length != 2) {

            System.out.println("This application requires two parameters: "
                    + "the name of a directory containing JPEG images, and a file name of a JPEG image.");
            return;

        }

        String imageDatabaseDirectoryName = args[0];
        String searchImageFilePath = args[1];

        double[] searchImageFeatureVector = ImageSimilarityEngine.getFCTHFeatureVector(searchImageFilePath);

        System.out.println("Search image FCTH vector: " + Arrays.toString(searchImageFeatureVector));

        ArrayList<ImageSimilarityEngine.ImageInImageDatabase> database = new ArrayList();

        File directory = new File(imageDatabaseDirectoryName);

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jpg") || name.endsWith(".jpeg");
            }
        };

        String[] fileNames = directory.list(filter);

        for (String fileName : fileNames) {

            double[] fcthFeatureVector = ImageSimilarityEngine.getFCTHFeatureVector(imageDatabaseDirectoryName + fileName);
            double distanceToSearchImage = ImageSimilarityEngine.calculateEuclideanDistance(fcthFeatureVector, searchImageFeatureVector);

            ImageSimilarityEngine.ImageInImageDatabase imageInImageDatabase = new ImageSimilarityEngine.ImageInImageDatabase();

            imageInImageDatabase.fileName = fileName;
            imageInImageDatabase.fcthFeatureVector = fcthFeatureVector;
            imageInImageDatabase.distanceToSearchImage = distanceToSearchImage;

            database.add(imageInImageDatabase);

        }

        Collections.sort(database, new ImageSimilarityEngine.ImageComparator());

        for (ImageSimilarityEngine.ImageInImageDatabase result : database) {

            System.out.println("Distance " + Double.toString(result.distanceToSearchImage) + ": " + result.fileName);

        }

    }
}
