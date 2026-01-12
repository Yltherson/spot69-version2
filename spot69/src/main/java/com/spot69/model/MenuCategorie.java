package com.spot69.model;

import java.time.LocalDateTime;

public class MenuCategorie {
    private int id;
    private String nom;
    private String description;
    private String statut; // visible | deleted
    private LocalDateTime creationDate;
    private Rayon rayon; 
    private LocalDateTime updateDate;
    private int utilisateurId;
    private Integer parentId;
    private String imageUrl; // Ajout du champ Image_Url

    public MenuCategorie() {}

    public MenuCategorie(int id, String nom, String description, String statut,
                     LocalDateTime creationDate, LocalDateTime updateDate, int utilisateurId, String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.utilisateurId = utilisateurId;
        this.imageUrl = imageUrl;
    }
    
    public MenuCategorie(int id, Rayon rayon, String nom, String description, String statut,
            LocalDateTime creationDate, LocalDateTime updateDate,
            int utilisateurId, Integer parentId, String imageUrl) {

this.id = id;
this.rayon = rayon;
this.nom = nom;
this.description = description;
this.statut = statut;
this.creationDate = creationDate;
this.updateDate = updateDate;
this.utilisateurId = utilisateurId;
this.parentId = parentId;
this.imageUrl = imageUrl;
}


    // Getters et Setters
    public Rayon getRayon() { return rayon; }
    public void setRayon(Rayon rayon) { this.rayon = rayon; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getImageUrl() { return imageUrl; } // Getter pour imageUrl
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // Setter pour imageUrl
}
