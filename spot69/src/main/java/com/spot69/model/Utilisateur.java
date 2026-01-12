//package com.spot69.model;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//
//public class Utilisateur {
//    private int id;
//    private int plafond;
//    private String nom;
//    private String prenom;
//    private String email;
//    private String login;
//    private String motDePasse;
//    private Role role; // objet Role au lieu de String
//    private String statut; // visible, deleted
//    private Timestamp creationDate;
//    private Timestamp updateDate;
//    private BigDecimal pourcentage; // nouvelle colonne
//    private String telephone; // nouveau champ
//    private String adresse; // nouveau champ
//    private Integer point; // nouveau champ (peut être null)
//    private String privilege; // nouveau champ
//
//    // Constructeur vide
//    public Utilisateur() {
//        this.statut = "visible"; // valeur par défaut
//        this.privilege = "bronze"; // valeur par défaut
//        this.point = 0; // valeur par défaut
//    }
//
//    // Constructeur complet
//    public Utilisateur(int id, String nom, String prenom, String email, String login,
//                       String motDePasse, Role role, String statut,
//                       Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage) {
//        this.id = id;
//        this.nom = nom;
//        this.prenom = prenom;
//        this.email = email;
//        this.login = login;
//        this.motDePasse = motDePasse;
//        this.role = role;
//        this.statut = statut;
//        this.creationDate = creationDate;
//        this.updateDate = updateDate;
//        this.pourcentage = pourcentage;
//        this.privilege = "bronze";
//        this.point = 0;
//    }
//
//    // Constructeur avec plafond
//    public Utilisateur(int id, String nom, String prenom, String email, String login,
//            Role role, String statut,
//            Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage, int plafond) {
//        this.id = id;
//        this.nom = nom;
//        this.prenom = prenom;
//        this.email = email;
//        this.login = login;
//        this.role = role;
//        this.statut = statut;
//        this.creationDate = creationDate;
//        this.updateDate = updateDate;
//        this.pourcentage = pourcentage;
//        this.plafond = plafond;
//        this.privilege = "bronze";
//        this.point = 0;
//    }
//
//    // Constructeur complet avec tous les champs
//    public Utilisateur(int id, String nom, String prenom, String email, String login,
//                       String motDePasse, Role role, String statut,
//                       Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage,
//                       int plafond, String telephone, String adresse, Integer point, String privilege) {
//        this.id = id;
//        this.nom = nom;
//        this.prenom = prenom;
//        this.email = email;
//        this.login = login;
//        this.motDePasse = motDePasse;
//        this.role = role;
//        this.statut = statut;
//        this.creationDate = creationDate;
//        this.updateDate = updateDate;
//        this.pourcentage = pourcentage;
//        this.plafond = plafond;
//        this.telephone = telephone;
//        this.adresse = adresse;
//        this.point = point;
//        this.privilege = privilege;
//    }
//
//    // Constructeur sans motDePasse mais avec tous les autres champs
//    public Utilisateur(int id, String nom, String prenom, String email, String login,
//                       Role role, String statut,
//                       Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage,
//                       int plafond, String telephone, String adresse, Integer point, String privilege) {
//        this.id = id;
//        this.nom = nom;
//        this.prenom = prenom;
//        this.email = email;
//        this.login = login;
//        this.role = role;
//        this.statut = statut;
//        this.creationDate = creationDate;
//        this.updateDate = updateDate;
//        this.pourcentage = pourcentage;
//        this.plafond = plafond;
//        this.telephone = telephone;
//        this.adresse = adresse;
//        this.point = point;
//        this.privilege = privilege;
//    }
//
//    // Getters & Setters
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//    
//    public int getPlafond() {
//        return plafond;
//    }
//
//    public void setPlafond(int plafond) {
//        this.plafond = plafond;
//    }
//
//    public String getNom() {
//        return nom;
//    }
//
//    public void setNom(String nom) {
//        this.nom = nom;
//    }
//
//    public String getPrenom() {
//        return prenom;
//    }
//
//    public void setPrenom(String prenom) {
//        this.prenom = prenom;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getLogin() {
//        return login;
//    }
//
//    public void setLogin(String login) {
//        this.login = login;
//    }
//
//    public String getMotDePasse() {
//        return motDePasse;
//    }
//
//    public void setMotDePasse(String motDePasse) {
//        this.motDePasse = motDePasse;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public String getStatut() {
//        return statut;
//    }
//
//    public void setStatut(String statut) {
//        this.statut = statut;
//    }
//
//    public Timestamp getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Timestamp creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    public Timestamp getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(Timestamp updateDate) {
//        this.updateDate = updateDate;
//    }
//
//    public BigDecimal getPourcentage() {
//        return pourcentage;
//    }
//
//    public void setPourcentage(BigDecimal pourcentage) {
//        this.pourcentage = pourcentage;
//    }
//
//    public String getTelephone() {
//        return telephone;
//    }
//
//    public void setTelephone(String telephone) {
//        this.telephone = telephone;
//    }
//
//    public String getAdresse() {
//        return adresse;
//    }
//
//    public void setAdresse(String adresse) {
//        this.adresse = adresse;
//    }
//
//    public Integer getPoint() {
//        return point;
//    }
//
//    public void setPoint(Integer point) {
//        this.point = point;
//    }
//
//    public String getPrivilege() {
//        return privilege;
//    }
//
//    public void setPrivilege(String privilege) {
//        this.privilege = privilege;
//    }
//
//    // Méthode utilitaire pour obtenir le nom complet
//    public String getNomComplet() {
//        return prenom + " " + nom;
//    }
//
//    @Override
//    public String toString() {
//        return "Utilisateur{" +
//                "id=" + id +
//                ", nom='" + nom + '\'' +
//                ", prenom='" + prenom + '\'' +
//                ", email='" + email + '\'' +
//                ", login='" + login + '\'' +
//                ", role=" + (role != null ? role.getRoleName() : "null") +
//                ", telephone='" + telephone + '\'' +
//                ", adresse='" + adresse + '\'' +
//                ", point=" + point +
//                ", privilege='" + privilege + '\'' +
//                '}';
//    }
//}

