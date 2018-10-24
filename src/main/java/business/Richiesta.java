/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import java.util.Date;

/**
 *
 * @author lorenzo
 */
public class Richiesta {

    private Annuncio annuncio;
    private Studente studente;
    private Docente docente;
    private int crediti;

    private Date dataInizio,dataFine;
    
    public Richiesta(){}

    public Richiesta(Annuncio annuncio, Studente studente) {
        this.annuncio = annuncio;
        this.studente = studente;
    }
    
    public Richiesta(Annuncio annuncio, Studente studente, Docente docente, int crediti, Date dataInizio, Date dataFine) {
        this.annuncio = annuncio;
        this.studente = studente;
        this.docente = docente;
        this.crediti = crediti;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    public Annuncio getAnnuncio() {
        return annuncio;
    }

    public void setAnnuncio(Annuncio annuncio) {
        this.annuncio = annuncio;
    }

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }

    public Docente getDocente() {
        return docente;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public int getCrediti() {
        return crediti;
    }

    public void setCrediti(int crediti) {
        this.crediti = crediti;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }
}
