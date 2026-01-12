package com.spot69.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ReservationEvenement {
    private int id;
    private int evenementId;
    private int typeTableId; // Nouveau
    private int utilisateurId;
    private int quantiteTables; // Remplacé QUANTITE_TICKETS
    private int capaciteTotale; // Nouveau
    private BigDecimal montantTotal;
    private String statut;
    private String moyenPaiement;
    private String nomPersonne;
    private String numeroTransaction;
    private String numeroTransfert;
    private LocalDateTime dateReservation;
    private LocalDateTime dateValidation;
    private Integer validePar;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relations
    private Evenement evenement;
    private Utilisateur utilisateur;
    private TypeTableEvenement typeTable; // Nouveau

    // Constructeurs
    public ReservationEvenement() {}
    
    public ReservationEvenement(int evenementId, int typeTableId, int utilisateurId, 
                               int quantiteTables, int capaciteTotale, BigDecimal montantTotal, 
                               String moyenPaiement) {
        this.evenementId = evenementId;
        this.typeTableId = typeTableId;
        this.utilisateurId = utilisateurId;
        this.quantiteTables = quantiteTables;
        this.capaciteTotale = capaciteTotale;
        this.montantTotal = montantTotal;
        this.moyenPaiement = moyenPaiement;
        this.statut = "EN_ATTENTE";
        this.dateReservation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getEvenementId() { return evenementId; }
    public void setEvenementId(int evenementId) { this.evenementId = evenementId; }
    
    public int getTypeTableId() { return typeTableId; } // Nouveau
    public void setTypeTableId(int typeTableId) { this.typeTableId = typeTableId; } // Nouveau
    
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    
    public int getQuantiteTables() { return quantiteTables; } // Remplacé getQuantiteTickets
    public void setQuantiteTables(int quantiteTables) { this.quantiteTables = quantiteTables; }
    
    public int getCapaciteTotale() { return capaciteTotale; } // Nouveau
    public void setCapaciteTotale(int capaciteTotale) { this.capaciteTotale = capaciteTotale; } // Nouveau
    
    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getMoyenPaiement() { return moyenPaiement; }
    public void setMoyenPaiement(String moyenPaiement) { this.moyenPaiement = moyenPaiement; }
    
    public String getNomPersonne() { return nomPersonne; }
    public void setNomPersonne(String nomPersonne) { this.nomPersonne = nomPersonne; }
    
    public String getNumeroTransaction() { return numeroTransaction; }
    public void setNumeroTransaction(String numeroTransaction) { this.numeroTransaction = numeroTransaction; }
    
    public String getNumeroTransfert() { return numeroTransfert; }
    public void setNumeroTransfert(String numeroTransfert) { this.numeroTransfert = numeroTransfert; }
    
    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }
    
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
    
    public Integer getValidePar() { return validePar; }
    public void setValidePar(Integer validePar) { this.validePar = validePar; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    
    public TypeTableEvenement getTypeTable() { return typeTable; } // Nouveau
    public void setTypeTable(TypeTableEvenement typeTable) { this.typeTable = typeTable; } // Nouveau
    
    // Méthodes utilitaires pour compatibilité JSP
    public Date getDateReservationAsDate() {
        return dateReservation != null ? 
            Date.from(dateReservation.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    public Date getDateValidationAsDate() {
        return dateValidation != null ? 
            Date.from(dateValidation.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    
    // Méthodes pour la compatibilité (si nécessaire)
    @Deprecated
    public int getQuantiteTickets() {
        return quantiteTables; // Pour compatibilité avec l'ancien code
    }
    
    @Deprecated
    public void setQuantiteTickets(int quantiteTickets) {
        this.quantiteTables = quantiteTickets; // Pour compatibilité
    }
    
    // Calculer le prix unitaire
    public BigDecimal getPrixUnitaire() {
        if (quantiteTables > 0 && montantTotal != null) {
            return montantTotal.divide(new BigDecimal(quantiteTables));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "ReservationEvenement{id=" + id + ", evenementId=" + evenementId + 
               ", typeTableId=" + typeTableId + ", quantiteTables=" + quantiteTables + 
               ", capaciteTotale=" + capaciteTotale + "}";
    }
}