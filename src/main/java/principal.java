
import Rank.BM25;
import buscador.Coletor;
import buscador.indexador.Indexador;
import ferramentas.Uteis;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static buscador.Pesquisa.Combina;
import static buscador.Pesquisa.pesquisar;
import static ferramentas.Uteis.ImprimeEncontrados;

public class principal {

    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        int limite=150;
        String url1="https://en.wikipedia.org/wiki/Main_Page";
        String url2="https://www.uol.com.br";
        String url3="http://www.globo.com/";
        Coletor C= new Coletor(100);
//        C.pegaLinkPag(url3);
//        C.setCount(0);
//        C.pegaLinkPag(url2);
//            C.setCount(0);
//            C.pegaLinkPag(url1);
//        try {
//            Uteis.parse();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Indexador I = new Indexador();
        I.lerArquivosColetados();
        while (true) {
            pesquisar(I, 5);
        }
    }



}