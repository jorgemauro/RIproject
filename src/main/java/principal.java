
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.util.*;
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.*;

import static ferramentas.uteis.getNomeArquivo;
import static ferramentas.uteis.getUrlValid;
import static ferramentas.uteis.robotSafe;

public class principal {

    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        int limite=150;
        String url1="https://pt.wikipedia.org/";
        String url2="https://www.alura.com.br/";
        String url3="http://www.globo.com/";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("digite a url");
        /*try {
            url=br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }



}