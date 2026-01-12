package com.spot69.model;

import java.sql.Timestamp;

public class Caisse {
    private int id;
    private int id_caisse;
    private Timestamp open_at;
    private Timestamp close_at;
    private Timestamp create_at;
    private Timestamp update_at;

    // Constructeur
    public Caisse(int id, int id_caisse, Timestamp open_at, Timestamp close_at, Timestamp create_at, Timestamp update_at) {
        this.id = id;
        this.id_caisse = id_caisse;
        this.open_at = open_at;
        this.close_at = close_at;
        this.create_at = create_at;
        this.update_at = update_at;
    }

    // Getter et Setter pour id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter et Setter pour id_caisse
    public int getId_caisse() {
        return id_caisse;
    }

    public void setId_caisse(int id_caisse) {
        this.id_caisse = id_caisse;
    }

    // Getter et Setter pour open_at
    public Timestamp getOpen_at() {
        return open_at;
    }

    public void setOpen_at(Timestamp open_at) {
        this.open_at = open_at;
    }

    // Getter et Setter pour close_at
    public Timestamp getClose_at() {
        return close_at;
    }

    public void setClose_at(Timestamp close_at) {
        this.close_at = close_at;
    }

    // Getter et Setter pour create_at
    public Timestamp getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Timestamp create_at) {
        this.create_at = create_at;
    }

    // Getter et Setter pour update_at
    public Timestamp getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(Timestamp update_at) {
        this.update_at = update_at;
    }
    
}
