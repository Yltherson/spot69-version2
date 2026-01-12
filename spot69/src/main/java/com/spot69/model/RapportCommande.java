package com.spot69.model;

import java.sql.Timestamp;

public class RapportCommande {
    private Integer platId;
    private Integer produitId;
    private String nomPlat;     // ou nom produit
    private int quantiteTotale;
    private Integer montantTotal;

    private Integer prixUnitaire;
    private Timestamp dateVente;

    // getters/setters
    public Integer getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(Integer prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Timestamp getDateVente() { return dateVente; }
    public void setDateVente(java.sql.Timestamp dateVente2) { this.dateVente = dateVente2; }

    // Getters et setters
    public Integer getPlatId() { return platId; }
    public void setPlatId(Integer platId) { this.platId = platId; }

    public Integer getProduitId() { return produitId; }
    public void setProduitId(Integer produitId) { this.produitId = produitId; }

    public String getNomPlat() { return nomPlat; }
    public void setNomPlat(String nomPlat) { this.nomPlat = nomPlat; }

    public int getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(int quantiteTotale) { this.quantiteTotale = quantiteTotale; }

    public Integer getMontantTotal() { return montantTotal; }
    public void setMontantTotal(Integer montantTotal) { this.montantTotal = montantTotal; }
}
