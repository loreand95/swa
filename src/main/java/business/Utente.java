/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

/**
 *
 * @author lorenzo
 */
public class Utente {

    protected long id;
    protected String username, password, email,tipo,token;
    

    public Utente(){}

    public Utente(long id) {
        this.id=id;
    }

    public Utente(String username, String password) {
        this.username=username;
        this.password=password;
    }
    
    public Utente(String username, String password, String email, String tipo) {
        this.username=username;
        this.password=password;
        this.email=email;
        this.tipo=tipo;
    }

    public Utente(long id, String username, String email, String tipo ) {
        this.id=id;
        this.username=username;
        this.email=email;
        this.tipo=tipo;
    }

    public Utente(long id, String username, String email, String tipo, String token ) {
        this.id=id;
        this.username=username;
        this.email=email;
        this.tipo=tipo;
        this.token=token;
    }

    public void setId(Long id){
        this.id=id;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getId() {
        
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
