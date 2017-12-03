package ferramentas;

import buscador.indexador.Indexador;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.springframework.web.util.HtmlUtils;

public class Uteis {
    private static final boolean DEBUG = false;
    private static final String DISALLOW = "Disallow:";
// verifica o robots safe da url em questão
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
    // retira caracteres indejados do nome para o salvamento de arquivo
    public static String ArrumaNome(String nome){
        nome=nome.replaceAll("[=]", "_Equals_");
        nome=nome.replaceAll("[#]", "_Ash_");
        nome=nome.replaceAll("[%]","_PorCent_");
        nome=nome.replaceAll("[&]","_Ecomerc_");
        nome=nome.replaceAll("[*]","_Aster_");
        nome=nome.replaceAll("[|]","_BarraPe_");
        nome=nome.replaceAll("[:]","_DoisPont_");
        nome=nome.replaceAll("[\"]","_AspasDup_");
        nome=nome.replaceAll("[<]","_MenorQ_");
        nome=nome.replaceAll("[>]","_MaiorQ_");
        nome=nome.replaceAll("[?]","_Inter_");
        nome=nome.replaceAll("[/]","_BarraDir_");
        return nome;
    }
    //recupera o nome das paginas para serem acessadas
    public static String voltaNome(String nome){
        nome=nome.replaceAll("_Ash_","#");
        nome=nome.replaceAll("_PorCent_","%");
        nome=nome.replaceAll("_Ecomerc_","&");
        nome=nome.replaceAll("_Aster_","*");
        nome=nome.replaceAll("_BarraPe_","|");
        nome=nome.replaceAll("_BarraEsq_","\\");
        nome=nome.replaceAll("_DoisPont_",":");
        nome=nome.replaceAll("_AspasDup_","\"");
        nome=nome.replaceAll("_MenorQ_","<");
        nome=nome.replaceAll("_MaiorQ_",">");
        nome=nome.replaceAll("_Inter_","?");
        nome=nome.replaceAll("/","_BarraDir_");
        nome=nome.replaceAll(".html","");
        return nome;
    }
    // verifica se a Url é valida para busca
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
    // gera nome do arquivo que será armazenado
    public static String getNomeArquivo(String[] urlPrapast, String nomeArq) {
        boolean nofound;
        nofound = true;
        if(urlPrapast.length>3){
            if(!urlPrapast[3].equals("")){
                for(int i=3;i<urlPrapast.length;i++){
                    nofound=!urlPrapast[i].equals("?");
                    if (nofound&&!urlPrapast.equals("")&&(nomeArq.length()+urlPrapast[i].length())<100)
                        nomeArq+=ArrumaNome(urlPrapast[i]);
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
    //Escreve no fim de um arquivo
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
    //grava um arquivo zipado
    public static void GravaArquivoZip(String data, String nomeArq) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(nomeArq);
        GZIPOutputStream g = new GZIPOutputStream(outputStream);
        g.write(data.getBytes());
        g.close();
    }
    // grava rquivos no formato Html
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
    // imprime o tempo gasto
    public static void printaTempo(long inicio , String nomeTest){
        System.out.println(nomeTest+"-> Tempo Total: "+(System.currentTimeMillis()-inicio));
    }
    // faz o parse dos arquivos WTX
    public static void parse() throws IOException {
        int count=0;
        String Path = "sitesextract";
        String outputPath = "sites/coletados";
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
                Document doc = Jsoup.parse(IOUtils.toString(inputStream), "", Parser.xmlParser());
                for (Element element : doc.select("doc")) {
                    Element html = element.select("html").first();
                    if (html != null) {
                        html.select("img").remove();
                        html.select("center").remove();
                        html.select("a").remove();
                        String text = HtmlUtils.htmlUnescape(html.toString());
                        String[] split = element.select("docno").text().split("-");
                        File file2 = new File(outputPath + File.separator + split[0] + split[1]);
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
//recupera o indexInvertido
    public static Indexador recuperaIndexInvert(Indexador ind) {
        HashMap<String, HashMap<String, HashSet<Integer>>> recIndex = new HashMap<>();
        HashMap<String,Integer> docSize=new HashMap<>();
        HashSet<String> alfabeto=new HashSet<>();
        InputStream fileInputStream = null;
        Reader reader = null;
        StringWriter writer = null;
        String nomeCompleto = "IndexInvertido.zip";
        RecuperaDocSize(ind.getDocSize(), ind.getDocs(),"DocSize.zip");
        RecuperaAlfabeto(ind.getAlfabeto(),"Alfabeto.zip");
        RecuperaIndex(ind.getIndexador(), "IndexInvertido.zip");
        return ind;
    }

    private static void RecuperaDocSize(HashMap<String, Integer> docSize,List<String> docs , String s) {
        InputStream fileInputStream;
        Reader reader;
        StringWriter writer;File file = new File(s);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                reader = new InputStreamReader(gzipInputStream, "UTF-8");
                writer = new StringWriter();
                char[] buffer = new char[10240];
                for (int length = 0; (length = reader.read(buffer)) > 0; ) {
                    writer.write(buffer, 0, length);
                }
                JSONObject jsonObject = new JSONObject(writer.toString());
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (!docs.contains(key))
                        docs.add(key);
                    docSize.put(key, (Integer) jsonObject.get(key));

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void RecuperaAlfabeto(HashSet<String> alfabeto, String nomeCompleto) {

        InputStream fileInputStream;
        Reader reader;
        StringWriter writer;File file = new File(nomeCompleto);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                reader = new InputStreamReader(gzipInputStream, "UTF-8");
                writer = new StringWriter();
                char[] buffer = new char[10240];
                for (int length = 0; (length = reader.read(buffer)) > 0; ) {
                    writer.write(buffer, 0, length);
                }
                JSONObject jsonObject = new JSONObject(writer.toString());
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONArray arr= (JSONArray) jsonObject.get(key);
                    arr.forEach(j->{
                        alfabeto.add((String) j);
                    });
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void RecuperaIndex(HashMap<String, HashMap<String, HashSet<Integer>>> recIndex, String nomeCompleto) {
        InputStream fileInputStream;
        Reader reader;
        StringWriter writer;File file = new File(nomeCompleto);
        if (file.exists()) {
            try {
                fileInputStream = new FileInputStream(file);
                GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                reader = new InputStreamReader(gzipInputStream, "UTF-8");
                writer = new StringWriter();
                char[] buffer = new char[10240];
                for (int length = 0; (length = reader.read(buffer)) > 0; ) {
                    writer.write(buffer, 0, length);
                }
                JSONObject jsonObject = new JSONObject(writer.toString());
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (jsonObject.get(key) instanceof JSONObject) {
                        JSONObject occurence = (JSONObject) jsonObject.get(key);
                        Iterator<?> keys2 = occurence.keys();
                        HashMap<String, HashSet<Integer>> oc = new HashMap<>();
                        while (keys2.hasNext()) {
                            String key2 = (String) keys2.next();
                            JSONObject frequencia = (JSONObject) jsonObject.get(key);
                            Iterator<?> keys3 = frequencia.keys();
                            while (keys3.hasNext()) {
                                String key3 = (String) keys3.next();
                                JSONArray arr = (JSONArray) frequencia.get(key3);
                                oc.put(key3, new HashSet<>());
                                arr.forEach(k -> {
                                    oc.get(key3).add((Integer) k);
                                });

                            }
                        }
                        recIndex.put(key, oc);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    }
    public static void ImprimeEncontrados(LinkedHashMap<String, Double> encontrados) {
        File folder = new File("sites/coletados");
        File[] it = folder.listFiles();
        for (File arquivo : folder.listFiles()) {
            encontrados.forEach((s,j)->{
                Boolean fileExists = new File("sites/coletados/"+arquivo.getName()+"/"+s).exists();
                if(fileExists){
                    String nomeFinal=voltaNome(s);
                    String[] compara = arquivo.getName().split("[.]");
                    if(compara.length>1&&compara[1].equals(nomeFinal)){
                        nomeFinal="https://"+arquivo.getName();
                    }else{
                        nomeFinal="http://"+arquivo.getName()+"/"+nomeFinal;
                    }
                    System.out.println(nomeFinal);
                }
            });
        }



    }
}