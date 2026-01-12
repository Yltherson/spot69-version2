package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CaisseCaissiere {
    private int id;
    private int caissiereId;
    private Utilisateur caissiere; // Référence
    private Timestamp ouverture;
    private Timestamp fermeture;
    private BigDecimal soldeInitial;
    private BigDecimal soldeFinal;
    private String statut; // OUVERTE, FERMEE
    private Boolean shot; 
    private BigDecimal montantShot;
    private BigDecimal montantDonne;

    // Constructeurs
    public CaisseCaissiere() {
        this.statut = "OUVERTE";
        this.soldeInitial = BigDecimal.ZERO;
    }

    public CaisseCaissiere(int id, int caissiereId, Timestamp ouverture, Timestamp fermeture,
                         BigDecimal soldeInitial, BigDecimal soldeFinal, String statut) {
        this.id = id;
        this.caissiereId = caissiereId;
        this.ouverture = ouverture;
        this.fermeture = fermeture;
        this.soldeInitial = soldeInitial;
        this.soldeFinal = soldeFinal;
        this.statut = statut;
    }
    
  
    

    public Boolean isShot() { return shot; }
    public void setShot(Boolean shot) { this.shot = shot; }
    
    public BigDecimal getMontantShot() { return montantShot; }
    public void setMontantShot(BigDecimal montantShot) { this.montantShot = montantShot; }


    public BigDecimal getMontantDonne() { return montantDonne; }
    public void setMontantDonne(BigDecimal montantDonne) { this.montantDonne = montantDonne; }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCaissiereId() { return caissiereId; }
    public void setCaissiereId(int caissiereId) { this.caissiereId = caissiereId; }

    public Utilisateur getCaissiere() { return caissiere; }
    public void setCaissiere(Utilisateur caissiere) { 
        this.caissiere = caissiere;
        if (caissiere != null) {
            this.caissiereId = caissiere.getId();
        }
    }

    public Timestamp getOuverture() { return ouverture; }
    public void setOuverture(Timestamp ouverture) { this.ouverture = ouverture; }

    public Timestamp getFermeture() { return fermeture; }
    public void setFermeture(Timestamp fermeture) { this.fermeture = fermeture; }

    public BigDecimal getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(BigDecimal soldeInitial) { this.soldeInitial = soldeInitial; }

    public BigDecimal getSoldeFinal() { return soldeFinal; }
    public void setSoldeFinal(BigDecimal soldeFinal) { this.soldeFinal = soldeFinal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "CaisseCaissiere{" +
                "id=" + id +
                ", caissiereId=" + caissiereId +
                ", ouverture=" + ouverture +
                ", fermeture=" + fermeture +
                ", soldeInitial=" + soldeInitial +
                ", soldeFinal=" + soldeFinal +
                ", statut='" + statut + '\'' +
                '}';
    }
}