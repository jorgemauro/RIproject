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
import java.util.zip.GZIPOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static ferramentas.Uteis.*;

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

//Preenche o index para pesquisa

    private void fillIndex(String url) {
        System.out.println(this.indexador.size());
        this.frequencia.forEach((s,Integer) ->{
            if(s.equals("BBC")){
                System.out.println("iawshdfvb");
            }
            HashMap<String, HashSet<Integer>> ocorrencia = new HashMap<>();
            ocorrencia=this.frequencia.get(s);
            if (!this.indexador.containsKey(s)) {
                this.indexador.put(s, new HashMap<>());
            }
            this.indexador.get(s).putAll(ocorrencia);
//            if(memoriaInf()<80){
//                Gson gson = new Gson();
//                this.indexador.forEach((k,obj) ->{
//                    JSONObject json = new JSONObject();
//                    json.put(k,obj);
//                    escreveFimArquivo("indexador.json",json.toString());
//                });
//                this.indexador=new HashMap<>();
//            }
        }
        );
    }

    private void fillAlfabeto(String data, String site) {
        data=data.replaceAll("<.*?>", " ");
        data=data.replaceAll("[^a-zA-záàâãéèêíïóôõöúçÁÀÂÃÉÈÍÏÓÔÕÖÚÇ]", " ");
        String[] f=data.split(" ");
        this.docSize.put(site,f.length);
        for(int i=0;i<f.length;i++) {
            if (!this.alfabeto.contains(f[i])&&f[i].length() > 1) {
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

    // le os arquivos coletados
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
                    if(this.count==50000)
                        break;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        this.GravaIndexEAlfabeto();
    }


    // grava o index e o Alfabeto compactados
    private void GravaIndexEAlfabeto() {
        JSONObject jsonAlfabeto = new JSONObject();
        jsonAlfabeto.put("alfabeto",this.alfabeto);


        JSONObject jsonIndexinvert = new JSONObject();
        this.indexador.forEach((k,obj) ->{
            jsonIndexinvert.put(k,obj);
        });
        JSONObject jsonDocSize = new JSONObject();
        this.docSize.forEach((k,obj) ->{
            jsonDocSize.put(k,obj);
        });
        try {

            this.zipConteudo(jsonDocSize.toString(),"DocSize.zip");
            this.zipConteudo(jsonAlfabeto.toString(),"Alfabeto.zip");
            this.zipConteudo(jsonIndexinvert.toString(),"IndexInvertido.zip");
            this.indexador=new HashMap<>();
            this.alfabeto=new HashSet<>();
            this.docSize= new HashMap<>();
            recuperaIndexInvert(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Transaforma o conteudo de uma string para ser zipado
    private void zipConteudo(String s, String nome)throws IOException {

        if (s == null || s.length() == 0) {
            System.out.println("string vazia");
        }else {
            GravaArquivoZip(s,nome);
        }
    }
// verifica o quando tew de memoria esta sendo utilizada
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
    // salva index em um json
    public void saveIndex(){
        JSONObject j=new JSONObject();
        this.indexador.forEach((s,h)->{
            j.put(s,h);
        });
        Uteis.escreveFimArquivo("Indexador.json",j.toString());
    }
}
