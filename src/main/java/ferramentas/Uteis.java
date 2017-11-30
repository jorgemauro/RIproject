package ferramentas;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.springframework.web.util.HtmlUtils;

public class Uteis {
    private static final boolean DEBUG = false;
    private static final String DISALLOW = "Disallow:";

    public static boolean robotSafe(URL url) {
        String strHost = url.getHost();

        String strRobot = "http://" + strHost + "/robots.txt";
        URL urlRobot;
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            return false;
        }

        if (DEBUG) System.out.println("Checking robot protocol " +
                urlRobot.toString());
        StringBuilder strCommands= new StringBuilder();
        try {
            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
            if(numRead != -1)
                strCommands = new StringBuilder(new String(b, 0, numRead));
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1) {
                    String newCommands = new String(b, 0, numRead);
                    strCommands.append(newCommands);
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
    public static Boolean getUrlValid(String url) {
        HashSet<String> ex=new HashSet<>();
        ex.add("zip");
        ex.add("jar");
        ex.add("gz");
        ex.add("pdf");
        ex.add("doc");
        ex.add("rar");
        ex.add("txt");
        ex.add("ogg");
        ex.add("svg");
        ex.add("jpg");
        ex.add("png");
        ex.add("mp4");
        ex.add("wma");
        ex.add("mp3");
        url=url.substring(url.length()-3,url.length());
        url=url.replaceAll("[^a-zA-Z]","").toLowerCase();
        for(int i=0;i<ex.size();i++){
            if(ex.contains(url)){
                return false;
            }
        }
        return true;
    }
    public static String getNomeArquivo(String[] urlPrapast, String nomeArq) {
        boolean nofound;
        nofound = true;
        if(urlPrapast.length>3){
            if(!urlPrapast[3].equals("")){
                for(int i=3;i<urlPrapast.length;i++){
                    nofound=!urlPrapast[i].equals("?");
                    if (nofound&&!urlPrapast.equals("")&&(nomeArq.length()+urlPrapast[i].length())<100)
                        nomeArq+=urlPrapast[i].replaceAll("[^a-zA-Z0-9]"," ");
                }
            }else{
                nomeArq=urlPrapast[2];
                String[] serpador=nomeArq.split("[.]");
                nomeArq= serpador[1];
            }
        }else{
            nomeArq=urlPrapast[2];
            String[] serpador=nomeArq.split("[.]");
            nomeArq= serpador[1];
        }
        return nomeArq;
    }
    public static void escreveFimArquivo(String arq, String escrita){

        FileWriter writeFile = null;
        String newLine = System.getProperty("line.separator");
        try {
            writeFile = new FileWriter(arq,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeFile.append(escrita+newLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void GravaArquivo(String data, String dest, String nomeArq) throws IOException {
        Boolean d = new File(dest.toString()).mkdirs();

        FileOutputStream outputStream = new FileOutputStream(dest + "/" + nomeArq+".html");

//        GZIPOutputStream g = new GZIPOutputStream(outputStream);
//        g.write(data.getBytes());
//        g.close();
        outputStream.write(data.getBytes());
        outputStream.flush();
        outputStream.close();
    }
    public static void printaTempo(long inicio , String nomeTest){
        //System.out.println(nomeTest+"-> Tempo Total: "+(System.currentTimeMillis()-inicio));
        System.out.println(inicio);
    }
    public static void parse() throws IOException {
        int count=0;
        String Path = "sitesextract";
        String outputPath = "sites";
        File folder = new File(Path);
        File[] it = folder.listFiles();
        for (File arquivo : folder.listFiles()) {
            String subPasta=arquivo.getName();
            InputStream fileInputStream = null;
            File[] arquivos = arquivo.listFiles();
            for(File arq : arquivo.listFiles()) {
                String nomeCompleto=Path+"/"+subPasta+"/"+arq.getName();
                GZIPInputStream inputStream = new GZIPInputStream(
                        new FileInputStream(nomeCompleto));
                count++;
                if(count ==21) {
                    System.out.println(count);

                    Scanner in = new Scanner(inputStream);
                    String x = "";
                    while (in.hasNext()) {
                        x += in.nextLine();
                    }
                    System.out.println("teste");
                }
                Document doc = Jsoup.parse(IOUtils.toString(inputStream), "", Parser.xmlParser());
                for (Element element : doc.select("doc")) {
                    Element html = element.select("html").first();
                    if (html != null) {
                        html.select("img").remove();
                        html.select("center").remove();
                        html.select("a").remove();
                        String text = HtmlUtils.htmlUnescape(html.toString());
                        String[] split = element.select("docno").text().split("-");
                        File file2 = new File(outputPath + File.separator + split[0] + File.separator + split[1]);
                        file2.mkdirs();
                        FileOutputStream fileOutputStream = new FileOutputStream(
                                file2.getPath() + File.separator + split[2] + ".html");
                        fileOutputStream.write(text.getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }

                }
            }
        }
    }
}