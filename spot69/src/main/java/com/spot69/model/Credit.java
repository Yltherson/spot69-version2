package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Credit {
    private int id;
    private int utilisateurId;
    private int commandeId;
    private int montantTotal;
    private int montantPaye;
    private String statut;
    private Timestamp dateCredit;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Associations (optionnel si tu veux lier directement)
    private Utilisateur utilisateur;
    private Commande commande;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public int getCommandeId() { return commandeId; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }

    public int getMontantTotal() { return montantTotal; }
    public void setMontantTotal(int montantTotal) { this.montantTotal = montantTotal; }

    public int getMontantPaye() { return montantPaye; }
    public void setMontantPaye(int montantPaye) { this.montantPaye = montantPaye; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Timestamp getDateCredit() { return dateCredit; }
    public void setDateCredit(Timestamp dateCredit) { this.dateCredit = dateCredit; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
}

