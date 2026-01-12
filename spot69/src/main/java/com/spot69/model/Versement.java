package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Versement {
    private int id;
    private int creditId;
    private int utilisateurId;
    private BigDecimal montant;
    private String notes;
    private Timestamp dateVersement;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private Credit credit;
    private Utilisateur utilisateur;
    
    private String modePaiement;

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }


    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCreditId() { return creditId; }
    public void setCreditId(int creditId) { this.creditId = creditId; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getDateVersement() { return dateVersement; }
    public void setDateVersement(Timestamp dateVersement) { this.dateVersement = dateVersement; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Credit getCredit() { return credit; }
    public void setCredit(Credit credit) { this.credit = credit; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
}
