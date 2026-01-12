package com.spot69.model;

import java.sql.Timestamp;

public class PointConfig {
    private int id;
    private String typeConfig; // 'PRODUIT', 'CATEGORIE', etc.
    private Integer refId; // ID du produit, catégorie, etc.
    private int points;
    private Double conditionValeur; 
    private String conditionType; // '>=', '<=', '=', etc.
    private Timestamp dateDebut;
    private Timestamp dateFin;
    private Integer utilisateurId;
    private String statut; // 'ACTIF', 'INACTIF', 'DELETED'
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructeurs
    public PointConfig() {}
    
    public PointConfig(int id, String typeConfig, Integer refId, int points, 
                      Double conditionValeur, String conditionType,
                      Timestamp dateDebut, Timestamp dateFin, Integer utilisateurId,
                      String statut, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.typeConfig = typeConfig;
        this.refId = refId;
        this.points = points;
        this.conditionValeur = conditionValeur;
        this.conditionType = conditionType;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateurId = utilisateurId;
        this.statut = statut;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTypeConfig() { return typeConfig; }
    public void setTypeConfig(String typeConfig) { this.typeConfig = typeConfig; }
    
    public Integer getRefId() { return refId; }
    public void setRefId(Integer refId) { this.refId = refId; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public Double getConditionValeur() { return conditionValeur; }
    public void setConditionValeur(Double conditionValeur) { this.conditionValeur = conditionValeur; }
    
    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }
    
    public Timestamp getDateDebut() { return dateDebut; }
    public void setDateDebut(Timestamp dateDebut) { this.dateDebut = dateDebut; }
    
    public Timestamp getDateFin() { return dateFin; }
    public void setDateFin(Timestamp dateFin) { this.dateFin = dateFin; }
    
    public Integer getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Integer utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}