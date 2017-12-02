package buscador;

import Rank.BM25;
import buscador.indexador.Indexador;

import java.util.*;

public class Pesquisa {
    public static String[] Combina(String s){
        String[] t = s.toLowerCase().split("[\\p{Punct}\\s]+");
        ArrayList<String> filtered = new ArrayList<>();

        for (String string : t) {
            if (string.length() > 2) filtered.add(string);
        }

        String[] arr = new String[filtered.size()];
        arr = filtered.toArray(arr);
        return arr;
    }
    public static void pesquisar(String pesquisa, Indexador I, int resultado){
        LinkedHashMap<String, Double> rank=new LinkedHashMap<>();
        rank= BM25.score(pesquisa.split(" "),I);
        int NumeroDocs =3;
        LinkedHashMap<String, Double> bestDocs=BM25.getTopN(NumeroDocs,rank);
        String []s= new String[NumeroDocs];
        SortedSet <Double>sorted = new TreeSet<>(bestDocs.values());


    }
}
