package com.spot69.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TypeTableEvenement {
    private int id;
    private int evenementId;
    private String nom;
    private String description;
    private int capacite;
    private BigDecimal prix;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations
    private Evenement evenement;
    private int quantiteDisponible; // Pour affichage
    
    // Constructeurs
    public TypeTableEvenement() {}
    
    public TypeTableEvenement(int evenementId, String nom, String description, 
                              int capacite, BigDecimal prix, String statut) {
        this.evenementId = evenementId;
        this.nom = nom;
        this.description = description;
        this.capacite = capacite;
        this.prix = prix;
        this.statut = statut;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getEvenementId() { return evenementId; }
    public void setEvenementId(int evenementId) { this.evenementId = evenementId; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    
    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }
    
    // Méthodes utilitaires
    public Date getCreatedAtAsDate() {
        return createdAt != null ? Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public Date getUpdatedAtAsDate() {
        return updatedAt != null ? Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public boolean isActif() {
        return "ACTIF".equals(statut);
    }
    
    public BigDecimal getPrixTotal(int quantite) {
        return prix.multiply(new BigDecimal(quantite));
    }
    
    @Override
    public String toString() {
        return "TypeTableEvenement{id=" + id + ", nom='" + nom + "', capacite=" + capacite + 
               ", prix=" + prix + ", statut='" + statut + "'}";
    }
}