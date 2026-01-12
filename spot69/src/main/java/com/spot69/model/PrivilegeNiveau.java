package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PrivilegeNiveau {
    private int id;
    private String nom;
    private int seuilPoints;
    private BigDecimal pourcentageReduction;
    private String description;
    private String couleur;
    private String statut; // ACTIF, INACTIF
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    // Constructeurs
    public PrivilegeNiveau() {}

    public PrivilegeNiveau(int id, String nom, int seuilPoints, BigDecimal pourcentageReduction, 
                          String description, String couleur, String statut) {
        this.id = id;
        this.nom = nom;
        this.seuilPoints = seuilPoints;
        this.pourcentageReduction = pourcentageReduction;
        this.description = description;
        this.couleur = couleur;
        this.statut = statut;
    }

    public PrivilegeNiveau(int id, String nom, int seuilPoints, BigDecimal pourcentageReduction, 
                          String description, String couleur, String statut,
                          Timestamp createdAt, Timestamp updatedAt, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.nom = nom;
        this.seuilPoints = seuilPoints;
        this.pourcentageReduction = pourcentageReduction;
        this.description = description;
        this.couleur = couleur;
        this.statut = statut;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getSeuilPoints() { return seuilPoints; }
    public void setSeuilPoints(int seuilPoints) { this.seuilPoints = seuilPoints; }

    public BigDecimal getPourcentageReduction() { return pourcentageReduction; }
    public void setPourcentageReduction(BigDecimal pourcentageReduction) { this.pourcentageReduction = pourcentageReduction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }

    // Méthode pour obtenir la classe CSS basée sur la couleur/nom
    public String getBadgeClass() {
        switch(nom.toUpperCase()) {
            case "VIP": return "badge-vip";
            case "GOLD": return "badge-gold";
            case "SILVER": return "badge-silver";
            case "BRONZE": return "badge-bronze";
            default: return "badge-secondary";
        }
    }

    // Méthode pour vérifier si un niveau est éligible selon les points
    public boolean isEligible(int points) {
        return points >= seuilPoints;
    }

    // Méthode pour obtenir le niveau basé sur les points
    public static String getNiveauByPoints(int points, java.util.List<PrivilegeNiveau> niveaux) {
        PrivilegeNiveau currentNiveau = niveaux.get(0); // Par défaut le premier (bronze)
        
        for (PrivilegeNiveau niveau : niveaux) {
            if (niveau.getStatut().equals("ACTIF") && points >= niveau.getSeuilPoints()) {
                currentNiveau = niveau;
            }
        }
        
        return currentNiveau.getNom();
    }

    @Override
    public String toString() {
        return "PrivilegeNiveau{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", seuilPoints=" + seuilPoints +
                ", pourcentageReduction=" + pourcentageReduction +
                ", description='" + description + '\'' +
                ", couleur='" + couleur + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}