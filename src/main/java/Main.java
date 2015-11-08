import io.atlassian.fugue.Option;
import service.SimpleSimilarityRanker;
import similarity.ImageFeatureExtractor;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("This application requires two parameters: "
                    + "the name of a directory containing JPEG images, and a file name of a JPEG image.");
            return;
        }

        ArrayList<ImageFeatureExtractor.ImageInImageDatabase> databaseIndex = SimpleSimilarityRanker
                .rankAndIndexImagesBasedOnInputtedImage(args[1], args[0],
                    Option.some(SimpleSimilarityRanker.SimilarityFeature.SIFT),
                    Option.some(SimpleSimilarityRanker.SimilarityCalculator.ABSOLUTE_DIFF));

        databaseIndex.forEach(index -> {
            System.out.println(index.fileName.concat(" has distance to ").concat(args[1]).concat(" = ").concat(Double.toString(index.distanceToSearchImage)));
        });
    }
}
