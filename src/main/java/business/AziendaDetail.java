package business;

public class AziendaDetail {

    String nome, url;;

    public AziendaDetail(String nome, String url){
        this.nome=nome;
        this.url=url;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String uri) {
        this.url = uri;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String ragioneSociale) {
        this.nome = ragioneSociale;
    }
}
