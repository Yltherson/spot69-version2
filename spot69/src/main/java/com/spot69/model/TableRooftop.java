package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TableRooftop {

    private int id;
    private int numeroTable;
    private String etatActuel; // RESERVE ou DISPONIBLE
    private String statut;      // DELETED ou VISIBLE
    private BigDecimal plafond;
    private Integer deletedBy;  // peut être null
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private int qteCommandes;        // nombre de commandes
    private BigDecimal montantTotal; // montant total des commandes

    // Constructeur vide
    public TableRooftop() {}

    // Constructeur avec tous les champs
    public TableRooftop(int id, int numeroTable, String etatActuel, String statut,
                        BigDecimal plafond, Integer deletedBy,
                        Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.numeroTable = numeroTable;
        this.etatActuel = etatActuel;
        this.statut = statut;
        this.plafond = plafond;
        this.deletedBy = deletedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public int getQteCommandes() {
        return qteCommandes;
    }

    public void setQteCommandes(int qteCommandes) {
        this.qteCommandes = qteCommandes;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroTable() {
        return numeroTable;
    }

    public void setNumeroTable(int numeroTable) {
        this.numeroTable = numeroTable;
    }

    public String getEtatActuel() {
        return etatActuel;
    }

    public void setEtatActuel(String etatActuel) {
        this.etatActuel = etatActuel;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    public void setPlafond(BigDecimal plafond) {
        this.plafond = plafond;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(Timestamp timestamp) {
        this.createdAt = timestamp;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TableRooftop{" +
                "id=" + id +
                ", numeroTable=" + numeroTable +
                ", etatActuel='" + etatActuel + '\'' +
                ", statut='" + statut + '\'' +
                ", plafond=" + plafond +
                ", deletedBy=" + deletedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
