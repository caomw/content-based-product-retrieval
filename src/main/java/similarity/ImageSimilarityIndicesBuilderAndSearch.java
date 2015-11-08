package similarity;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;
import net.semanticmetadata.lire.indexing.parallel.ParallelIndexer;
import org.apache.lucene.index.IndexReader;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageSimilarityIndicesBuilderAndSearch {
    //Building indices for Images with Mpeg-7 (& Mpeg-7 like) Powered Localized descriptors: SIMPLE is a collection od SCD, CLD, EHD, CEDD using parallel run
    public static void getSimpleFeatureVectorWithParallelIndexer(int numThreads, String indexDirectory, String imageDirectory) throws FileNotFoundException, IOException {
        ParallelIndexer parallelIndexer = new ParallelIndexer(numThreads, indexDirectory, imageDirectory);
        parallelIndexer.addBuilders(new ChainedDocumentBuilder());
        parallelIndexer.run();
    }

    //Searching similar images (bufferedImage) based on descriptor in the available indices
    public static ImageSearchHits search(BufferedImage bufferedImage, int maxHits, Class<?> descriptorClass, boolean isCached, IndexReader indexReader) throws FileNotFoundException, IOException {
        GenericFastImageSearcher genericFastImageSearcher = new GenericFastImageSearcher(maxHits, descriptorClass, isCached, indexReader);
        return genericFastImageSearcher.search(bufferedImage, indexReader);
    }
}
