
import jdk.nashorn.internal.parser.JSONParser;
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

public class recuperador {
    public static final boolean DEBUG = false;
    public static final String DISALLOW = "Disallow:";
    private HashMap<String, HashMap<String,Integer>> indexador;
    private HashSet<String> links;
    public JSONObject json;
    public HashSet<String> robots;
    private int count=0;
    private int limite;
    private HashSet<String> alfabeto;
    private HashMap<String,Integer> frequencia;
    public recuperador(int limite) {
        this.links = new HashSet<>();
        this.json= new JSONObject();
        this.limite=limite;
        this.alfabeto= new HashSet<>();
        this.frequencia=new HashMap<>();
        this.indexador=new HashMap<>();
    }
    public boolean robotSafe(URL url) {
        String strHost = url.getHost();

        String strRobot = "http://" + strHost + "/robots.txt";
        URL urlRobot;
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            return false;
        }

        if (DEBUG) System.out.println("Checking robot protocol " +
                urlRobot.toString());
        String strCommands="";
        try {
            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
            if(numRead != -1)
                strCommands = new String(b, 0, numRead);
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1) {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
            urlRobotStream.close();
        } catch (IOException e) {
            return true;
        }
        if (DEBUG) System.out.println(strCommands);

        String strURL = url.getFile();
        int index = 0;
        while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
            index += DISALLOW.length();
            String strPath = strCommands.substring(index);
            StringTokenizer st = new StringTokenizer(strPath);

            if (!st.hasMoreTokens())
                break;

            String strBadPath = st.nextToken();

            // if the URL starts with a disallowed path, it is not safe
            if (strURL.indexOf(strBadPath) == 0)
                return false;
        }

        return true;
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
            if (!fileExists && !links.contains(URL) && count < limite) {
                try {
                    if (this.robotSafe(new URL(URL))) {

                        links.add(URL);
                        System.out.println(this.count);
                        this.count++;
                        document = Jsoup.connect(URL).get();

                        data = Jsoup.parse(document.html()).text();
                        linksOnPage = document.select("a[href]");
                        data = data.replaceAll("[^a-zA-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ]", " ");
                        data = data.toLowerCase();
                        this.json.put(URL, data);
                        Boolean d = new File(dest.toString()).mkdirs();
                        FileOutputStream outputStream = new FileOutputStream(dest + "/" + nomeArq);
                        GZIPOutputStream g = new GZIPOutputStream(outputStream);
                        g.write(data.getBytes());
                        for (Element page : linksOnPage) {
                            pegalinkpag(page.attr("abs:href"));
                        }
                        links.remove(URL);
                    }

                } catch (IOException e) {
                    System.err.println("For '" + URL + "': " + e.getMessage());
                }
            }
        }

    }

    private Boolean getUrlValid(String url) {
        HashSet<String> ex=new HashSet<>();
        ex.add("zip");
        ex.add("jar");
        ex.add("gz");
        ex.add("pdf");
        ex.add("doc");
        ex.add("rar");
        ex.add("txt");
        url=url.substring(url.length()-3,url.length());
        url=url.replaceAll("[^a-zA-Z]","");
        for(int i=0;i<ex.size();i++){
            return !url.equals(ex);
        }
        return true;
    }

    private String getNomeArquivo(String[] urlPrapast, String nomeArq) {
        boolean nofound=true;
        if(urlPrapast.length>3){
            if(!urlPrapast[3].equals("")){
                for(int i=3;i<urlPrapast.length;i++){
                    nofound=!urlPrapast[i].equals("?");
                    if (nofound&&!urlPrapast.equals("")&&(nomeArq.length()+urlPrapast[i].length())<100)
                        nomeArq+=urlPrapast[i].replaceAll("[^a-zA-Z0-9]"," ");
                }
                nomeArq+=".zip";
            }else{
                nomeArq=urlPrapast[2];
                String[] serpador=nomeArq.split("[.]");
                nomeArq= serpador[1];
                nomeArq+=".zip";
            }
        }else{
            nomeArq=urlPrapast[2];
            String[] serpador=nomeArq.split("[.]");
            nomeArq= serpador[1];
            nomeArq+=".zip";
        }
        return nomeArq;
    }

    private void fillIndex(String url) {
        this.frequencia.forEach((s, integer) ->{
            if (!this.indexador.containsKey(s)) {
                this.indexador.put(s, new HashMap<>());
            }
            this.indexador.get(s).put(url,integer);
        } );
    }

    private void fillAlfabeto(String data) {
        String[] f=data.split(" ");
        for(int i=0;i<f.length;i++)
            if(!this.alfabeto.contains(f[i])&&f[i].length()>1){
                this.alfabeto.add(f[i]);
                this.frequencia.put(f[i],1);
            }else if(f[i].length()>1){
                this.frequencia.put(f[i],this.frequencia.get(f[i])+1);
            }
    }

    public static void main(String[] args) {
        long tempoInicio = System.currentTimeMillis();
        int limite=2;
        String url="https://www.alura.com.br/";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Digite o limite de paginas");
        try {
            limite=Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("digite a url");
        /*try {
            url=br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        recuperador R = new recuperador(limite);
        R.pegalinkpag(url);
        FileWriter writeFile = null;
        try{
            writeFile = new FileWriter("saida.json");
            writeFile.write(R.json.toString());
            writeFile.close();
            System.out.println("Tempo Total: "+((System.currentTimeMillis()-tempoInicio)/1000)+" segundos");
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

}