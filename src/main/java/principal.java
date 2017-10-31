
import buscador.Coletor;
import buscador.Indexador;

import java.io.*;

public class principal {

    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        int limite=150;
        String url1="https://en.wikipedia.org/wiki/Main_Page";
        String url2="https://www.alura.com.br/";
        String url3="http://www.globo.com/";
        Coletor C= new Coletor(100);
        Indexador I = new Indexador();
        C.pegalinkpag(url1);
        C.setCount(0);
        C.pegalinkpag(url2);
        C.setCount(0);
        C.pegalinkpag(url3);
        I.lerArquivosColetados();
        System.out.println("só pra debugar");


    }



}