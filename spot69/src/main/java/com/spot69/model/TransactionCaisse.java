package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionCaisse {
    private int id;
    private int caisseId;
    private CaisseCaissiere caisse; // Référence
    private String typeOperation; // DEPOT, RETRAIT, VENTE, AUTRE
    private BigDecimal montant;
    private String modePaiement; // CASH, VIREMENT, MONCASH, NATCASH, CHEQUE
    private Integer clientId;
    private Utilisateur client; // Référence optionnelle
    private Integer commandeId;
    private Commande commande; // Référence optionnelle
    private String notes;
    private Timestamp dateOperation;

    // Constructeurs
    public TransactionCaisse() {}

    public TransactionCaisse(int id, int caisseId, String typeOperation, BigDecimal montant,
                           String modePaiement, Integer clientId, Integer commandeId,
                           String notes, Timestamp dateOperation) {
        this.id = id;
        this.caisseId = caisseId;
        this.typeOperation = typeOperation;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.clientId = clientId;
        this.commandeId = commandeId;
        this.notes = notes;
        this.dateOperation = dateOperation;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCaisseId() { return caisseId; }
    public void setCaisseId(int caisseId) { this.caisseId = caisseId; }

    public CaisseCaissiere getCaisse() { return caisse; }
    public void setCaisse(CaisseCaissiere caisse) { 
        this.caisse = caisse;
        if (caisse != null) {
            this.caisseId = caisse.getId();
        }
    }

    public String getTypeOperation() { return typeOperation; }
    public void setTypeOperation(String typeOperation) { this.typeOperation = typeOperation; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Utilisateur getClient() { return client; }
    public void setClient(Utilisateur client) { 
        this.client = client;
        if (client != null) {
            this.clientId = client.getId();
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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getDateOperation() { return dateOperation; }
    public void setDateOperation(Timestamp dateOperation) { this.dateOperation = dateOperation; }

    @Override
    public String toString() {
        return "TransactionCaisse{" +
                "id=" + id +
                ", caisseId=" + caisseId +
                ", typeOperation='" + typeOperation + '\'' +
                ", montant=" + montant +
                ", modePaiement='" + modePaiement + '\'' +
                ", clientId=" + clientId +
                ", commandeId=" + commandeId +
                ", notes='" + notes + '\'' +
                ", dateOperation=" + dateOperation +
                '}';
    }
}