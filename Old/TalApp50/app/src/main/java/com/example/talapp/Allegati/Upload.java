package com.example.talapp.Allegati;

public class Upload {
    private String nome;
    private String url;

    public Upload(){

    }

    public Upload(String nome, String url){
        if(nome.trim().equals("")){
            nome = "Senza nome";
        }
        this.nome = nome;
        this.url = url;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
