<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
 import="com.spot69.model.Reservation,com.spot69.model.Utilisateur,java.util.List,java.text.SimpleDateFormat"%>
<%
Reservation reservation = (Reservation) request.getAttribute("reservation");
List<Utilisateur> utilisateurs = (List<Utilisateur>) request.getAttribute("utilisateurs");
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
%>

<div class="form-section">
  <h6 class="form-section-title">Informations générales</h6>
  <div class="row">
    <div class="col-md-6">
      <div class="form-group">
        <label for="editTitle">Titre *</label>
        <input type="text" class="form-control" id="editTitle" name="title" 
               value="<%= reservation.getTitle() %>" required>
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-group">
        <label for="editRoomId">Chambre *</label>
        <input type="text" class="form-control" id="editRoomId" name="roomId" 
               value="<%= reservation.getRoomId() %>" required>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <div class="form-group">
        <label for="editType">Type de réservation *</label>
        <select class="form-control" id="editType" name="type" required>
          <option value="sejour" <%= "sejour".equals(reservation.getType()) ? "selected" : "" %>>Séjour</option>
          <option value="nuit" <%= "nuit".equals(reservation.getType()) ? "selected" : "" %>>Nuit</option>
          <option value="jour" <%= "jour".equals(reservation.getType()) ? "selected" : "" %>>Jour</option>
          <option value="moment" <%= "moment".equals(reservation.getType()) ? "selected" : "" %>>Moment</option>
        </select>
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-group">
        <label for="editPrixTotal">Prix Total (HTG) *</label>
        <input type="number" step="0.01" class="form-control" id="editPrixTotal" name="prixTotal" 
               value="<%= reservation.getPrixTotal() %>" required>
      </div>
    </div>
  </div>
</div>

<div class="form-section">
  <h6 class="form-section-title">Période de réservation</h6>
  <div class="row">
    <div class="col-md-6">
      <div class="form-group">
        <label for="editStart">Date et heure de début *</label>
        <input type="datetime-local" class="form-control" id="editStart" name="start" 
               value="<%= dateFormat.format(reservation.getStart()) %>" required>
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-group">
        <label for="editEnd">Date et heure de fin *</label>
        <input type="datetime-local" class="form-control" id="editEnd" name="end" 
               value="<%= dateFormat.format(reservation.getEnd()) %>" required>
      </div>
    </div>
  </div>
</div>

<!-- Champs supplémentaires selon le type -->
<div class="form-section" id="editSejourFields" 
     style="display: <%= "sejour".equals(reservation.getType()) ? "block" : "none" %>;">
  <h6 class="form-section-title">Détails du séjour</h6>
  <div class="row">
    <div class="col-md-6">
      <div class="form-group">
        <label for="editNumberOfNights">Nombre de nuits</label>
        <input type="number" class="form-control" id="editNumberOfNights" name="numberOfNights" 
               value="<%= reservation.getNumberOfNights() != null ? reservation.getNumberOfNights() : "" %>" min="1">
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-group">
        <label for="editArrivalTime">Heure d'arrivée</label>
        <input type="time" class="form-control" id="editArrivalTime" name="arrivalTime" 
               value="<%= reservation.getArrivalTime() != null ? reservation.getArrivalTime() : "" %>">
      </div>
    </div>
  </div>
</div>

<div class="form-section" id="editMomentFields" 
     style="display: <%= "moment".equals(reservation.getType()) ? "block" : "none" %>;">
  <h6 class="form-section-title">Détails du moment</h6>
  <div class="row">
    <div class="col-md-6">
      <div class="form-group">
        <label for="editNumberOfSlots">Nombre de créneaux</label>
        <input type="number" class="form-control" id="editNumberOfSlots" name="numberOfSlots" 
               value="<%= reservation.getNumberOfSlots() != null ? reservation.getNumberOfSlots() : "" %>" min="1">
      </div>
    </div>
    <div class="col-md-6">
      <div class="form-group">
        <label for="editDurationHours">Durée (heures)</label>
        <input type="number" class="form-control" id="editDurationHours" name="durationHours" 
               value="<%= reservation.getDurationHours() != null ? reservation.getDurationHours() : "" %>" min="1">
      </div>
    </div>
  </div>
</div>

<div class="form-section">
  <h6 class="form-section-title">Client</h6>
  <div class="form-group">
    <label for="editUtilisateurId">Utilisateur *</label>
    <select class="form-control" id="editUtilisateurId" name="utilisateurId" required>
      <option value="">Sélectionnez un utilisateur</option>
      <% 
      if (utilisateurs != null) {
          for (Utilisateur user : utilisateurs) { 
      %>
          <option value="<%= user.getId() %>" 
                  <%= reservation.getUtilisateurId() != null && 
                      reservation.getUtilisateurId().equals(user.getId()) ? "selected" : "" %>>
            <%= user.getLogin() %> (<%= user.getNom() %> <%= user.getPrenom() %>)
          </option>
      <% 
          }
      }
      %>
    </select>
  </div>
</div>

<div class="form-section">
  <h6 class="form-section-title">Statut</h6>
  <div class="form-group">
    <label for="editStatus">Statut</label>
    <select class="form-control" id="editStatus" name="status">
      <option value="en cours" <%= "en cours".equals(reservation.getStatus()) ? "selected" : "" %>>En cours</option>
      <option value="confirmé" <%= "confirmé".equals(reservation.getStatus()) ? "selected" : "" %>>Confirmé</option>
      <option value="annulé" <%= "annulé".equals(reservation.getStatus()) ? "selected" : "" %>>Annulé</option>
    </select>
  </div>
</div>

<script>
$(document).ready(function() {
    // Gérer l'affichage des champs supplémentaires selon le type
    $('#editType').change(function() {
        var type = $(this).val();
        $('#editSejourFields').toggle(type === 'sejour');
        $('#editMomentFields').toggle(type === 'moment');
    });
    
    // Validation des dates
    $('#editReservationForm').submit(function(e) {
        var start = new Date($('#editStart').val());
        var end = new Date($('#editEnd').val());
        
        if (start >= end) {
            e.preventDefault();
            alert('La date de fin doit être postérieure à la date de début.');
            return false;
        }
        
        return true;
    });
});
</script>