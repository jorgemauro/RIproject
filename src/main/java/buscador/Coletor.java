package buscador;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

import static ferramentas.Uteis.*;

public class Coletor {
    private HashSet<String> links;
    public JSONObject json;
    public HashSet<String> robots;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count=0;
    private int limite;

    public Coletor(int limite) {
        this.links = new HashSet<>();
        this.json= new JSONObject();
        this.limite=limite;
    }
// Pega os link das paginas e gera os arquivos nas pastas
    public synchronized void pegaLinkPag(String URL) {
        Document document;
        Document data;
        Elements linksOnPage;
        String[] urlPrapast=URL.split("/");
        String dest ="sites/";
        if(urlPrapast.length>2) {
            dest += "coletados/"+urlPrapast[2];
            Boolean urlValid= getUrlValid(URL);
            String nomeArq = getNomeArquivo(urlPrapast, "");
            String caminhoArquivo=dest + "/" + nomeArq;
            Boolean fileExists = new File(caminhoArquivo+".html").exists();
            if (urlValid && !links.contains(URL) && count < limite) {
                try {
                    if (!fileExists && robotSafe(new URL(URL))) {
                        //links.add(URL);
                        extrator(URL, dest, nomeArq);
                    }

                } catch (IOException e) {
                    System.err.println("For '" + URL + "': " + e.getMessage());
                }
            }
        }

    }
// extrai os links da url
    private void extrator(String URL, String dest, String nomeArq) throws IOException {
        Document document;
        Elements linksOnPage;
        Document data;
        if(this.count%10==0){
            System.out.println(this.count);
        }
        document = Jsoup.connect(URL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
        linksOnPage = document.select("a[href]");

        data = Jsoup.parse(document.html());
        this.count++;
        System.out.println(nomeArq);
        GravaArquivo(data.toString(), dest, nomeArq);

        for (Element page : linksOnPage) {
            if(!page.attr("abs:href").equals(URL)) {
                pegaLinkPag(page.attr("abs:href"));
            }
        }
    }

}
