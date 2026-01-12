package com.spot69.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class FactureDetail {
    private int id;
    private int qteVendu;
    private Facture facture;
    private Produit produit;
    private int quantite;
    private Integer prixAchatParUniteMesure;
    private Integer prixAchatTotal;
    private Integer qteUnite;
    private Integer prixRevientUnite;
    private Utilisateur deletedBy;
    private Date createdAt;
    private Date updatedAt;
    private int stockDebut;
    private int stockFin;
    private int qteIn;   // Quantité ajoutée
    private int qteOut;  // Quantité vendue

    public int getStockDebut() {
        return stockDebut;
    }

    public void setStockDebut(int stockDebut) {
        this.stockDebut = stockDebut;
    }

    public int getStockFin() {
        return stockFin;
    }

    public void setStockFin(int stockFin) {
        this.stockFin = stockFin;
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

    public void setQteOut(int qteOut) {
        this.qteOut = qteOut;
    }


    public FactureDetail() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQteVendu() {
        return qteVendu;
    }

    public void setQteVendu(int qteVendu) {
        this.qteVendu = qteVendu;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public Integer getPrixAchatParUniteMesure() {
        return prixAchatParUniteMesure;
    }

    public void setPrixAchatParUniteMesure(Integer prixAchatParUniteMesure) {
        this.prixAchatParUniteMesure = prixAchatParUniteMesure;
    }

    public Integer getPrixAchatTotal() {
        return prixAchatTotal;
    }

    public void setPrixAchatTotal(Integer prixAchatTotal) {
        this.prixAchatTotal = prixAchatTotal;
    }

    public Integer getQteUnite() {
        return qteUnite;
    }

    public void setQteUnite(Integer qteUnite) {
        this.qteUnite = qteUnite;
    }

    public Integer getPrixRevientUnite() {
        return prixRevientUnite;
    }

    public void setPrixRevientUnite(Integer prixRevientUnite) {
        this.prixRevientUnite = prixRevientUnite;
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
