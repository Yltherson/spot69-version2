package com.spot69.model;

import java.sql.Timestamp;

public class PointHistorique {
    private Integer id;
    private Integer pointId;
    private Integer commandeId;
    private Integer utilisateurId;
    private Integer reservationId;
    private String typeAction;
    private String sourceType;
    private Integer sourceRefId;
    private Integer quantite;
    private Integer pointsParUnite;
    private Integer totalPoints;
    private Integer ancienPoint;
    private Integer nouveauPoint;
    private Integer difference;
    private String notes;
    private String ipAdresse;
    private Integer utilisateurModif;
    private Timestamp createdAt;
    
    // Constructeurs
    public PointHistorique() {}
    
    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getPointId() { return pointId; }
    public void setPointId(Integer pointId) { this.pointId = pointId; }
    
    public Integer getCommandeId() { return commandeId; }
    public void setCommandeId(Integer commandeId) { this.commandeId = commandeId; }
    
    public Integer getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Integer utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    
    public String getTypeAction() { return typeAction; }
    public void setTypeAction(String typeAction) { this.typeAction = typeAction; }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public Integer getSourceRefId() { return sourceRefId; }
    public void setSourceRefId(Integer sourceRefId) { this.sourceRefId = sourceRefId; }
    
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    
    public Integer getPointsParUnite() { return pointsParUnite; }
    public void setPointsParUnite(Integer pointsParUnite) { this.pointsParUnite = pointsParUnite; }
    
    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
    
    public Integer getAncienPoint() { return ancienPoint; }
    public void setAncienPoint(Integer ancienPoint) { this.ancienPoint = ancienPoint; }
    
    public Integer getNouveauPoint() { return nouveauPoint; }
    public void setNouveauPoint(Integer nouveauPoint) { this.nouveauPoint = nouveauPoint; }
    
    public Integer getDifference() { return difference; }
    public void setDifference(Integer difference) { this.difference = difference; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getIpAdresse() { return ipAdresse; }
    public void setIpAdresse(String ipAdresse) { this.ipAdresse = ipAdresse; }
    
    public Integer getUtilisateurModif() { return utilisateurModif; }
    public void setUtilisateurModif(Integer utilisateurModif) { this.utilisateurModif = utilisateurModif; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "PointHistorique{" +
                "id=" + id +
                ", typeAction='" + typeAction + '\'' +
                ", utilisateurId=" + utilisateurId +
                ", ancienPoint=" + ancienPoint +
                ", nouveauPoint=" + nouveauPoint +
                ", difference=" + difference +
                ", createdAt=" + createdAt +
                '}';
    }
}