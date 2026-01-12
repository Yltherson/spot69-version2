package com.spot69.model;

public class TypeTransaction {
    private int id;
    private String code;
    private String libelle;

    // Codes constants
    public static final String DEPOT = "DEPOT";
    public static final String DEPENSE = "DEPENSE";
    public static final String RETRAIT = "RETRAIT";
    public static final String REMBOURSEMENT = "REMBOURSEMENT";
    public static final String AJUSTEMENT = "AJUSTEMENT";

    // Constructeurs
    public TypeTransaction() {}

    public TypeTransaction(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    @Override
    public String toString() {
        return "TypeTransaction{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}