package buscador;

import Rank.BM25;
import buscador.indexador.Indexador;

import java.util.*;

import static ferramentas.Uteis.ImprimeEncontrados;

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
    public static void pesquisar(Indexador I, int resultado){
        LinkedHashMap<String, Double> rank=new LinkedHashMap<>();
        System.out.println("O que você deseja ´pesquisar");
        Scanner s=new Scanner(System.in);
        String pesquisa = s.nextLine();
        rank=BM25.score(Combina(pesquisa),I);

        LinkedHashMap<String, Double> encontrados =BM25.getTopN(resultado,rank);

        ImprimeEncontrados(encontrados);


    }
}
