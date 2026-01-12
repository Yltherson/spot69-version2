package com.spot69.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Dans votre package model
public class RayonHierarchique {
 private Rayon rayon;
 private List<MenuCategorie> categories;
 private Map<Integer, List<MenuCategorie>> sousCategoriesParCategorie; // Map<categoryId, sousCategories>
 private Map<Integer, List<Plat>> platsParSousCategorie; // Map<sousCategoryId, plats>
 
 // Constructeurs
 public RayonHierarchique() {}
 
 public RayonHierarchique(Rayon rayon, List<MenuCategorie> categories) {
     this.rayon = rayon;
     this.categories = categories;
     this.sousCategoriesParCategorie = new HashMap<>();
     this.platsParSousCategorie = new HashMap<>();
 }
 
 // Getters & Setters
 public Rayon getRayon() { return rayon; }
 public void setRayon(Rayon rayon) { this.rayon = rayon; }
 
 public List<MenuCategorie> getCategories() { return categories; }
 public void setCategories(List<MenuCategorie> categories) { this.categories = categories; }
 
 public Map<Integer, List<MenuCategorie>> getSousCategoriesParCategorie() { return sousCategoriesParCategorie; }
 public void setSousCategoriesParCategorie(Map<Integer, List<MenuCategorie>> sousCategoriesParCategorie) { 
     this.sousCategoriesParCategorie = sousCategoriesParCategorie; 
 }
 
 public Map<Integer, List<Plat>> getPlatsParSousCategorie() { return platsParSousCategorie; }
 public void setPlatsParSousCategorie(Map<Integer, List<Plat>> platsParSousCategorie) { 
     this.platsParSousCategorie = platsParSousCategorie; 
 }
}
