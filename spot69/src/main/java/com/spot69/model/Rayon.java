package com.spot69.model;

import java.time.LocalDateTime;

public class Rayon {
    private int id;
    private String nom;
    private String description;
    private String statut; // VISIBLE | DELETED
    private String imageUrl;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private int utilisateurId;

    public Rayon() {}

    public Rayon(int id, String nom, String description, String statut,
                 String imageUrl, LocalDateTime creationDate, LocalDateTime updateDate,
                 int utilisateurId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.statut = statut;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.utilisateurId = utilisateurId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
}
