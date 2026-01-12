package com.spot69.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class Evenement {
    private int id;
    private String titre;
    private String artisteGroupe;
    private String media_path;
    private LocalDateTime dateEvent;
    private String description;
    private int utilisateurId;
    private int capaciteTotale; // Nouveau
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deletedBy;
    private String statut;
    
    // Relations
    private List<TypeTableEvenement> typesTables; // Nouveau
    
    // =====================
    // CONSTRUCTEURS
    // =====================
    public Evenement() {}
    
    public Evenement(String titre, String artisteGroupe, LocalDateTime dateEvent, 
                     String description, int utilisateurId, String statut, int capaciteTotale) {
        this.titre = titre;
        this.artisteGroupe = artisteGroupe;
        this.dateEvent = dateEvent;
        this.description = description;
        this.utilisateurId = utilisateurId;
        this.statut = statut;
        this.capaciteTotale = capaciteTotale;
    }
    
    // =====================
    // GETTERS & SETTERS
    // =====================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getArtisteGroupe() { return artisteGroupe; }
    public void setArtisteGroupe(String artisteGroupe) { this.artisteGroupe = artisteGroupe; }
    
    public String getMediaPath() { return media_path; }
    public void setMediaPath(String media_path) { this.media_path = media_path; }
    
    public String getMedia_path() { return media_path; }
    public void setMedia_path(String media_path) { this.media_path = media_path; }
    
    public LocalDateTime getDateEvent() { return dateEvent; }
    public void setDateEvent(LocalDateTime dateEvent) { this.dateEvent = dateEvent; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public int getCapaciteTotale() { return capaciteTotale; } // Nouveau
    public void setCapaciteTotale(int capaciteTotale) { this.capaciteTotale = capaciteTotale; } // Nouveau
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public List<TypeTableEvenement> getTypesTables() { return typesTables; } // Nouveau
    public void setTypesTables(List<TypeTableEvenement> typesTables) { this.typesTables = typesTables; } // Nouveau
    
    // =====================
    // MÉTHODES UTILITAIRES
    // =====================
    public Date getDateEventAsDate() {
        return dateEvent != null ? Date.from(dateEvent.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public Date getCreatedAtAsDate() {
        return createdAt != null ? Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public Date getUpdatedAtAsDate() {
        return updatedAt != null ? Date.from(updatedAt.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public boolean hasMedia() {
        return media_path != null && !media_path.trim().isEmpty();
    }
    
    public String getFullMediaPath() {
        if (media_path == null || media_path.trim().isEmpty()) {
            return null;
        }
        if (media_path.startsWith("/") || media_path.startsWith("http")) {
            return media_path;
        }
        return "/" + media_path;
    }
    
    // Méthodes pour les tables
    public boolean hasTables() {
        return typesTables != null && !typesTables.isEmpty();
    }
    
    public TypeTableEvenement getTableById(int tableId) {
        if (typesTables != null) {
            for (TypeTableEvenement table : typesTables) {
                if (table.getId() == tableId) {
                    return table;
                }
            }
        }
        return null;
    }
    
    public TypeTableEvenement getTableByIndex(int index) {
        if (typesTables != null && index >= 0 && index < typesTables.size()) {
            return typesTables.get(index);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Evenement{id=" + id + ", titre='" + titre + "', capaciteTotale=" + capaciteTotale + "}";
    }
}