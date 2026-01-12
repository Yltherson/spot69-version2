package com.spot69.model;

import java.math.BigDecimal;

public class ReservationTable {
    private int id;
    private int reservationEvenementId;
    private int typeTableId;
    private int quantite;
    private BigDecimal montant;
    
    // Relations
    private TypeTableEvenement typeTable;
    
    // Constructeurs
    public ReservationTable() {}
    
    public ReservationTable(int reservationEvenementId, int typeTableId, int quantite, BigDecimal montant) {
        this.reservationEvenementId = reservationEvenementId;
        this.typeTableId = typeTableId;
        this.quantite = quantite;
        this.montant = montant;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getReservationEvenementId() { return reservationEvenementId; }
    public void setReservationEvenementId(int reservationEvenementId) { 
        this.reservationEvenementId = reservationEvenementId; 
    }
    
    public int getTypeTableId() { return typeTableId; }
    public void setTypeTableId(int typeTableId) { this.typeTableId = typeTableId; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    
    public TypeTableEvenement getTypeTable() { return typeTable; }
    public void setTypeTable(TypeTableEvenement typeTable) { this.typeTable = typeTable; }
    
    @Override
    public String toString() {
        return "ReservationTable{id=" + id + ", typeTableId=" + typeTableId + 
               ", quantite=" + quantite + ", montant=" + montant + "}";
    }
}