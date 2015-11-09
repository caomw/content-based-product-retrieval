package cbpr;

import io.atlassian.fugue.Option;
import cbpr.service.SimpleSimilarityRanker;
import cbpr.similarity.ImageFeatureExtractor;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] paths = args;
        if (args.length != 2) {
            paths = new String[2];
            paths[1] = "/images/gucci1.jpeg";
            paths[0] = "/images/search/";
        }
        final String filePath = paths[1];

        ArrayList<ImageFeatureExtractor.ImageInImageDatabase> databaseIndex = SimpleSimilarityRanker
                .rankAndIndexImagesBasedOnInputtedImage(paths[1], paths[0],
                    Option.some(SimpleSimilarityRanker.SimilarityFeature.SIFT),
                    Option.some(SimpleSimilarityRanker.SimilarityCalculator.ABSOLUTE_DIFF));

        databaseIndex.forEach(index -> {
            System.out.println(index.fileName.concat(" has distance to ").concat(filePath).concat(" = ").concat(Double.toString(index.distanceToSearchImage)));
        });
    }
}
