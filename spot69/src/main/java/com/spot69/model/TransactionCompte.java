package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionCompte {
    private int id;
    private int compteClientId;
    private CompteClient compteClient; // Référence
    private int typeTransactionId;
    private TypeTransaction typeTransaction; // Référence
    private BigDecimal montant;
    private BigDecimal soldeAvant;
    private BigDecimal soldeApres;
    private int caissiereId;
    private Utilisateur caissiere; // Référence
    private Integer commandeId;
    private Integer reservationEvenementId;
    private Commande commande; // Référence optionnelle
    private String notes;
    private Timestamp dateTransaction;

    // Constructeurs
    public TransactionCompte() {}

    public TransactionCompte(int id, int compteClientId, int typeTransactionId, 
                           BigDecimal montant, BigDecimal soldeAvant, BigDecimal soldeApres,
                           int caissiereId, Integer commandeId, String notes, Timestamp dateTransaction) {
        this.id = id;
        this.compteClientId = compteClientId;
        this.typeTransactionId = typeTransactionId;
        this.montant = montant;
        this.soldeAvant = soldeAvant;
        this.soldeApres = soldeApres;
        this.caissiereId = caissiereId;
        this.commandeId = commandeId;
        this.notes = notes;
        this.dateTransaction = dateTransaction;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompteClientId() { return compteClientId; }
    public void setCompteClientId(int compteClientId) { this.compteClientId = compteClientId; }

    public CompteClient getCompteClient() { return compteClient; }
    public void setCompteClient(CompteClient compteClient) { 
        this.compteClient = compteClient;
        if (compteClient != null) {
            this.compteClientId = compteClient.getId();
        }
    }

    public int getTypeTransactionId() { return typeTransactionId; }
    public void setTypeTransactionId(int typeTransactionId) { this.typeTransactionId = typeTransactionId; }

    public TypeTransaction getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(TypeTransaction typeTransaction) { 
        this.typeTransaction = typeTransaction;
        if (typeTransaction != null) {
            this.typeTransactionId = typeTransaction.getId();
        }
    }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public BigDecimal getSoldeAvant() { return soldeAvant; }
    public void setSoldeAvant(BigDecimal soldeAvant) { this.soldeAvant = soldeAvant; }

    public BigDecimal getSoldeApres() { return soldeApres; }
    public void setSoldeApres(BigDecimal soldeApres) { this.soldeApres = soldeApres; }

    public int getCaissiereId() { return caissiereId; }
    public void setCaissiereId(int caissiereId) { this.caissiereId = caissiereId; }

    public Utilisateur getCaissiere() { return caissiere; }
    public void setCaissiere(Utilisateur caissiere) { 
        this.caissiere = caissiere;
        if (caissiere != null) {
            this.caissiereId = caissiere.getId();
        }
    }

    public Integer getCommandeId() { return commandeId; }
    public void setCommandeId(Integer commandeId) { this.commandeId = commandeId; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { 
        this.commande = commande;
        if (commande != null) {
            this.commandeId = commande.getId();
        }
    }
    public Integer getReservationEvenementId() {
        return reservationEvenementId;
    }
    
    public void setReservationEvenementId(Integer reservationEvenementId) {
        this.reservationEvenementId = reservationEvenementId;
    }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(Timestamp dateTransaction) { this.dateTransaction = dateTransaction; }

    @Override
    public String toString() {
        return "TransactionCompte{" +
                "id=" + id +
                ", compteClientId=" + compteClientId +
                ", typeTransactionId=" + typeTransactionId +
                ", montant=" + montant +
                ", soldeAvant=" + soldeAvant +
                ", soldeApres=" + soldeApres +
                ", caissiereId=" + caissiereId +
                ", commandeId=" + commandeId +
                ", notes='" + notes + '\'' +
                ", dateTransaction=" + dateTransaction +
                '}';
    }
}