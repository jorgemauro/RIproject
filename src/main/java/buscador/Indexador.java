package buscador;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import static ferramentas.Uteis.printaTempo;

public class Indexador {

    private HashMap<String, HashMap<String,Integer>> indexador;
    private HashSet<String> alfabeto;
    private HashMap<String,Integer> frequencia;
    public Indexador() {
        this.alfabeto= new HashSet<>();
        this.frequencia=new HashMap<>();
        this.indexador=new HashMap<>();
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
                try {
                    String nomeCompleto="sites/"+subPasta+"/"+arquivos[i].getName();
                    fileInputStream = new FileInputStream(nomeCompleto);
                    GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                    reader=new InputStreamReader(gzipInputStream,"UTF-8");
                    writer=new StringWriter();
                    char[] buffer = new char[10240];
                    for (int length = 0; (length = reader.read(buffer)) > 0;) {
                        writer.write(buffer, 0, length);
                    }
                    long tempoInicio1 = System.currentTimeMillis();
                    this.fillAlfabeto(writer.toString());
                    printaTempo(tempoInicio1,"Alfabeto");
                    long tempoInicio2 = System.currentTimeMillis();
                    this.fillIndex(subPasta+"/"+arquivos[i].getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        writer.close();
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
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
}
