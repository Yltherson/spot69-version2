package com.spot69.model;

import java.util.Date;

public class MouvementStock {
    private Date date;
    private int stockDebut;
    private int qteIn;
    private int qteOut;
    private int stockFin;
    private FactureDetail factureDetail;
    private String typeMouvement; // <-- ajouté
    private String justification; 

    // Getters et setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStockDebut() {
        return stockDebut;
    }

    public void setStockDebut(int stockDebut) {
        this.stockDebut = stockDebut;
    }

    public int getQteIn() {
        return qteIn;
    }

    public void setQteIn(int qteIn) {
        this.qteIn = qteIn;
    }

    public int getQteOut() {
        return qteOut;
    }
    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public void setQteOut(int qteOut) {
        this.qteOut = qteOut;
    }

    public int getStockFin() {
        return stockFin;
    }

    public void setStockFin(int stockFin) {
        this.stockFin = stockFin;
    }

    public FactureDetail getFactureDetail() {
        return factureDetail;
    }

    public void setFactureDetail(FactureDetail factureDetail) {
        this.factureDetail = factureDetail;
    }
    
    // Getters et setters
    public String getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(String typeMouvement) {
        this.typeMouvement = typeMouvement;
    }
}