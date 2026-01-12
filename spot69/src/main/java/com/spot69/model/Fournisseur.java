package com.spot69.model;

import java.math.BigDecimal;
import java.util.Date;

public class Fournisseur {

    private int id;
    private String nom;
    private String contact;
    private String telephone;
    private String email;
    private String devisePreference; // HTG, USD
    private String modePaiement;     // CASH, VIREMENT, CREDIT
    private boolean creditAutorise;
    private BigDecimal limiteCredit;
    private BigDecimal soldeActuel;
    private String statut; // VISIBLE, DELETED
    private Date createdAt;
    private Date updatedAt;
    private Integer deletedBy;

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDevisePreference() {
        return devisePreference;
    }

    public void setDevisePreference(String devisePreference) {
        this.devisePreference = devisePreference;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public boolean isCreditAutorise() {
        return creditAutorise;
    }

    public void setCreditAutorise(boolean creditAutorise) {
        this.creditAutorise = creditAutorise;
    }

    public BigDecimal getLimiteCredit() {
        return limiteCredit;
    }

    public void setLimiteCredit(BigDecimal limiteCredit) {
        this.limiteCredit = limiteCredit;
    }

    public BigDecimal getSoldeActuel() {
        return soldeActuel;
    }

    public void setSoldeActuel(BigDecimal soldeActuel) {
        this.soldeActuel = soldeActuel;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }
}
