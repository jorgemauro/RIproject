
import Rank.BM25;
import buscador.Coletor;
import buscador.indexador.Indexador;

import java.util.HashMap;

public class principal {

    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        int limite=150;
        String url1="https://en.wikipedia.org/wiki/Main_Page";
        String url2="https://www.uol.com.br";
        String url3="http://www.globo.com/";
//        Coletor C= new Coletor(100);
//        C.pegalinkpag(url3);
//        C.setCount(0);
//        C.pegalinkpag(url2);
//        try {
//            Uteis.parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Indexador I = new Indexador();
        HashMap<String, Double> rank=new HashMap<>();
        I.lerArquivosColetados();
        String pesquisa = "tem";
        rank=BM25.score(pesquisa.split(" "),I);

        HashMap<String, Double> encontrados =BM25.getTopN(5,rank);

        System.out.println("só pra debugar");


    }



}