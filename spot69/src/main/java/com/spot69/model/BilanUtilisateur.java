package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class BilanUtilisateur {
    private int totalCommandes;
    private BigDecimal totalMontant;
    private BigDecimal totalCompteClient;
    private BigDecimal pourcentage;           // pourcentage à calculer plus tard
    private BigDecimal montantPourcentage;    // montantPourcentage à calculer plus tard

    private Timestamp dateDebut;              // ajout dateDebut
    private Timestamp dateFin;                // ajout dateFin

    private Map<String, Integer> qteParStatut = new HashMap<>();
    private Map<String, BigDecimal> montantParStatut = new HashMap<>(); // <-- nouvelle map

    // Getters et setters
    public int getTotalCommandes() { return totalCommandes; }
    public void setTotalCommandes(int totalCommandes) { this.totalCommandes = totalCommandes; }
    

    public BigDecimal getTotalCompteClient() { return totalCompteClient; }
    public void setTotalCompteClient(BigDecimal totalCompteClient) { this.totalCompteClient = totalCompteClient; }

    public BigDecimal getTotalMontant() { return totalMontant; }
    public void setTotalMontant(BigDecimal totalMontant) { this.totalMontant = totalMontant; }

    public BigDecimal getPourcentage() { return pourcentage; }
    public void setPourcentage(BigDecimal pourcentage) { this.pourcentage = pourcentage; }

    public BigDecimal getMontantPourcentage() { return montantPourcentage; }
    public void setMontantPourcentage(BigDecimal montantPourcentage) { this.montantPourcentage = montantPourcentage; }

    public Timestamp getDateDebut() { return dateDebut; }
    public void setDateDebut(Timestamp dateDebut) { this.dateDebut = dateDebut; }

    public Timestamp getDateFin() { return dateFin; }
    public void setDateFin(Timestamp dateFin) { this.dateFin = dateFin; }

    public Map<String, Integer> getQteParStatut() { return qteParStatut; }
    public void setQteParStatut(Map<String, Integer> qteParStatut) { this.qteParStatut = qteParStatut; }

    public Map<String, BigDecimal> getMontantParStatut() { return montantParStatut; } // <-- getter
    public void setMontantParStatut(Map<String, BigDecimal> montantParStatut) { this.montantParStatut = montantParStatut; } // <-- setter
}
