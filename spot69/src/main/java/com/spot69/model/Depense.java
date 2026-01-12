package com.spot69.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Depense {
    private int id;
    private int caissiereId;
    private int idType;
    private int montant;
    private String notes;
    private Date date;
    private int userId;
    private Utilisateur utilisateur;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Relation
    private DepenseType type;

    // Getters / Setters
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public int getCaissiereId() { return caissiereId; }
    public void setCaissiereId(int caissiereId) { this.caissiereId = caissiereId; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdType() { return idType; }
    public void setIdType(int idType) { this.idType = idType; }

    public int getMontant() { return montant; }
    public void setMontant(int montant) { this.montant = montant; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public DepenseType getType() { return type; }
    public void setType(DepenseType type) { this.type = type; }
}
