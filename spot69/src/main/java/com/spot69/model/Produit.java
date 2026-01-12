package com.spot69.model;

import java.math.BigDecimal;
import java.util.Date;

public class Produit {
    private int id;
    private int qtePoints;
    private String nom;
    private String description;
    private String imageUrl;
    private String emplacement;
    private String codeProduit;
    private int categorieId;
    private int qteEnStock;
    private int sousCategorieId;
    private int prixAchatParUniteVente;
    private String uniteVente;          // ex : caisse, kg
    private String contenuParUnite;     // ex : 24 bouteilles/caisse
    private int seuilAlerte;
    private BigDecimal prixVente;
    private BigDecimal prixTotal;
    private int utilisateurId;
    private Date createdAt;
    private Date updatedAt;
    private Integer deletedBy; 
    // peut être null si non supprimé
    private Integer rayonId; 
    private Rayon rayon;

    private MenuCategorie categorie;
    private MenuCategorie sousCategorie;
    
    private MenuCategorie categorieMenu;
    private MenuCategorie sousCategorieMenu;

   



  
    // --- Getters & Setters ---
    
 // Rayon
    public Integer getRayonId() { 
        return rayonId; 
    }
    
    public void setRayonId(Integer rayonId) { 
        this.rayonId = rayonId; 
    }
    
    public Rayon getRayon() {
        return rayon;
    }

    public void setRayon(Rayon rayon) {
        this.rayon = rayon;
    }


    
    public int getQtePoints() {
        return qtePoints;
    }

    public void setQtePoints(int qtePoints) {
        this.qtePoints = qtePoints;
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
    


    public String getImageUrl() { return imageUrl; } // Getter pour imageUrl
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // Setter pour imageUrl
    
    public int getQteEnStock() {
        return qteEnStock;
    }

    public void setQteEnStock(int qteEnStock) {
        this.qteEnStock = qteEnStock;
    }
    


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrixAchatParUniteVente() {
        return prixAchatParUniteVente;
    }

    public void setPrixAchatParUniteVente(int prixAchatParUniteVente) {
        this.prixAchatParUniteVente = prixAchatParUniteVente;
    }

    
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
  
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getSousCategorieId() {
        return sousCategorieId;
    }

    public void setSousCategorieId(int sousCategorieId) {
        this.sousCategorieId = sousCategorieId;
    }

    public String getUniteVente() {
        return uniteVente;
    }

    public void setUniteVente(String uniteVente) {
        this.uniteVente = uniteVente;
    }

    public String getContenuParUnite() {
        return contenuParUnite;
    }

    public void setContenuParUnite(String contenuParUnite) {
        this.contenuParUnite = contenuParUnite;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }
    
    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
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
    
    public String getStatutStock() {
        if (qteEnStock == 0) {
            return "RUPTURE";
        } else if (qteEnStock <= seuilAlerte) {
            return "STOCK_BAS";
        } else {
            return "STOCK_OK";
        }
    }

    
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", emplacement='" + emplacement + '\'' +
                ", codeProduit='" + codeProduit + '\'' +
                ", categorieId=" + categorieId +
                ", sousCategorieId=" + sousCategorieId +
                ", qteEnStock=" + qteEnStock +
                ", uniteVente='" + uniteVente + '\'' +
                ", contenuParUnite='" + contenuParUnite + '\'' +
                ", seuilAlerte=" + seuilAlerte +
                ", prixVente=" + prixVente +
                ", prixTotal=" + prixTotal +
                ", utilisateurId=" + utilisateurId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedBy=" + deletedBy +
                ", statutStock='" + getStatutStock() + '\'' +
                ", categorie=" + (categorie != null ? categorie.toString() : "null") +
                ", sousCategorie=" + (sousCategorie != null ? sousCategorie.toString() : "null") +
                '}';
    }


}
