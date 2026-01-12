package com.spot69.model;

import java.time.LocalDateTime;

public class Plat {
    private int id;
    private int qtePoints;
    private String nom;
    private String description;
    private double prix;
    private String image;          // Nouveau
    private Integer categorieId;
    private Integer sousCategorieId;
    private String statut; // visible | deleted
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private int utilisateurId;
    private Integer produitId;         // Pour lier à un produit existant
    public String typePlat;
    
    // Objets liés pour l'affichage
    private Integer rayonId; 
    private Rayon rayon;
    private MenuCategorie categorie;
    private MenuCategorie sousCategorie;
    private Produit produit;
    private MenuCategorie categorieMenu;
    private MenuCategorie sousCategorieMenu;
    
    // --- Constructeurs ---
    
    public Plat() {}
    
    public Plat(int id, String nom, String description, double prix, String image,
                int categorieId, String statut, LocalDateTime creationDate,
                LocalDateTime updateDate, int utilisateurId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.categorieId = categorieId;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.utilisateurId = utilisateurId;
    }
    
    public Plat(int id, String nom, String description, double prix, String image,
                int categorieId, int sousCategorieId, String statut, LocalDateTime creationDate,
                LocalDateTime updateDate, int utilisateurId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.categorieId = categorieId;
        this.sousCategorieId = sousCategorieId;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.utilisateurId = utilisateurId;
    }
    
    // Constructeur complet avec tous les champs
    public Plat(int id, String nom, String description, double prix, String image,
                Integer rayonId, Integer categorieId, Integer sousCategorieId,
                String statut, LocalDateTime creationDate, LocalDateTime updateDate,
                int utilisateurId, Integer produitId, int qtePoints, String typePlat) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.image = image;
        this.rayonId = rayonId;
        this.categorieId = categorieId;
        this.sousCategorieId = sousCategorieId;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.utilisateurId = utilisateurId;
        this.produitId = produitId;
        this.qtePoints = qtePoints;
        this.typePlat = typePlat;
    }
    
    // --- Getters & Setters ---
    
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public int getQtePoints() {
        return qtePoints;
    }

    public void setQtePoints(int qtePoints) {
        this.qtePoints = qtePoints;
    }

    public String getNom() { 
        return nom; 
    }
    
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getTypePlat() { 
        return typePlat; 
    }
    
    public void setTypePlat(String typePlat) { 
        this.typePlat = typePlat; 
    }

    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }

    public double getPrix() { 
        return prix; 
    }
    
    public void setPrix(double prix) { 
        this.prix = prix; 
    }

    public String getImage() { 
        return image; 
    }
    
    public void setImage(String image) { 
        this.image = image; 
    }

    // Rayon
    public Integer getRayonId() { 
        return rayonId; 
    }
    
    public void setRayonId(Integer rayonId) { 
        this.rayonId = rayonId; 
    }

    // Catégorie
    public Integer getCategorieId() { 
        return categorieId; 
    }
    
    public void setCategorieId(Integer categorieId) { 
        this.categorieId = categorieId; 
    }

    // Sous-catégorie
    public Integer getSousCategorieId() { 
        return sousCategorieId; 
    }
    
    public void setSousCategorieId(Integer sousCategorieId) { 
        this.sousCategorieId = sousCategorieId; 
    }

    public String getStatut() { 
        return statut; 
    }
    
    public void setStatut(String statut) { 
        this.statut = statut; 
    }

    public LocalDateTime getCreationDate() { 
        return creationDate; 
    }
    
    public void setCreationDate(LocalDateTime creationDate) { 
        this.creationDate = creationDate; 
    }

    public LocalDateTime getUpdateDate() { 
        return updateDate; 
    }
    
    public void setUpdateDate(LocalDateTime updateDate) { 
        this.updateDate = updateDate; 
    }

    public int getUtilisateurId() { 
        return utilisateurId; 
    }
    
    public void setUtilisateurId(int utilisateurId) { 
        this.utilisateurId = utilisateurId; 
    }
    
    // Produit lié
    public Integer getProductId() { 
        return produitId; 
    }
    
    public void setProductId(Integer produitId) { 
        this.produitId = produitId; 
    }

    // --- Objets liés ---
    
    public MenuCategorie getCategorieMenu() {
        return categorieMenu;
    }

    public void setCategorieMenu(MenuCategorie categorieMenu) {
        this.categorieMenu = categorieMenu;
    }

    public MenuCategorie getSousCategorieMenu() {
        return sousCategorieMenu;
    }

    public void setSousCategorieMenu(MenuCategorie sousCategorieMenu) {
        this.sousCategorieMenu = sousCategorieMenu;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public Rayon getRayon() {
        return rayon;
    }

    public void setRayon(Rayon rayon) {
        this.rayon = rayon;
    }

    public MenuCategorie getCategorie() {
        return categorie;
    }

    public void setCategorie(MenuCategorie categorie) {
        this.categorie = categorie;
    }

    public MenuCategorie getSousCategorie() {
        return sousCategorie;
    }

    public void setSousCategorie(MenuCategorie sousCategorie) {
        this.sousCategorie = sousCategorie;
    }
    
    // --- Méthode utilitaire ---
    
    @Override
    public String toString() {
        return "Plat{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", rayonId=" + rayonId +
                ", categorieId=" + categorieId +
                ", sousCategorieId=" + sousCategorieId +
                ", produitId=" + produitId +
                '}';
    }
}