package Rank;

import buscador.Indexador;

import java.util.*;

public class BM25 {

    public static double K = 1d;
    public static double B = 0.5d;

    // The total number of documents
    public static int N = 500;
    public static int AVGDL = 100;

    public static HashMap<String, Double> score(String[] query, Indexador I) {

        Indexador indexs 				= getIndexs(query,I);					// inverted index that only contain the tokens which are in query
        List<String> docs 				= I.getDocs();					// all documents that contain indexes
        HashMap<String, Double> scores 	= getScoreMap(docs);		// scores for each document, <Url, Score>

        N = I.getDocs().size();
        int sum = 0;
        for ( String doc: I.getDocs()) {
            sum += I.getDocSize().get(doc);
        }
        AVGDL =  sum / N;

        double score=0;
        int fi;
        int docLength;
        int nQueryI;
        String url="";

        for (int i = 0; i < docs.size(); i++) {

            url = docs.get(i);
            score = scores.get(url);
            docLength = I.getDocSize().get(url);

                fi = findFi(indexs, url);
                nQueryI = I.getDocs().size();

                score += bm25_aux(fi, docLength, AVGDL) * IDF(N, nQueryI);
            if(score>0) {
                scores.put(url, score);
            }
        }

        return scores;
    }
    public static int findFi(Indexador index, String url) {

        final int[] FI = {0};
            if (index.getDocs().contains(url)) {
                index.getIndexador().forEach((s,h)->{
                    if(h.containsKey(url)) {
                        if (h.get(url).size() > 0) {
                            FI[0] += h.get(url).size();
                        }
                    }});
            }

        return FI[0];
    }

    public static double bm25_aux(int fi, int docLength, int avgdl) {

        double tmp1 = (fi * (K + 1.0));
        double tmp2 = (fi + K * (1 - B + B * (docLength * 1.0 / avgdl)));
        return tmp1 / tmp2;

    }

    public static double IDF(int N, int nQueryI) {
//		if (1.0*N/nQueryI < 0.7) N *= 5;
        double result = (N - nQueryI + 0.5) / (nQueryI + 0.5);

        result = Math.log(result)/Math.log(2);
        if (result <= 1) {
            return 1.0d;
        } else {
            return result;
        }
    }

    // extract urls from indexDocs and put into map with 0 score
    public static HashMap<String, Double> getScoreMap(List<String> docs) {

        HashMap<String, Double> scores = new HashMap<String, Double>();

        for (String doc : docs) {
            scores.put(doc, 0.0);
        }

        return scores;
    }

    public static Indexador getIndexs(String[] query, Indexador I) {

        Indexador indexs = new Indexador();
        HashMap<String, HashMap<String, HashSet<Integer>>> index= new HashMap<>();

        for (String token : query) {
            if (I.getIndexador().containsKey(token)&&I.getIndexador().get(token).size() != 0) {
                index.put(token, I.getIndexador().get(token));
                I.getIndexador().get(token).forEach((s,h)->{
                    indexs.getDocs().add(s);
                    indexs.getDocSize().put(s,I.getDocSize().get(s));
                });

            }
        }
        indexs.setIndexador(index);
        return indexs;
    }



    public static HashMap<String, Double> getTopN(int n, HashMap<String, Double> scores) {
        HashMap<String, Double> scoresTopN = new HashMap<>();

        Double max;
        String max_idx;

        for (int i = 0; i < n; i++) {

            max = Double.MIN_VALUE;
            max_idx = null;

            for (Map.Entry<String, Double> entry: scores.entrySet()) {

                if (entry.getValue() > max) {

                    max = entry.getValue();
                    max_idx = entry.getKey();

                }

            }

            if (max_idx != null) {
                scoresTopN.put(max_idx, scores.remove(max_idx));
            }

        }

        return scoresTopN;
    }

    public static ArrayList<String> getTopNList(int n, HashMap<String, Double> scores) {

        ArrayList<String> topN = new ArrayList<>();

        HashMap<String, Double> scoresCopy = new HashMap<>(scores);

        Double max;
        String max_idx;

        for (int i = 0; i < n; i++) {

            max = Double.MIN_VALUE;
            max_idx = null;

            for (Map.Entry<String, Double> entry: scoresCopy.entrySet()) {

                if (entry.getValue() > max) {

                    max = entry.getValue();
                    max_idx = entry.getKey();

                }

            }

            if (max_idx != null) {
                topN.add(max_idx);
                scoresCopy.remove(max_idx);
            }

        }

        return topN;
    }
}

