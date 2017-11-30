package buscador;

import Rank.BM25;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class Pesquisa {
    public static void pesquisar(String pesquisa, Indexador I){
        HashMap<String, Double> rank=new HashMap<>();
        rank= BM25.score(pesquisa.split(" "),I);
        int NumeroDocs =3;
        HashMap<String, Double> bestDocs=BM25.getTopN(NumeroDocs,rank);
        String []s= new String[NumeroDocs];
        SortedSet <Double>sorted = new TreeSet<>(bestDocs.values());
        System.out.println("teste");


    }
}
