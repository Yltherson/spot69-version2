package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class Chambre {
    private int id;
    private String nomChambre;
    private String descriptionChambre;
    private int capacite;
    private String installations; // Stocké comme JSON ou chaîne séparée par des virgules
    private BigDecimal prixMoment;    // Prix par créneau de 2h
    private BigDecimal prixNuit;      // Prix pour une nuit (20h-7h)
    private BigDecimal prixJour;      // Prix pour une journée (8h-19h)
    private BigDecimal prixSejour;    // Prix par nuit pour un séjour
    private boolean disponible;
    private String media; // URLs des images séparées par des virgules
    private int utilisateurId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer deletedBy;
    private String statut;
    private List<String> momentsReserves;     // Liste des créneaux de moments réservés
    private List<String> nuitsReserves;       // Liste des nuits réservées
    private List<String> joursReserves;       // Liste des jours réservés
    private List<String> sejoursReserves;     // Liste des séjours réservés

    // Constructeurs
    public Chambre() {}

    public Chambre(int id, String nomChambre, String descriptionChambre, int capacite, 
                  String installations, BigDecimal prixMoment, BigDecimal prixNuit, 
                  BigDecimal prixJour, BigDecimal prixSejour, boolean disponible, String media) {
        this.id = id;
        this.nomChambre = nomChambre;
        this.descriptionChambre = descriptionChambre;
        this.capacite = capacite;
        this.installations = installations;
        this.prixMoment = prixMoment;
        this.prixNuit = prixNuit;
        this.prixJour = prixJour;
        this.prixSejour = prixSejour;
        this.disponible = disponible;
        this.media = media;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public List<String> getMomentsReserves() { return momentsReserves; }
    public void setMomentsReserves(List<String> momentsReserves) { this.momentsReserves = momentsReserves; }
    
    public List<String> getNuitsReserves() { return nuitsReserves; }
    public void setNuitsReserves(List<String> nuitsReserves) { this.nuitsReserves = nuitsReserves; }
    
    public List<String> getJoursReserves() { return joursReserves; }
    public void setJoursReserves(List<String> joursReserves) { this.joursReserves = joursReserves; }
    
    public List<String> getSejoursReserves() { return sejoursReserves; }
    public void setSejoursReserves(List<String> sejoursReserves) { this.sejoursReserves = sejoursReserves; }


    public String getNomChambre() { return nomChambre; }
    public void setNomChambre(String nomChambre) { this.nomChambre = nomChambre; }

    public String getDescriptionChambre() { return descriptionChambre; }
    public void setDescriptionChambre(String descriptionChambre) { this.descriptionChambre = descriptionChambre; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getInstallations() { return installations; }
    public void setInstallations(String installations) { this.installations = installations; }
    
    // Méthode utilitaire pour obtenir les installations comme liste
    public List<String> getInstallationsList() {
        if (installations == null || installations.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(installations.split(","));
    }
    
    // Méthode utilitaire pour définir les installations depuis une liste
    public void setInstallationsList(List<String> installationsList) {
        if (installationsList == null || installationsList.isEmpty()) {
            this.installations = "";
        } else {
            this.installations = String.join(",", installationsList);
        }
    }

    public BigDecimal getPrixMoment() { return prixMoment; }
    public void setPrixMoment(BigDecimal prixMoment) { this.prixMoment = prixMoment; }

    public BigDecimal getPrixNuit() { return prixNuit; }
    public void setPrixNuit(BigDecimal prixNuit) { this.prixNuit = prixNuit; }

    public BigDecimal getPrixJour() { return prixJour; }
    public void setPrixJour(BigDecimal prixJour) { this.prixJour = prixJour; }

    public BigDecimal getPrixSejour() { return prixSejour; }
    public void setPrixSejour(BigDecimal prixSejour) { this.prixSejour = prixSejour; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }
    
    // Méthode utilitaire pour obtenir les médias comme liste
    public List<String> getMediaList() {
        if (media == null || media.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(media.split(","));
    }
    
    // Méthode utilitaire pour définir les médias depuis une liste
    public void setMediaList(List<String> mediaList) {
        if (mediaList == null || mediaList.isEmpty()) {
            this.media = "";
        } else {
            this.media = String.join(",", mediaList);
        }
    }

    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Chambre{" +
                "id=" + id +
                ", nomChambre='" + nomChambre + '\'' +
                ", descriptionChambre='" + descriptionChambre + '\'' +
                ", capacite=" + capacite +
                ", installations='" + installations + '\'' +
                ", prixMoment=" + prixMoment +
                ", prixNuit=" + prixNuit +
                ", prixJour=" + prixJour +
                ", prixSejour=" + prixSejour +
                ", disponible=" + disponible +
                ", media='" + media + '\'' +
                ", utilisateurId=" + utilisateurId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", statut='" + statut + '\'' +
                '}';
    }
}