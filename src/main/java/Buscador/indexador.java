package Buscador;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class indexador {

    private HashMap<String, HashMap<String,Integer>> indexador;
    private HashSet<String> alfabeto;
    private HashMap<String,Integer> frequencia;
    public indexador(int limite) {
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
    public static void lerArquivosColetados() {
        File folder = new File("sites");
        File[] it = folder.listFiles();
        for (File arquivo : folder.listFiles()) {
            String subPasta=arquivo.getName();
            InputStream fileInputStream = null;
            File[] arquivos = arquivo.listFiles();
            for(int i=0;i<arquivos.length;i++) {
                try {
                    fileInputStream = new FileInputStream("sites/"+subPasta+"/"+arquivos[i].getName());
                    GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
                    int data1 = gzipInputStream.read();
                    String data=data1+"";

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
