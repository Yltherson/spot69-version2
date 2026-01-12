package com.spot69.model;

import java.sql.Timestamp;

public class Point {
    private int id;
    private int utilisateurId;
    private Integer commandeId;
    private String sourceType; // 'COMMANDE', 'PROMOTION', etc.
    private Integer sourceConfigId;
    private Integer reservationId;
    private int pointsObtenus;
    private Timestamp dateObtention;
    private Timestamp dateExpiration;
    private String statut; // 'VALIDE', 'UTILISE', 'EXPIRE', 'DELETED'
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Commande commande;
    private PointConfig pointConfig;
    
    // Constructeurs
    public Point() {}
    
    public Point(int id, int utilisateurId, Integer commandeId, String sourceType,
                 Integer sourceConfigId, int pointsObtenus, Timestamp dateObtention,
                 Timestamp dateExpiration, String statut, String notes,
                 Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.commandeId = commandeId;
        this.sourceType = sourceType;
        this.sourceConfigId = sourceConfigId;
        this.pointsObtenus = pointsObtenus;
        this.dateObtention = dateObtention;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Integer getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }
    
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public Integer getCommandeId() { return commandeId; }
    public void setCommandeId(Integer commandeId) { this.commandeId = commandeId; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public Integer getSourceConfigId() { return sourceConfigId; }
    public void setSourceConfigId(Integer sourceConfigId) { this.sourceConfigId = sourceConfigId; }
    
    public int getPointsObtenus() { return pointsObtenus; }
    public void setPointsObtenus(int pointsObtenus) { this.pointsObtenus = pointsObtenus; }
    
    public Timestamp getDateObtention() { return dateObtention; }
    public void setDateObtention(Timestamp dateObtention) { this.dateObtention = dateObtention; }
    
    public Timestamp getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Timestamp dateExpiration) { this.dateExpiration = dateExpiration; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }
    
    public PointConfig getPointConfig() { return pointConfig; }
    public void setPointConfig(PointConfig pointConfig) { this.pointConfig = pointConfig; }
}