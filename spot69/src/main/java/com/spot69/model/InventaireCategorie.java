package com.spot69.model;

import java.util.Date;
import java.util.List;

public class InventaireCategorie {

    private int id;
    private String nom;
    private String description;
    private String statut;
    private Date createdAt;
    private Date updatedAt;
    private Integer deletedBy;
    
    private List<InventaireCategorie> sousCategories; // avec ses getter/setter
    
    // Spécifique aux sous-catégories : id de la catégorie parente
    private Integer categorieId;

    // Indique si c'est une catégorie ou une sous-catégorie
    private String type; // "CATEGORIE" ou "SOUS_CATEGORIE"

    // Constructeur par défaut
    public InventaireCategorie() {}

    // Getters & Setters
    public List<InventaireCategorie> getSousCategories() {
        return sousCategories;
    }

    public void setSousCategories(List<InventaireCategorie> sousCategories) {
        this.sousCategories = sousCategories;
    }


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
