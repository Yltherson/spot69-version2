package com.spot69.utils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import com.spot69.model.CommandeDetail;
import com.spot69.model.Credit; // si tu as un modèle Credit

public class EmailUtils {

    private static final String EMAIL = "contact@spot69.net";
    private static final String SMTP_PASSWORD = "Emanagement2024@"; // mot de passe SMTP généré sur GoDaddy
    
    
    public static String genererHtmlDetailsCommande(List<CommandeDetail> details) {
        if (details == null || details.isEmpty()) return "<p>Aucun détail de commande disponible.</p>";

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (CommandeDetail d : details) {
            if (d.getProduit() != null) {
                sb.append("<li>")
                  .append(d.getProduit().getNom())
                  .append(" - Qté: ").append(d.getQuantite())
                  .append(" - Prix Unitaire: ").append(d.getPrixUnitaire())
                  .append("</li>");
            }
            if (d.getPlat() != null) {
                sb.append("<li>")
                  .append(d.getPlat().getNom())
                  .append(" - Qté: ").append(d.getQuantite())
                  .append("</li>");
            }
        }
        sb.append("</ul>");
        return sb.toString();
    }


    public static void envoyerEmailValidationCredit(String destinataire, Credit credit, List<CommandeDetail> details) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.office365.com");	
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Validation de commande crédit - Pot69");

            String htmlContent = "<html><body>"
                    + "<h2>Validation de commande crédit</h2>"
                    + "<p>Une commande crédit a été effectuée sur votre compte :</p>"
                    + "<ul>"
                    + "<li><strong>Montant :</strong> " + credit.getMontantTotal() + " HTG</li>"
                    + "<li><strong>Date :</strong> " + new Date() + "</li>"
                    + "</ul>"
                    + "<h3>Détails de la commande :</h3>"
                    + genererHtmlDetailsCommande(details)
                    + "<p><strong>IMPORTANT :</strong> Cette commande a été effectuée à votre compte.</p>"
                    + "<p>Si ce n'est pas vous, la commande sera automatiquement annulée dans 5 minutes.</p>"
                    + "<br><p>Cordialement,<br>L'équipe Pot69</p>"
                    + "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Email de validation envoyé à: " + destinataire);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + destinataire);
            e.printStackTrace();
        }
    }
    
    public static void envoyerEmailAnnulationCredit(String destinataire, List<CommandeDetail> details) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.office365.com");    
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Annulation de votre crédit - Pot69");

            String htmlContent = "<html><body>"
                    + "<h2>Annulation de crédit</h2>"
                    //+ "<p>Le crédit avec l'ID <strong>" + credit.getId() + "</strong> a été automatiquement annulé car il n'a pas été validé dans les délais.</p>"
                    + "<h3>Détails de la commande annulée :</h3>"
                    + genererHtmlDetailsCommande(details)
                    + "<br><p>Cordialement,<br>L'équipe Pot69</p>"
                    + "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Email d'annulation envoyé à: " + destinataire);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email d'annulation: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
