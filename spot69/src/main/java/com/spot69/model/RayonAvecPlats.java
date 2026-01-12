package com.spot69.model;

import java.util.List;

//Dans votre package model
public class RayonAvecPlats {
 private Rayon rayon;
 private List<Plat> plats;
 
 // Constructeurs
 public RayonAvecPlats() {}
 
 public RayonAvecPlats(Rayon rayon, List<Plat> plats) {
     this.rayon = rayon;
     this.plats = plats;
 }
 
 // Getters & Setters
 public Rayon getRayon() { return rayon; }
 public void setRayon(Rayon rayon) { this.rayon = rayon; }
 
 public List<Plat> getPlats() { return plats; }
 public void setPlats(List<Plat> plats) { this.plats = plats; }
}
