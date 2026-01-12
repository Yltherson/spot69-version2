package com.spot69.model;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Reservation {
    private String id;
    private String roomId;
    private Date start;
    private Date end;
    private String type; // 'sejour', 'jour', 'moment', 'nuit'
    private String status; // 'confirmé', 'en cours', 'annulé' (français)
    private String title;
    private BigDecimal prixTotal;
    private String arrivalTime;
    private Integer numberOfNights;
    private Integer numberOfSlots;
    private Integer durationHours;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer utilisateurId;
    
    // NOUVEAUX CHAMPS DE PAIEMENT
    private String paymentMethod; // 'SOLDE', 'MONCASH', 'NATCASH'
    private String payerName;
    private String payerPhone;
    private String transactionId;
    private String paymentNote;
    private String paymentStatus; // 'pending', 'completed', 'failed', 'refunded'

    // Constructeurs
    public Reservation() {}

    public Reservation(String id, String roomId, Date start, Date end, String type, 
                      String status, String title, BigDecimal prixTotal, Integer utilisateurId) {
        this.id = id;
        this.roomId = roomId;
        this.start = start;
        this.end = end;
        this.type = type;
        this.status = status;
        this.title = title;
        this.prixTotal = prixTotal;
        this.utilisateurId = utilisateurId;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public Date getStart() { return start; }
    public void setStart(Date start) { this.start = start; }

    public Date getEnd() { return end; }
    public void setEnd(Date end) { this.end = end; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { 
        // Assurer la cohérence des valeurs
        if (status != null) {
            switch(status.toLowerCase()) {
                case "confirmed":
                case "confirmé":
                    return "confirmé";
                case "pending":
                case "en cours":
                    return "en cours";
                case "cancelled":
                case "annulé":
                    return "annulé";
            }
        }
        return status; 
    }
    
    public void setStatus(String status) { 
        // Normaliser les valeurs
        if (status != null) {
            switch(status.toLowerCase()) {
                case "confirmed":
                case "confirmé":
                    this.status = "confirmé";
                    break;
                case "pending":
                case "en cours":
                    this.status = "en cours";
                    break;
                case "cancelled":
                case "annulé":
                    this.status = "annulé";
                    break;
                default:
                    this.status = status;
            }
        } else {
            this.status = "en cours"; // Valeur par défaut
        }
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getPrixTotal() { return prixTotal; }
    public void setPrixTotal(BigDecimal prixTotal) { this.prixTotal = prixTotal; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public Integer getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(Integer numberOfNights) { this.numberOfNights = numberOfNights; }

    public Integer getNumberOfSlots() { return numberOfSlots; }
    public void setNumberOfSlots(Integer numberOfSlots) { this.numberOfSlots = numberOfSlots; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Integer utilisateurId) { 
        this.utilisateurId = utilisateurId; 
    }

    // NOUVEAUX GETTERS ET SETTERS POUR LES CHAMPS DE PAIEMENT
    public String getPaymentMethod() { 
        // Retourner une valeur par défaut si null
        return paymentMethod != null ? paymentMethod : "MONCASH"; 
    }
    
    public void setPaymentMethod(String paymentMethod) { 
        if (paymentMethod != null) {
            switch(paymentMethod.toUpperCase()) {
                case "SOLDE":
                case "MONCASH":
                case "NATCASH":
                    this.paymentMethod = paymentMethod.toUpperCase();
                    break;
                default:
                    this.paymentMethod = "MONCASH"; // Valeur par défaut
            }
        } else {
            this.paymentMethod = "MONCASH"; // Valeur par défaut
        }
    }

    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }

    public String getPayerPhone() { return payerPhone; }
    public void setPayerPhone(String payerPhone) { this.payerPhone = payerPhone; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentNote() { return paymentNote; }
    public void setPaymentNote(String paymentNote) { this.paymentNote = paymentNote; }

    public String getPaymentStatus() { 
        // Retourner une valeur par défaut si null
        if (paymentStatus != null) {
            switch(paymentStatus.toLowerCase()) {
                case "pending":
                case "completed":
                case "failed":
                case "refunded":
                    return paymentStatus;
                default:
                    return "pending";
            }
        }
        return "pending"; // Valeur par défaut
    }
    
    public void setPaymentStatus(String paymentStatus) { 
        if (paymentStatus != null) {
            switch(paymentStatus.toLowerCase()) {
                case "pending":
                case "completed":
                case "failed":
                case "refunded":
                    this.paymentStatus = paymentStatus;
                    break;
                default:
                    this.paymentStatus = "pending";
            }
        } else {
            this.paymentStatus = "pending"; // Valeur par défaut
        }
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", prixTotal=" + prixTotal +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", numberOfNights=" + numberOfNights +
                ", numberOfSlots=" + numberOfSlots +
                ", durationHours=" + durationHours +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", utilisateurId=" + utilisateurId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", payerName='" + payerName + '\'' +
                ", payerPhone='" + payerPhone + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentNote='" + paymentNote + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}