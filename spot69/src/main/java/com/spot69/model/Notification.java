package com.spot69.model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private String generatedBy; 
    private String isRead;   // SYSTEM
    private int toUser;           // ID de l'utilisateur destinataire
    private int updatedBy;
    private String messages;
    private String typeNotif;     // <-- Ajout ici
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deletedBy;    // Peut être null
    private String status;        // VISIBLE | DELETED
    private Utilisateur utilisateur;

    // Constructeurs
    public Notification() {
    }

    public Notification(int id, String generatedBy, int toUser, String messages,
                        String typeNotif, LocalDateTime createdAt, LocalDateTime updatedAt,
                        Integer deletedBy, String status) {
        this.id = id;
        this.generatedBy = generatedBy;
        this.toUser = toUser;
        this.messages = messages;
        this.typeNotif = typeNotif;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedBy = deletedBy;
        this.status = status;
    }

    public Notification(int id, String generatedBy, int toUser, String messages,
                        String typeNotif, LocalDateTime createdAt, LocalDateTime updatedAt,
                        int updatedBy, Integer deletedBy, String status) {
        this.id = id;
        this.generatedBy = generatedBy;
        this.toUser = toUser;
        this.messages = messages;
        this.typeNotif = typeNotif;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
        this.status = status;
    } 
    public Notification(int id, String generatedBy, int toUser, String messages,
            String typeNotif, LocalDateTime createdAt, LocalDateTime updatedAt,
            int updatedBy, Integer deletedBy, String status, String isRead) {
this.id = id;
this.generatedBy = generatedBy;
this.toUser = toUser;
this.messages = messages;
this.typeNotif = typeNotif;
this.createdAt = createdAt;
this.updatedAt = updatedAt;
this.updatedBy = updatedBy;
this.deletedBy = deletedBy;
this.status = status;
this.isRead = isRead;
}

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public String getIsRead() { return isRead; }
    public void setIsRead(String isRead) { this.isRead = isRead; }

    public int getToUser() { return toUser; }
    public void setToUser(int toUser) { this.toUser = toUser; }

    public String getMessages() { return messages; }
    public void setMessages(String messages) { this.messages = messages; }

    public String getTypeNotif() { return typeNotif; }   // <-- Getter ajouté
    public void setTypeNotif(String typeNotif) { this.typeNotif = typeNotif; } // <-- Setter ajouté

    public int getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(int updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // toString()
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", generatedBy='" + generatedBy + '\'' +
                ", toUser=" + toUser +
                ", messages='" + messages + '\'' +
                ", typeNotif='" + typeNotif + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedBy=" + deletedBy +
                ", status='" + status + '\'' +
                '}';
    }
}
