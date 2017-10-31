package buscador;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

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

    public void pegalinkpag(String URL) {
        Document document;
        String data;
        Elements linksOnPage;
        String[] urlPrapast=URL.split("/");
        String dest ="sites/";
        if(urlPrapast.length>2) {
            dest += urlPrapast[2];
            Boolean urlValid= getUrlValid(URL);
            String nomeArq = getNomeArquivo(urlPrapast, "");
            Boolean fileExists = new File(dest.toString() + "/" + nomeArq).exists();
            if (urlValid && !links.contains(URL) && count < limite) {
                try {
                    if (!fileExists && robotSafe(new URL(URL))) {
                        //links.add(URL);
                        if(this.count%10==0){
                            System.out.println(this.count);
                        }
                        document = Jsoup.connect(URL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
                        linksOnPage = document.select("a[href]");
                        if(!fileExists){
                            this.count++;
                            data = Jsoup.parse(document.html()).text();
                            data = data.replaceAll("[^a-zA-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ]", " ");
                            data = data.toLowerCase();
                            this.json.put(URL, data);
                            GravaArquivo(data, dest, nomeArq);
                        }
                        for (Element page : linksOnPage) {
                            pegalinkpag(page.attr("abs:href"));
                        }
                    }

                } catch (IOException e) {
                    System.err.println("For '" + URL + "': " + e.getMessage());
                }
            }
        }

    }

}
