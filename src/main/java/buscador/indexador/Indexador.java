package buscador.indexador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ferramentas.Uteis;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static ferramentas.Uteis.GravaArquivo;
import static ferramentas.Uteis.escreveFimArquivo;
import static ferramentas.Uteis.printaTempo;

public class Indexador {

    public HashMap<String, HashMap<String, HashSet<Integer>>> getIndexador() {
        return indexador;
    }

    public void setIndexador(HashMap<String, HashMap<String, HashSet<Integer>>> indexador) {
        this.indexador = indexador;
    }

    public HashSet<String> getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(HashSet<String> alfabeto) {
        this.alfabeto = alfabeto;
    }

    public HashMap<String, HashMap> getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(HashMap<String, HashMap> frequencia) {
        this.frequencia = frequencia;
    }

    public HashMap<String, Integer> getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(HashMap<String, Integer> ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    private HashMap<String, HashMap<String,HashSet<Integer>>> indexador;

    public List<String> getDocs() {
        return docs;
    }

    public void setDocs(List<String> docs) {
        this.docs = docs;
    }

    private List<String> docs;

    public HashMap<String, Integer> getDocSize() {
        return docSize;
    }

    public void setDocSize(HashMap<String, Integer> docSize) {
        this.docSize = docSize;
    }

    private HashMap<String,Integer> docSize;
    private HashSet<String> alfabeto;
    private HashMap<String,HashMap> frequencia;
    private HashMap<String,Integer> ocorrencia;
    int count=0;
    public Indexador() {
        this.alfabeto= new HashSet<>();
        this.frequencia=new HashMap<>();
        this.indexador=new HashMap<>();
        this.docSize=new HashMap<>();
        this.docs= new ArrayList<>();
    }



    private void fillIndex(String url) {
        System.out.println(this.indexador.size());
        this.frequencia.forEach((s,Integer) ->{
            HashMap<String, HashSet<Integer>> ocorrencia = new HashMap<>();
            ocorrencia=this.frequencia.get(s);
            if (!this.indexador.containsKey(s)) {
                this.indexador.put(s, new HashMap<>());
            }
            this.indexador.get(s).putAll(ocorrencia);
            if(memoriaInf()<80){
                Gson gson = new Gson();
                this.indexador.forEach((k,obj) ->{
                    JSONObject json = new JSONObject();
                    json.put(k,obj);
                    escreveFimArquivo("indexador.json",json.toString());
                });
                this.indexador=new HashMap<>();
            }
        } );
    }

    private void fillAlfabeto(String data, String site) {
        data=data.replaceAll("<.*?>", " ");
        data=data.replaceAll("[^a-zA-záàâãéèêíïóôõöúçÁÀÂÃÉÈÍÏÓÔÕÖÚÇ]", " ");
        String[] f=data.split(" ");
        this.docSize.put(site,f.length);
        for(int i=0;i<f.length;i++) {
            if (!this.alfabeto.contains(f[i])) {
                this.alfabeto.add(f[i]);
                HashMap<String, HashSet<Integer>> ocorrencia = new HashMap<>();
                if (!ocorrencia.containsKey(f[i]))
                    ocorrencia.put(site, new HashSet<>());
                    ocorrencia.get(site).add(i);
                this.frequencia.put(f[i], ocorrencia);
            } else if (f[i].length() > 1) {
                HashMap<String, HashSet<Integer>> ocorrencia = new HashMap<>();
                ocorrencia = this.frequencia.get(f[i]);
                if (!ocorrencia.containsKey(f[i]))
                    ocorrencia.put(site, new HashSet<>());
                ocorrencia.get(site).add(i);
                this.frequencia.put(f[i], ocorrencia);
            }
        }
    }
    public  void lerArquivosColetados() {
        File folder = new File("sites");
        File[] it = folder.listFiles();
        for (File arquivo : folder.listFiles()) {
            String subPasta=arquivo.getName();
            InputStream fileInputStream = null;
            File[] arquivos = arquivo.listFiles();
            Reader reader=null;
            StringWriter writer=null;
            for(int i=0;i<arquivos.length;i++) {
                for(File arq: arquivos[i].listFiles())
                try {

                    String pasta = System.getProperty("user.home");
                    String nomeCompleto=arquivos[i]+"/"+arq.getName();
                    File file = new File(nomeCompleto);
//                    GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
//                    reader=new InputStreamReader(gzipInputStream,"UTF-8");

                    Document doc= Jsoup.parse(file,"UTF-8");;
//                    char[] buffer = new char[10240];
//                    for (int length = 0; (length = reader.read(buffer)) > 0;) {
//                        writer.write(buffer, 0, length);
//                    }
                    this.docs.add(arq.getName());
                    this.fillAlfabeto(doc.text(), arq.getName());
                    this.fillIndex(arq.getName());
                    System.out.println(this.count);
                    this.count++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(this.count==251) {
                    break;
                }
            }

            if(this.count==251) {
                break;
            }
        }
    }

    private long memoriaInf() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        long resultado=(freeMemory + (maxMemory - allocatedMemory)) / 1024;
        return resultado;
    }

    public static String unzip(final byte[] compressed) {
        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }
        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while((line = bufferedReader.readLine()) != null){
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }
    public static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }
    public void saveIndex(){
        JSONObject j=new JSONObject();
        this.indexador.forEach((s,h)->{
            j.put(s,h);
        });
        Uteis.escreveFimArquivo("Indexador.json",j.toString());
    }
}
