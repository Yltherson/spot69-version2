package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CompteClient {
    private int id;
    private int clientId;
    private Utilisateur client; // Référence au client
    private BigDecimal solde;
    private Timestamp dateCreation;
    private BigDecimal limiteCredit = BigDecimal.ZERO;
    private Timestamp dateMaj;

    // Constructeurs
    // Constructeurs
    public CompteClient() {
        this.solde = BigDecimal.ZERO;
        this.limiteCredit = BigDecimal.ZERO;
    }

    public CompteClient(int id, int clientId, BigDecimal solde, Timestamp dateCreation, Timestamp dateMaj) {
        this.id = id;
        this.clientId = clientId;
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.dateMaj = dateMaj;
    }

    public CompteClient(int id, Utilisateur client, BigDecimal solde, Timestamp dateCreation, Timestamp dateMaj) {
        this.id = id;
        this.client = client;
        this.clientId = client.getId();
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.dateMaj = dateMaj;
    }

    
    public BigDecimal getLimiteCredit() {
        return limiteCredit != null ? limiteCredit : BigDecimal.ZERO;
    }
    
    public void setLimiteCredit(BigDecimal limiteCredit) {
        this.limiteCredit = limiteCredit;
    }
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public Utilisateur getClient() { return client; }
    public void setClient(Utilisateur client) { 
        this.client = client;
        if (client != null) {
            this.clientId = client.getId();
        }
    }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public Timestamp getDateMaj() { return dateMaj; }
    public void setDateMaj(Timestamp dateMaj) { this.dateMaj = dateMaj; }
    
    // Méthodes de calcul
    public BigDecimal getSoldeDisponible() {
        return getSolde().add(getLimiteCredit());
    }
    
    public boolean peutPayer(BigDecimal montant) {
        return getSoldeDisponible().compareTo(montant) >= 0;
    }
    
    public BigDecimal calculerNouveauSolde(BigDecimal montant) {
        return getSolde().subtract(montant);
    }
    
    public boolean estDansLimites(BigDecimal nouveauSolde) {
        return nouveauSolde.compareTo(getLimiteCredit().negate()) >= 0;
    }
    
    public BigDecimal getCreditUtilise() {
        if (getSolde().compareTo(BigDecimal.ZERO) < 0) {
            return getSolde().abs();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "CompteClient{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", solde=" + solde +
                ", dateCreation=" + dateCreation +
                ", dateMaj=" + dateMaj +
                '}';
    }
}