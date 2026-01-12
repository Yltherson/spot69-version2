package com.spot69.model;

import java.sql.Date;
import java.sql.Timestamp;

public class CommandeJournaliere {
    private int id;
    private int utilisateurId;
    private Date dateJour;
    private int nombreCommandes;
    private double montantTotal;
    private Timestamp derniereMaj;
    
    // Constructeurs
    public CommandeJournaliere() {}
    
    public CommandeJournaliere(int id, int utilisateurId, Date dateJour, 
                              int nombreCommandes, double montantTotal, Timestamp derniereMaj) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.dateJour = dateJour;
        this.nombreCommandes = nombreCommandes;
        this.montantTotal = montantTotal;
        this.derniereMaj = derniereMaj;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public Date getDateJour() { return dateJour; }
    public void setDateJour(Date dateJour) { this.dateJour = dateJour; }
    
    public int getNombreCommandes() { return nombreCommandes; }
    public void setNombreCommandes(int nombreCommandes) { this.nombreCommandes = nombreCommandes; }
    
    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    
    public Timestamp getDerniereMaj() { return derniereMaj; }
    public void setDerniereMaj(Timestamp derniereMaj) { this.derniereMaj = derniereMaj; }
}