package com.spot69.model;

import java.math.BigDecimal;
import java.sql.Date;  // Changé pour Date au lieu de Timestamp pour la date de naissance
import java.sql.Timestamp;
import java.time.LocalDate;

public class Utilisateur {
    private int id;
    private int plafond;
    private String nom;
    private String prenom;
    private String email;
    private String login;
    private String motDePasse;
    private Role role;
    private String statut;
    private Timestamp creationDate;
    private Timestamp updateDate;

    private BigDecimal pourcentage; // nouvelle colonne
    private String telephone; // nouveau champ
    private String adresse; // nouveau champ
    private Integer point; // nouveau champ (peut être null)
    private String privilege; // nouveau champ
    private LocalDate dateDeNaissance;
    private Date dateNaissance;

    // Constructeur vide
    public Utilisateur() {
        this.statut = "visible"; // valeur par défaut
        this.privilege = "bronze"; // valeur par défaut
        this.point = 0; // valeur par défaut
    }
    
 // Modifiez le constructeur complet existant pour inclure dateDeNaissance
    public Utilisateur(int id, String nom, String prenom, String email, String login,
                       String motDePasse, Role role, String statut,
                       Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage,
                       int plafond, String telephone, String adresse, Integer point, 
                       String privilege, LocalDate dateDeNaissance) { // Ajoutez ce paramètre
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.pourcentage = pourcentage;
        this.plafond = plafond;
        this.telephone = telephone;
        this.adresse = adresse;
        this.point = point;
        this.privilege = privilege;
        this.dateDeNaissance = dateDeNaissance; // Initialisez le nouveau champ
    }

 

    // Constructeur complet avec dateNaissance
    public Utilisateur(int id, String nom, String prenom, String email, String login,
                       String motDePasse, Role role, String statut,
                       Timestamp creationDate, Timestamp updateDate, BigDecimal pourcentage,
                       int plafond, String telephone, String adresse, Integer point, 
                       String privilege, Date dateNaissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
        this.statut = statut;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
        this.pourcentage = pourcentage;
        this.plafond = plafond;
        this.telephone = telephone;
        this.adresse = adresse;
        this.point = point;
        this.privilege = privilege;
        this.dateNaissance = dateNaissance;
    }

    // Getters et Setters pour dateNaissance
    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    // ... autres getters et setters existants
    
    public int getId() {
      return id;
  }

  public void setId(int id) {
      this.id = id;
  }
  
  public int getPlafond() {
      return plafond;
  }

  public void setPlafond(int plafond) {
      this.plafond = plafond;
  }

  public String getNom() {
      return nom;
  }

  public void setNom(String nom) {
      this.nom = nom;
  }

  public String getPrenom() {
      return prenom;
  }

  public void setPrenom(String prenom) {
      this.prenom = prenom;
  }

  public String getEmail() {
      return email;
  }

  public void setEmail(String email) {
      this.email = email;
  }

  public String getLogin() {
      return login;
  }

  public void setLogin(String login) {
      this.login = login;
  }

  public String getMotDePasse() {
      return motDePasse;
  }

  public void setMotDePasse(String motDePasse) {
      this.motDePasse = motDePasse;
  }

  public Role getRole() {
      return role;
  }

  public void setRole(Role role) {
      this.role = role;
  }

  public String getStatut() {
      return statut;
  }

  public void setStatut(String statut) {
      this.statut = statut;
  }

  public Timestamp getCreationDate() {
      return creationDate;
  }

  public void setCreationDate(Timestamp creationDate) {
      this.creationDate = creationDate;
  }

  public Timestamp getUpdateDate() {
      return updateDate;
  }

  public void setUpdateDate(Timestamp updateDate) {
      this.updateDate = updateDate;
  }

  public BigDecimal getPourcentage() {
      return pourcentage;
  }

  public void setPourcentage(BigDecimal pourcentage) {
      this.pourcentage = pourcentage;
  }

  public String getTelephone() {
      return telephone;
  }

  public void setTelephone(String telephone) {
      this.telephone = telephone;
  }

  public String getAdresse() {
      return adresse;
  }

  public void setAdresse(String adresse) {
      this.adresse = adresse;
  }

  public Integer getPoint() {
      return point;
  }

  public void setPoint(Integer point) {
      this.point = point;
  }

  public String getPrivilege() {
      return privilege;
  }
  

  public void setPrivilege(String privilege) {
      this.privilege = privilege;
  }

    // Méthode utilitaire pour obtenir le nom complet
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    // Ajoutez les getters et setters pour dateDeNaissance
    public LocalDate getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(LocalDate dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }


    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", role=" + (role != null ? role.getRoleName() : "null") +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", point=" + point +
                ", privilege='" + privilege + '\'' +
                  ", dateDeNaissance=" + dateDeNaissance + 
                '}';
    }

}