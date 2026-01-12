package com.spot69.model;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class Facture {
    private int id;
    private String noFacture;
    private String moyenPaiement;
    private Integer deletedInterval; // en jours

    private Integer montantTotal;
    private Integer montantVerse = 0;
    private boolean isCredit = false;
    private Integer solde;
    private Fournisseur fournisseur;
    private Utilisateur utilisateur;
    private Utilisateur deletedBy;
    private Date createdAt;
    private Date updatedAt;

    public Facture() {}

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Integer getDeletedInterval() {
        return deletedInterval;
    }

    public void setDeletedInterval(Integer deletedInterval) {
        this.deletedInterval = deletedInterval;
    }

    
    public String getMoyenPaiement() {
        return moyenPaiement;
    }

    public void setMoyenPaiement(String moyenPaiement) {
        this.moyenPaiement = moyenPaiement;
    }

    public String getNoFacture() {
        return noFacture;
    }

    public void setNoFacture(String noFacture) {
        this.noFacture = noFacture;
    }

    public int getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Integer montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Integer getMontantVerse() {
        return montantVerse;
    }

    public void setMontantVerse(Integer montantVerse) {
        this.montantVerse = montantVerse;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }

    public Integer getSolde() {
        return solde;
    }

    public void setSolde(Integer solde) {
        this.solde = solde;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
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
