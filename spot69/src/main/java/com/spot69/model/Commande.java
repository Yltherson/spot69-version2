package com.spot69.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Commande {
    private int id;
    private String numeroCommande;

    private Utilisateur utilisateur;

    private String whoIsOrdering;
    private Integer clientId;
    private Integer cashedBy;
    private Timestamp dateCommande;
    private String statutCommande; // EN_ATTENTE, EN_PREPARATION, PRETE, etc.
    private BigDecimal montantTotal;
    private String modePaiement; // CASH, VIREMENT, MONCASH, NATCASH
    private String statutPaiement; // NON_PAYE, PARTIEL, PAYE
    private BigDecimal montantPaye;
    private int utilisateurId;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer deletedBy;
    private String statut; // VISIBLE, DELETED
    private List<CommandeDetail> details;
    private Utilisateur client;
    private Utilisateur caissier;
    private Credit credit;
    private Utilisateur staff;
 // Nouveau champ TableRooftop
    private TableRooftop tableRooftop;

    private boolean isCredit; // true si c'est un crédit, false sinon

 // Getter et Setter
 public boolean isCredit() {
     return isCredit;
 }

 public void setIsCredit(boolean isCredit) {
     this.isCredit = isCredit;
 }

    // Getters et Setters
    public List<CommandeDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CommandeDetail> details) {
        this.details = details;
    }
    
    public Utilisateur getCaissier() {
        return caissier;
    }

    public void setCaissier(Utilisateur caissier) {
        this.caissier = caissier;
    }
    
    
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public Credit getCredit() {
        return credit;
    }
    
    public void setCredit(Credit credit) {
        this.credit = credit;
    }
    
    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public int getCashedBy() {
        return cashedBy;
    }
    public void setCashedBy(int cashedBy) {
        this.cashedBy = cashedBy;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroCommande() {
        return numeroCommande;
    }
    public void setNumeroCommande(String numeroCommande) {
        this.numeroCommande = numeroCommande;
    }
    
    public String getWhoIsOrdering() {
        return whoIsOrdering;
    }
    public void setWhoIsOrdering(String whoIsOrdering) {
        this.whoIsOrdering = whoIsOrdering;
    }

    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Timestamp getDateCommande() {
        return dateCommande;
    }
    public void setDateCommande(Timestamp dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatutCommande() {
        return statutCommande;
    }
    public void setStatutCommande(String statutCommande) {
        this.statutCommande = statutCommande;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public String getModePaiement() {
        return modePaiement;
    }
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }
    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }
    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }
    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
 // Getter et Setter pour TableRooftop
    public TableRooftop getTableRooftop() {
        return tableRooftop;
    }

    public void setTableRooftop(TableRooftop tableRooftop) {
        this.tableRooftop = tableRooftop;
    }
}
