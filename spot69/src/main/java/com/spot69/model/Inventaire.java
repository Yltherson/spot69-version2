package com.spot69.model;

import java.util.Date;

public class Inventaire {
    private int id;
    private FactureDetail factureDetail;
    private Produit produit;
    private Utilisateur utilisateur;
    private int quantiteStock = 0;
    private String emplacement;
    private Fournisseur fournisseur;
    private Utilisateur deletedBy;
    private Date createdAt;
    private Date updatedAt;

    public Inventaire() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FactureDetail getFactureDetail() {
        return factureDetail;
    }

    public void setFactureDetail(FactureDetail factureDetail) {
        this.factureDetail = factureDetail;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Utilisateur getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Utilisateur deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
