package com.spot69.model;

import java.sql.Timestamp;

public class Role {
    private int id;
    private String roleName;
    private String droits; // Ex: "GESTION_COMPTE,SUPPRESSION_COMPTE"
    private String statut; // VISIBLE ou DELETED
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Role() {}

    public Role(int id, String roleName, String droits, String statut, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.roleName = roleName;
        this.droits = droits;
        this.statut = statut;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDroits() {
        return droits;
    }

    public void setDroits(String droits) {
        this.droits = droits;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
}
