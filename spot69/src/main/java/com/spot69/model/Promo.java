package com.spot69.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Promo {
    private int id;
    private String titre;
    private String sousTitre;
    private String description;
    private String typeContenu; // 'video' ou 'image'
    private String cheminMedia;
    private List<String> couleursGradient;
    private String texteBouton;
    private String routeCible;
    private String dureeVideo; // Pour les vidéos uniquement
    private String vues; // Format: "2.5K vues"
    private int ordreAffichage;
    private String statut; // 'actif', 'inactif', 'supprime'
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int utilisateurId;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private Utilisateur utilisateur; // Pour la jointure
    
    // Constructeurs
    public Promo() {
        this.ordreAffichage = 0;
        this.statut = "actif";
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
    
    public Promo(int id, String titre, String sousTitre, String description, 
                 String typeContenu, String cheminMedia, List<String> couleursGradient,
                 String texteBouton, String routeCible, String dureeVideo, String vues,
                 int ordreAffichage, String statut, LocalDateTime dateDebut, 
                 LocalDateTime dateFin, int utilisateurId) {
        this.id = id;
        this.titre = titre;
        this.sousTitre = sousTitre;
        this.description = description;
        this.typeContenu = typeContenu;
        this.cheminMedia = cheminMedia;
        this.couleursGradient = couleursGradient;
        this.texteBouton = texteBouton;
        this.routeCible = routeCible;
        this.dureeVideo = dureeVideo;
        this.vues = vues;
        this.ordreAffichage = ordreAffichage;
        this.statut = statut;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.utilisateurId = utilisateurId;
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
    
    // Méthode utilitaire pour convertir la chaîne de gradient en liste
    public static List<String> parseGradient(String gradientString) {
        if (gradientString == null || gradientString.trim().isEmpty()) {
            return Arrays.asList("#8B5CF6", "#EC4899", "#3B82F6"); // Couleurs par défaut
        }
        return Arrays.asList(gradientString.split(","));
    }
    
    // Méthode utilitaire pour convertir la liste en chaîne pour la base de données
    public static String gradientToString(List<String> gradientList) {
        if (gradientList == null || gradientList.isEmpty()) {
            return "#8B5CF6,#EC4899,#3B82F6";
        }
        return String.join(",", gradientList);
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getSousTitre() { return sousTitre; }
    public void setSousTitre(String sousTitre) { this.sousTitre = sousTitre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTypeContenu() { return typeContenu; }
    public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }
    
    public String getCheminMedia() { return cheminMedia; }
    public void setCheminMedia(String cheminMedia) { this.cheminMedia = cheminMedia; }
    
    public List<String> getCouleursGradient() { return couleursGradient; }
    public void setCouleursGradient(List<String> couleursGradient) { 
        this.couleursGradient = couleursGradient; 
    }
    
    // Pour la sérialisation/désérialisation avec la base de données
    public String getCouleursGradientString() {
        return gradientToString(this.couleursGradient);
    }
    
    public void setCouleursGradientFromString(String gradientString) {
        this.couleursGradient = parseGradient(gradientString);
    }
    
    public String getTexteBouton() { return texteBouton; }
    public void setTexteBouton(String texteBouton) { this.texteBouton = texteBouton; }
    
    public String getRouteCible() { return routeCible; }
    public void setRouteCible(String routeCible) { this.routeCible = routeCible; }
    
    public String getDureeVideo() { return dureeVideo; }
    public void setDureeVideo(String dureeVideo) { this.dureeVideo = dureeVideo; }
    
    public String getVues() { return vues; }
    public void setVues(String vues) { this.vues = vues; }
    
    public int getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(int ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    
    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    @Override
    public String toString() {
        return "Promo{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", typeContenu='" + typeContenu + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}