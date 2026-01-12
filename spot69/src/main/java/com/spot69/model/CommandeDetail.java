package com.spot69.model;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CommandeDetail {

    private int id;
    private int commandeId;
    private Integer produitId; // nullable
    private Integer platId;    // nullable
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal sousTotal;
    private String notes;      // nullable
    private String statut;     // enum: VISIBLE, DELETED
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer deletedBy; // ID de l'utilisateur (nullable)
    private int panierId; // à ajouter si pas encore présent
    private Produit produit;
    private Plat plat;


    // Getters & Setters
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public Plat getPlat() { return plat; }
    public void setPlat(Plat plat) { this.plat = plat; }


    public int getId() {
        return id;
    }
    
 

    public void setId(int id) {
        this.id = id;
    }
    
    public int getPanierId() {
        return panierId;
    }

    public void setPanierId(int panierId) {
        this.panierId = panierId;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public Integer getProduitId() {
        return produitId;
    }

    public void setProduitId(Integer produitId) {
        this.produitId = produitId;
    }

    public Integer getPlatId() {
        return platId;
    }

    public void setPlatId(Integer platId) {
        this.platId = platId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(BigDecimal sousTotal) {
        this.sousTotal = sousTotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }
}
