package com.spot69.utils;

import com.spot69.dao.CommandeDAO;
import com.spot69.model.Commande;
import com.spot69.model.CommandeDetail;
import com.spot69.model.Panier;
import com.spot69.model.Plat;
import com.spot69.model.Produit;
import com.spot69.model.Utilisateur;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ImpressionCommande {
    
    private CommandeDAO commandeDAO;
    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormatWithDecimal;
    
    public ImpressionCommande() {
        this.commandeDAO = new CommandeDAO();
     // Configuration du format avec séparateurs de milliers
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRENCH);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        
        // Format pour les nombres entiers
        this.decimalFormat = new DecimalFormat("#,##0", symbols);
        
        // Format pour les nombres avec décimales
        this.decimalFormatWithDecimal = new DecimalFormat("#,##0.00", symbols);
    }
    
    /**
     * Méthode qui prend en paramètre un ID de commande et lance l'impression dans le navigateur
     * @param commandeId ID de la commande à imprimer
     * @param response HttpServletResponse pour écrire le HTML
     */
    public void imprimerCommande(int commandeId, HttpServletResponse response) throws IOException {
        // Récupérer la commande
        Commande commande = commandeDAO.getCommandeById(commandeId);
        
        if (commande == null) {
            response.getWriter().write("<html><body><h1>Commande non trouvée</h1></body></html>");
            return;
        }
        
        // Récupérer les détails de la commande
        List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
        commande.setDetails(details);
        
        // Configurer la réponse pour l'impression
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        // Générer le HTML pour l'impression
        String html = genererHTMLImpression(commande);
        
        // Écrire le HTML dans la réponse
        out.println(html);
        
        // Script JavaScript pour déclencher l'impression automatiquement
        out.println("<script type=\"text/javascript\">");
        out.println("window.onload = function() {");
        out.println("    window.print();");
        out.println("    // Optionnel: rediriger après impression");
        out.println("    // setTimeout(function() { window.close(); }, 1000);");
        out.println("}");
        out.println("</script>");
    }
    
    /**
     * Génère le HTML formaté pour l'impression de la commande
     */
    private String genererHTMLImpression(Commande commande) {
        StringBuilder html = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"fr\">");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\">");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("    <title>Commande #").append(commande.getNumeroCommande()).append("</title>");
        html.append("    <style>");
        html.append("        @media print {");
        html.append("            body { font-family: 'Courier New', monospace; font-size: 10pt; }");
        html.append("            .no-print { display: none; }");
        html.append("            @page { margin-top: 0.5cm;"
        		+ "						margin-right: 2cm;"
        		+ "						margin-left: 2cm;"
        		+ " }");
        html.append("        }");
        html.append("        @media screen {");
        html.append("            body { font-family: Arial, sans-serif; max-width: 800px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; }");
        html.append("            .print-btn { background: #4CAF50; color: white; padding: 10px 20px; border: none; cursor: pointer; margin: 20px 0; }");
        html.append("        }");
        html.append("        .header { display: flex; justify-content: space-between; margin-bottom: 0px; line-height: 1.1; margin-top: 5px; }");
        html.append("        .left-header { text-align: left; flex: 1; }");
        html.append("        .right-header { text-align: right; flex: 1; }");
        html.append("        .company-name { font-size: 22px; font-weight: bold; margin: 0; }");
        html.append("        .order-number { font-size: 11px; margin: 2px 0 2px 0; }");
        html.append("        .contact-info { font-size: 15px; margin: 0; line-height: 1.2; }");
        html.append("        .ticket-title { text-align: center; font-size: 12px; margin: 5px 0; border-bottom: 1px solid #000; padding-bottom: 5px; }");
        html.append("        .info-section { margin-bottom: 0px; }");
        html.append("        .info-row { display: flex; justify-content: space-between; margin-bottom: 0px; font-size: 14px; }");
        html.append("        .info-label { font-weight: bold; }");
        html.append("        .table-details { width: 100%; border-collapse: collapse; margin: 10px 0; font-size: 14px; }");
        html.append("        .table-details th { border-bottom: 1px solid #000; padding: 1px; text-align: left; background: #f0f0f0; }");
        html.append("        .table-details th:nth-child(1) { width: 60%; }");  /* Description - plus large */
        html.append("        .table-details th:nth-child(2) { width: 10%; text-align: center; }");  /* Qté */
        html.append("        .table-details th:nth-child(3) { width: 15%; }");  /* Prix U. */
        html.append("        .table-details th:nth-child(4) { width: 15%; }");  /* Total */
        html.append("        .table-details td:nth-child(1) { width: 60%; }");  /* Description - plus large */
        html.append("        .table-details td:nth-child(2) { width: 10%; text-align: center; }");  /* Qté */
        html.append("        .table-details td:nth-child(3) { width: 15%; }");  /* Prix U. */
        html.append("        .table-details td:nth-child(4) { width: 15%; }");  /* Total */
        html.append("        .table-details td { padding: 1px 4px; border-bottom: 1px solid #ddd; }");
        html.append("        .table-details .text-right { text-align: right; }");
        html.append("        .totals { margin-top: 10px; padding-top: 2px; border-top: 1px dashed #000; }");
        html.append("        .total-row { display: flex; justify-content: space-between; margin-bottom: 2px; font-size: 10px; }");
        html.append("        .total-label { font-weight: bold; }");
        html.append("        .grand-total { font-size: 18px; font-weight: bold; margin-top: 2px; }");
        html.append("        .footer { text-align: center; margin-top: 10px; font-size: 9px; color: #666; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        
        // En-tête modifié selon les instructions
        html.append("    <div class=\"header\">");
        
        // Partie gauche : Nom de l'entreprise et numéro de commande
        html.append("        <div class=\"left-header\">");
        html.append("            <div class=\"company-name\">DIVINITE DEPOT</div>");
        html.append("            <div class=\"contact-info\"> Frecino, Saint-Marc, Haiti</div>");
        html.append("            <div class=\"contact-info\">Tél: 47429057</div>");
        html.append("        </div>");
        
        // Partie droite : Adresse et informations de contact
        html.append("        <div class=\"right-header\">");
        html.append("            <div class=\"company-name\">VENTE CASH</div>");
        html.append("            <div class=\"order-number\">Sale No: ").append(commande.getNumeroCommande()).append("</div>");
        html.append("        </div>");
        html.append("    </div>");
        
        // Informations Date et Staff (sans ligne au-dessus)
        html.append("    <div class=\"info-section\">");
        html.append("        <div class=\"info-row\">");
        html.append("            <span class=\"info-label\">Date:</span>");
        html.append("            <span>").append(sdf.format(commande.getDateCommande())).append("</span>");
        html.append("        </div>");
        
        if (commande.getUtilisateur() != null) {
            html.append("        <div class=\"info-row\">");
            html.append("            <span class=\"info-label\">CASHIER:</span>");
            html.append("            <span>").append(commande.getUtilisateur().getNomComplet()).append("</span>");
            html.append("        </div>");
        }
        
        if (commande.getNotes() != null) {
            html.append("        <div class=\"info-row\">");
            html.append("            <span class=\"info-label\">CLIENT:</span>");
            html.append("            <span>").append(commande.getNotes()).append("</span>");
            html.append("        </div>");
        }
        
        if (commande.getClient() != null) {
            html.append("        <div class=\"info-row\">");
            html.append("            <span class=\"info-label\">Client:</span>");
            html.append("            <span>").append(commande.getClient().getNomComplet()).append("</span>");
            html.append("        </div>");
        }
        
        html.append("    </div>");
        
        // Détails des articles
        html.append("    <table class=\"table-details\">");
        html.append("        <thead>");
        html.append("            <tr>");
        html.append("                <th>Description</th>");
        html.append("                <th>Qté</th>");
        html.append("                <th class=\"text-right\">Prix U.</th>");
        html.append("                <th class=\"text-right\">Total</th>");
        html.append("            </tr>");
        html.append("        </thead>");
        html.append("        <tbody>");
        
        List<CommandeDetail> details = commande.getDetails();
        if (details != null && !details.isEmpty()) {
            for (CommandeDetail detail : details) {
                String articleNom = "N/A";
                if (detail.getProduit() != null && detail.getProduit().getNom() != null) {
                    articleNom = detail.getProduit().getNom();
                } else if (detail.getPlat() != null && detail.getPlat().getNom() != null) {
                    articleNom = detail.getPlat().getNom();
                }
                
                html.append("            <tr>");
                html.append("                <td>").append(articleNom).append("</td>");
                html.append("                <td>").append(detail.getQuantite()).append("</td>");
                html.append("                <td class=\"text-right\">").append(formatMontant(detail.getPrixUnitaire())).append("</td>");
                html.append("                <td class=\"text-right\">").append(formatMontant(detail.getSousTotal())).append("</td>");
                html.append("            </tr>");
            }
        } else {
            html.append("            <tr><td colspan=\"4\" style=\"text-align: center;\">Aucun article</td></tr>");
        }
        
        html.append("        </tbody>");
        html.append("    </table>");
        
        // Totaux
        if (commande.getMontantPaye() != null && commande.getMontantTotal() != null 
            && commande.getMontantPaye().compareTo(commande.getMontantTotal()) < 0) {
            html.append("        <div class=\"total-row\">");
            html.append("            <span class=\"total-label\">Montant payé:</span>");
            html.append("            <span>").append(formatMontant(commande.getMontantPaye())).append("</span>");
            html.append("        </div>");
            
            html.append("        <div class=\"total-row\">");
            html.append("            <span class=\"total-label\">Reste à payer:</span>");
            html.append("            <span>").append(formatMontant(commande.getMontantTotal().subtract(commande.getMontantPaye()))).append("</span>");
            html.append("        </div>");
        }
        
        html.append("        <div class=\"total-row grand-total\">");
        html.append("            <span>TOTAL:</span>");
        html.append("            <span>").append(formatMontant(commande.getMontantTotal())).append(" HTG</span>");
        html.append("        </div>");
        
      
        
        html.append("    </div>");
        
        html.append("    <div class=\"footer\">");
        html.append("    </div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    
//    /**
//     * Formate un montant pour l'affichage
//     */
//    private String formatMontant(java.math.BigDecimal montant) {
//        if (montant == null) return "0";
//        return String.format("%.0f", montant.doubleValue()).replace('.', ',');
//    }
    
    private String formatMontant(java.math.BigDecimal montant) {
        if (montant == null) return "0";
        
        try {
            // Vérifier si le montant a des décimales
            double valeur = montant.doubleValue();
            
            if (montant.remainder(java.math.BigDecimal.ONE).compareTo(java.math.BigDecimal.ZERO) == 0) {
                // Pas de décimales
                return decimalFormat.format(valeur) + "";
            } else {
                // Avec décimales
                return decimalFormatWithDecimal.format(valeur) + "";
            }
        } catch (Exception e) {
            // En cas d'erreur, retourner une représentation simple
            return montant.toString() + "";
        }
    }
    
  
    
    /**
     * Version alternative avec des options personnalisables
     */
    public void imprimerCommandeAvecOptions(int commandeId, HttpServletResponse response, 
                                           boolean autoPrint, boolean includeHeader, 
                                           String template) throws IOException {
        Commande commande = commandeDAO.getCommandeById(commandeId);
        
        if (commande == null) {
            response.getWriter().write("<html><body><h1>Commande non trouvée</h1></body></html>");
            return;
        }
        
        // Récupérer les détails
        List<CommandeDetail> details = commandeDAO.getDetailsByCommandeId(commandeId);
        commande.setDetails(details);
        
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        // Utiliser un template spécifique si fourni
        String html = (template != null && !template.isEmpty()) 
            ? genererHTMLPersonnalise(commande, template, includeHeader) 
            : genererHTMLImpression(commande);
        
        out.println(html);
        
        if (autoPrint) {
            out.println("<script type=\"text/javascript\">");
            out.println("window.onload = function() {");
            out.println("    window.print();");
            out.println("}");
            out.println("</script>");
        }
    }
    
    /**
     * Méthode pour générer un HTML personnalisé selon un template
     */
    private String genererHTMLPersonnalise(Commande commande, String template, boolean includeHeader) {
        // Implémentation basique - à adapter selon vos besoins
        if ("minimal".equals(template)) {
            return genererHTMLMinimal(commande);
        } else if ("detail".equals(template)) {
            return genererHTMLDetail(commande, includeHeader);
        }
        return genererHTMLImpression(commande);
    }
    
    /**
     * Template minimal pour les tickets de caisse
     */
    private String genererHTMLMinimal(Commande commande) {
        StringBuilder html = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset=\"UTF-8\"><style>");
        html.append("body { font-family: 'Courier New', monospace; font-size: 10pt; margin: 0; padding: 10px; }");
        html.append("@page { margin: 0; }");
        html.append("table { width: 100%; border-collapse: collapse; }");
        html.append("td { padding: 2px 0; }");
        html.append(".text-right { text-align: right; }");
        html.append(".text-center { text-align: center; }");
        html.append(".separator { border-top: 1px dashed #000; margin: 5px 0; }");
        html.append("</style></head><body>");
        
        html.append("<div class=\"text-center\"><strong>SPOT 69</strong><br>");
        html.append("Commande #").append(commande.getNumeroCommande()).append("<br>");
        html.append(sdf.format(commande.getDateCommande())).append("</div>");
        html.append("<div class=\"separator\"></div>");
        
        html.append("<table>");
        List<CommandeDetail> details = commande.getDetails();
        if (details != null && !details.isEmpty()) {
            for (CommandeDetail detail : details) {
                String nom = "N/A";
                if (detail.getProduit() != null && detail.getProduit().getNom() != null) {
                    nom = detail.getProduit().getNom();
                } else if (detail.getPlat() != null && detail.getPlat().getNom() != null) {
                    nom = detail.getPlat().getNom();
                }
                
                html.append("<tr>");
                html.append("<td>").append(detail.getQuantite()).append("x ").append(nom).append("</td>");
                html.append("<td class=\"text-right\">").append(formatMontant(detail.getSousTotal())).append("</td>");
                html.append("</tr>");
            }
        }
        
        html.append("</table>");
        
        html.append("<div class=\"separator\"></div>");
        html.append("<table>");
        html.append("<tr><td><strong>TOTAL</strong></td><td class=\"text-right\"><strong>").append(formatMontant(commande.getMontantTotal())).append("</strong></td></tr>");
        html.append("</table>");
        
        html.append("<div class=\"text-center\" style=\"margin-top: 20px; font-size: 8pt;\">");
        html.append("Merci !");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }
    
    /**
     * Template détaillé
     */
    private String genererHTMLDetail(Commande commande, boolean includeHeader) {
        // Pour cette version, on utilise le même HTML que l'impression normale
        // mais on pourrait ajouter plus de détails si nécessaire
        return genererHTMLImpression(commande);
    }
    /**
     * Méthode pour générer un proforma à partir des items du panier
     * @param client Client pour le proforma
     * @param utilisateur Utilisateur qui crée le proforma
     * @param panierItems Liste des items du panier
     * @param response HttpServletResponse pour écrire le HTML
     */
    public void imprimerProforma(Utilisateur client, Utilisateur utilisateur, 
                               List<Panier> panierItems, HttpServletResponse response) 
                               throws IOException {
        
        // Configurer la réponse
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        // Générer le HTML du proforma
        String html = genererHTMLProforma(client, utilisateur, panierItems);
        
        // Écrire le HTML dans la réponse
        out.println(html);
        
        // Script JavaScript pour déclencher l'impression automatiquement
        out.println("<script type=\"text/javascript\">");
        out.println("window.onload = function() {");
        out.println("    window.print();");
        out.println("}");
        out.println("</script>");
    }

    /**
     * Génère le HTML formaté pour le proforma
     */
//    private String genererHTMLProforma(Utilisateur client, Utilisateur utilisateur, 
//                                      List<Panier> panierItems) {
//        System.out.println("APPELPROFORMAT");
//        StringBuilder html = new StringBuilder();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        
//        // Calculer le total
//        BigDecimal total = BigDecimal.ZERO;
//        for (Panier item : panierItems) {
//            BigDecimal itemTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
//            total = total.add(itemTotal);
//        }
//        
//        // Générer un numéro de proforma unique
//        String proformaNumero = "PRO-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        
//        html.append("<!DOCTYPE html>");
//        html.append("<html lang=\"fr\">");
//        html.append("<head>");
//        html.append("    <meta charset=\"UTF-8\">");
//        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
//        html.append("    <title>Proforma #").append(proformaNumero).append("</title>");
//        html.append("    <style>");
//        html.append("        @media print {");
//        html.append("            body { font-family: 'Courier New', monospace; font-size: 12pt; }");
//        html.append("            .no-print { display: none; }");
//        html.append("            @page { margin: 0.5cm; }");
//        html.append("        }");
//        html.append("        @media screen {");
//        html.append("            body { font-family: Arial, sans-serif; max-width: 800px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; }");
//        html.append("        }");
//        html.append("        .header { text-align: center; margin-bottom: 30px; }");
//        html.append("        .restaurant-name { font-size: 24px; font-weight: bold; margin-bottom: 5px; color: #000000; }");
//        html.append("        .document-title { text-align: center; font-size: 20px; margin: 20px 0; border-bottom: 2px solid #ddd; padding-bottom: 10px; }");
//        html.append("        .info-section { margin-bottom: 20px;  padding: 15px; border-radius: 5px; }");
//        html.append("        .info-row { display: flex; margin-bottom: 8px; }");
//        html.append("        .info-label { font-weight: bold; min-width: 120px; }");
//        html.append("        .table-details { width: 100%; border-collapse: collapse; margin: 20px 0; }");
//        html.append("        .table-details th { background: #343a40; color: white; padding: 10px; text-align: left; }");
//        html.append("        .table-details td { padding: 8px 10px; border-bottom: 1px solid #ddd; }");
//        html.append("        .table-details .text-right { text-align: right; }");
//        html.append("        .total-section { margin-top: 30px; padding-top: 20px; border-top: 2px dashed #ddd; }");
//        html.append("        .total-row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }");
//        html.append("        .grand-total { font-size: 18px;  margin-top: 10px; color: #000000; }");
//        html.append("        .footer { text-align: center; margin-top: 40px; font-size: 12px; color: #666; }");
//        html.append("        .watermark { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(-45deg);");
//        html.append("                   font-size: 80px; color: rgba(218, 175, 90, 0.1); z-index: -1; pointer-events: none; }");
//        html.append("        .signature-line { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; }");
//        html.append("    </style>");
//        html.append("</head>");
//        html.append("<body>");
//        
//        // Watermark
//        html.append("    <div class=\"watermark\">PROFORMA</div>");
//        
//        // En-tête
//        html.append("    <div class=\"header\">");
//        html.append("        <div class=\"restaurant-name\"><img src='image/logo.png' width='110', height='auto';></div>");
//        html.append("        <div class=\"restaurant-address\"> St Marc, Rue Bonet </div>");
//        html.append("        <div class=\"restaurant-address\">Tél: 4038-8773 </div>");
//        html.append("    </div>");
//        
//        // Titre du document
//        html.append("    <div class=\"document-title\">");
//        html.append("        PROFORMA N° ").append(proformaNumero);
//        html.append("    </div>");
//        
//        // Informations
//        html.append("    <div class=\"info-section\">");
//        html.append("        <div class=\"info-row\">");
//        html.append("            <span class=\"info-label\">Date:</span>");
//        html.append("            <span>").append(sdf.format(new Date())).append("</span>");
//        html.append("        </div>");
//        
//        if (client != null) {
//            html.append("        <div class=\"info-row\">");
//            html.append("            <span class=\"info-label\">Client:</span>");
//            html.append("            <span>").append(client.getNomComplet()).append("</span>");
//            html.append("        </div>");
//            
//            if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
//                html.append("        <div class=\"info-row\">");
//                html.append("            <span class=\"info-label\">Téléphone:</span>");
//                html.append("            <span>").append(client.getTelephone()).append("</span>");
//                html.append("        </div>");
//            }
//        }
//        
//        if (utilisateur != null) {
//            html.append("        <div class=\"info-row\">");
//            html.append("            <span class=\"info-label\">Préparé par:</span>");
//            html.append("            <span>").append(utilisateur.getNomComplet()).append("</span>");
//            html.append("        </div>");
//        }
//        
//        html.append("    </div>");
//        
//        // Détails des articles
//        html.append("    <table class=\"table-details\">");
//        html.append("        <thead>");
//        html.append("            <tr>");
//        html.append("                <th>Article</th>");
//        html.append("                <th>Description</th>");
//        html.append("                <th>Qté</th>");
//        html.append("                <th class=\"text-right\">Prix U.</th>");
//        html.append("                <th class=\"text-right\">Total</th>");
//        html.append("            </tr>");
//        html.append("        </thead>");
//        html.append("        <tbody>");
//        
//        if (panierItems != null && !panierItems.isEmpty()) {
//            for (Panier item : panierItems) {
//                String articleNom = "N/A";
//                String description = "";
//                BigDecimal prixUnitaire = BigDecimal.ZERO;
//                int quantite = item.getQuantite();
//                BigDecimal sousTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
//                
//                // Déterminer le type d'article
//                if (item.getPlat() != null) {
//                    Plat plat = item.getPlat();
//                    articleNom = plat.getNom();
//                    
//                    if (plat.getProduit() != null) {
//                        Produit produit = plat.getProduit();
//                        prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
//                        description = produit.getDescription() != null ? produit.getDescription() : "";
//                    } else {
//                        prixUnitaire = BigDecimal.valueOf(plat.getPrix());
//                        description = plat.getDescription() != null ? plat.getDescription() : "";
//                    }
//                    
//                } else if (item.getProduit() != null) {
//                    Produit produit = item.getProduit();
//                    articleNom = produit.getNom();
//                    description = produit.getDescription() != null ? produit.getDescription() : "";
//                    prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
//                }
//                
//                // Si le sous-total n'est pas disponible, le calculer
//                if (sousTotal.compareTo(BigDecimal.ZERO) == 0 && prixUnitaire.compareTo(BigDecimal.ZERO) > 0) {
//                    sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
//                }
//                
//                html.append("            <tr>");
//                html.append("                <td>").append(articleNom).append("</td>");
//                html.append("                <td>").append(description.length() > 50 ? description.substring(0, 50) + "..." : description).append("</td>");
//                html.append("                <td>").append(quantite).append("</td>");
//                html.append("                <td class=\"text-right\">").append(formatMontant(prixUnitaire)).append("</td>");
//                html.append("                <td class=\"text-right\">").append(formatMontant(sousTotal)).append("</td>");
//                html.append("            </tr>");
//            }
//        } else {
//            html.append("            <tr><td colspan=\"5\" style=\"text-align: center;\">Aucun article dans ce proforma</td></tr>");
//        }
//        
//        html.append("        </tbody>");
//        html.append("    </table>");
//        
//        // Totaux
//        html.append("    <div class=\"total-section\">");
//        html.append("        <div class=\"total-row grand-total\">");
//        html.append("            <span>TOTAL PROFORMA:</span>");
//        html.append("            <span>").append(formatMontant(total)).append("</span>");
//        html.append("        </div>");
//        html.append("    </div>");
//        
//        // Notes et signatures
////        html.append("    <div class=\"footer\">");
////        html.append("        <p><strong>Notes importantes:</strong></p>");
////        html.append("        <p>1. Ce document est un proforma et ne constitue pas une facture définitive.</p>");
////        html.append("        <p>2. Les prix sont indiqués en Gourdes (HTG) et sont valables jusqu'à expiration.</p>");
////        html.append("        <p>3. Validité: 7 jours à partir de la date d'émission.</p>");
////        html.append("        <p>4. Les taxes applicables seront ajoutées lors de la facturation finale.</p>");
////        html.append("        <p>5. Pour toute question, contactez-nous au 3901-6969.</p>");
////        
////        html.append("        <div class=\"signature-line\">");
////        html.append("            <div style=\"display: flex; justify-content: space-between; margin-top: 30px;\">");
////        html.append("                <div style=\"text-align: center;\">");
////        html.append("                    <p>Pour le client</p>");
////        html.append("                    <p style=\"margin-top: 40px;\">_________________________</p>");
////        html.append("                    <p>Nom et signature</p>");
////        html.append("                    <p>Date: ___________________</p>");
////        html.append("                </div>");
////        html.append("                <div style=\"text-align: center;\">");
////        html.append("                    <p>Pour SPOT69 ROOFTOP</p>");
////        html.append("                    <p style=\"margin-top: 40px;\">_________________________</p>");
////        html.append("                    <p>Nom et signature</p>");
////        html.append("                    <p>Date: ___________________</p>");
////        html.append("                </div>");
////        html.append("            </div>");
////        html.append("        </div>");
////        html.append("    </div>");
////        
////        html.append("</body>");
////        html.append("</html>");
//        
//        return html.toString();
//    }

    /**
     * Génère le HTML formaté pour le proforma avec le même format que la commande
     */
    private String genererHTMLProforma(Utilisateur client, Utilisateur utilisateur, 
                                      List<Panier> panierItems) {
        System.out.println("APPEL PROFORMA");
        StringBuilder html = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        // Calculer le total
        BigDecimal total = BigDecimal.ZERO;
        for (Panier item : panierItems) {
            BigDecimal itemTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
            total = total.add(itemTotal);
        }
        
        // Générer un numéro de proforma unique
        String proformaNumero = "PRO-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"fr\">");
        html.append("<head>");
        html.append("    <meta charset=\"UTF-8\">");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("    <title>Proforma #").append(proformaNumero).append("</title>");
        html.append("    <style>");
        html.append("        @media print {");
        html.append("            body { font-family: 'Courier New', monospace; font-size: 10pt; }");
        html.append("            .no-print { display: none; }");
        html.append("            @page { margin-top: 0.5cm;"
                   + "                     margin-right: 2cm;"
                   + "                     margin-left: 2cm; }");
        html.append("        }");
        html.append("        @media screen {");
        html.append("            body { font-family: Arial, sans-serif; max-width: 800px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; }");
        html.append("            .print-btn { background: #4CAF50; color: white; padding: 10px 20px; border: none; cursor: pointer; margin: 20px 0; }");
        html.append("        }");
        html.append("        .header { display: flex; justify-content: space-between; margin-bottom: 0px; line-height: 1.1;  margin-top: 5px; }");
        html.append("        .left-header { text-align: left; flex: 1; }");
        html.append("        .right-header { text-align: right; flex: 1; }");
        html.append("        .company-name { font-size: 22px; font-weight: bold; margin: 0; }");
        html.append("        .order-number { font-size: 11px; margin: 2px 0 2px 0; }");
        html.append("        .contact-info { font-size: 15px; margin: 0; line-height: 1.2; }");
        html.append("        .ticket-title { text-align: center; font-size: 12px; margin: 5px 0; border-bottom: 1px solid #000; padding-bottom: 5px; }");
        html.append("        .info-section { margin-bottom: 0px; }");
        html.append("        .info-row { display: flex; justify-content: space-between; margin-bottom: 0px; font-size: 14px; }");
        html.append("        .info-label { font-weight: bold; }");
        html.append("        .table-details { width: 100%; border-collapse: collapse; margin: 10px 0; font-size: 14px; }");
        html.append("        .table-details th { border-bottom: 1px solid #000; padding: 1px; text-align: left; background: #f0f0f0; }");
        html.append("        .table-details th:nth-child(1) { width: 60%; }");  /* Description - plus large */
        html.append("        .table-details th:nth-child(2) { width: 10%; text-align: center; }");  /* Qté */
        html.append("        .table-details th:nth-child(3) { width: 15%; }");  /* Prix U. */
        html.append("        .table-details th:nth-child(4) { width: 15%; }");  /* Total */
        html.append("        .table-details td:nth-child(1) { width: 60%; }");  /* Description - plus large */
        html.append("        .table-details td:nth-child(2) { width: 10%; text-align: center; }");  /* Qté */
        html.append("        .table-details td:nth-child(3) { width: 15%; }");  /* Prix U. */
        html.append("        .table-details td:nth-child(4) { width: 15%; }");  /* Total */
        html.append("        .table-details td { padding: 1px 4px; border-bottom: 1px solid #ddd; }");
        html.append("        .table-details .text-right { text-align: right; }");
        html.append("        .totals { margin-top: 10px; padding-top: 2px; border-top: 1px dashed #000; }");
        html.append("        .total-row { display: flex; justify-content: space-between; margin-bottom: 2px; font-size: 10px; }");
        html.append("        .total-label { font-weight: bold; }");
        html.append("        .grand-total { font-size: 18px; font-weight: bold; margin-top: 2px; }");
        html.append("        .footer { text-align: center; margin-top: 10px; font-size: 9px; color: #666; }");
        html.append("        .watermark { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(-45deg);");
        html.append("                   font-size: 60px; color: rgba(0, 0, 0, 0.1); z-index: -1; pointer-events: none; font-weight: bold; }");
        html.append("        .document-type { text-align: center; font-size: 14px; font-weight: bold; margin: 10px 0; color: #333; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        
        // Watermark PROFORMA
        html.append("    <div class=\"watermark\">PROFORMA</div>");
        
        // En-tête modifié selon les instructions
        html.append("    <div class=\"header\">");
        
        // Partie gauche : Nom de l'entreprise et numéro de commande
        html.append("        <div class=\"left-header\">");
        html.append("            <div class=\"company-name\">DIVINITE DEPOT</div>");
        html.append("            <div class=\"contact-info\"> Frecino, Saint-Marc, Haiti</div>");
        html.append("            <div class=\"contact-info\">Tél: 47429057</div>");
        html.append("        </div>");
        
        // Partie droite : Adresse et informations de contact
        html.append("        <div class=\"right-header\">");
        html.append("            <div class=\"company-name\">PROFORMA</div>");
        html.append("            <div class=\"order-number\">No: ").append(proformaNumero).append("</div>");
        html.append("        </div>");
        html.append("    </div>");
        
        // Type de document
        html.append("    <div class=\"document-type\">");
        html.append("        DEVIS / PROFORMA");
        html.append("    </div>");
        
        // Informations Date et Staff (sans ligne au-dessus)
        html.append("    <div class=\"info-section\">");
        html.append("        <div class=\"info-row\">");
        html.append("            <span class=\"info-label\">Date:</span>");
        html.append("            <span>").append(sdf.format(new Date())).append("</span>");
        html.append("        </div>");
        
        if (utilisateur != null) {
            html.append("        <div class=\"info-row\">");
            html.append("            <span class=\"info-label\">CASHIER:</span>");
            html.append("            <span>").append(utilisateur.getNomComplet()).append("</span>");
            html.append("        </div>");
        }
        
        if (client != null) {
            html.append("        <div class=\"info-row\">");
            html.append("            <span class=\"info-label\">Client:</span>");
            html.append("            <span>").append(client.getNomComplet()).append("</span>");
            html.append("        </div>");
            
            if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
                html.append("        <div class=\"info-row\">");
                html.append("            <span class=\"info-label\">Téléphone:</span>");
                html.append("            <span>").append(client.getTelephone()).append("</span>");
                html.append("        </div>");
            }
        }
        
        html.append("    </div>");
        
        // Détails des articles
        html.append("    <table class=\"table-details\">");
        html.append("        <thead>");
        html.append("            <tr>");
        html.append("                <th>Description</th>");
        html.append("                <th>Qté</th>");
        html.append("                <th class=\"text-right\">Prix U.</th>");
        html.append("                <th class=\"text-right\">Total</th>");
        html.append("            </tr>");
        html.append("        </thead>");
        html.append("        <tbody>");
        
        if (panierItems != null && !panierItems.isEmpty()) {
            for (Panier item : panierItems) {
                String articleNom = "N/A";
                String description = "";
                BigDecimal prixUnitaire = BigDecimal.ZERO;
                int quantite = item.getQuantite();
                BigDecimal sousTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
                
                // Déterminer le type d'article
                if (item.getPlat() != null) {
                    Plat plat = item.getPlat();
                    articleNom = plat.getNom();
                    
                    if (plat.getProduit() != null) {
                        Produit produit = plat.getProduit();
                        prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
                        description = produit.getDescription() != null ? produit.getDescription() : "";
                    } else {
                        prixUnitaire = BigDecimal.valueOf(plat.getPrix());
                        description = plat.getDescription() != null ? plat.getDescription() : "";
                    }
                    
                } else if (item.getProduit() != null) {
                    Produit produit = item.getProduit();
                    articleNom = produit.getNom();
                    description = produit.getDescription() != null ? produit.getDescription() : "";
                    prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
                }
                
                // Si le sous-total n'est pas disponible, le calculer
                if (sousTotal.compareTo(BigDecimal.ZERO) == 0 && prixUnitaire.compareTo(BigDecimal.ZERO) > 0) {
                    sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
                }
                
                // Formater le nom de l'article avec description si disponible
                String displayName = articleNom;
                if (description != null && !description.isEmpty()) {
                    displayName = articleNom + " (" + description + ")";
                }
                
                html.append("            <tr>");
                html.append("                <td>").append(displayName).append("</td>");
                html.append("                <td>").append(quantite).append("</td>");
                html.append("                <td class=\"text-right\">").append(formatMontant(prixUnitaire)).append("</td>");
                html.append("                <td class=\"text-right\">").append(formatMontant(sousTotal)).append("</td>");
                html.append("            </tr>");
            }
        } else {
            html.append("            <tr><td colspan=\"4\" style=\"text-align: center;\">Aucun article</td></tr>");
        }
        
        html.append("        </tbody>");
        html.append("    </table>");
        
        // Totaux
        html.append("    <div class=\"totals\">");
        html.append("        <div class=\"total-row grand-total\">");
        html.append("            <span>TOTAL PROFORMA:</span>");
        html.append("            <span>").append(formatMontant(total)).append(" HTG</span>");
        html.append("        </div>");
        html.append("    </div>");
//        
//        // Notes
//        html.append("    <div class=\"info-section\" style=\"margin-top: 15px; font-size: 9px;\">");
//        html.append("        <div style=\"text-align: center; margin-bottom: 5px;\"><strong>INFORMATIONS IMPORTANTES</strong></div>");
//        html.append("        <div>1. Ce document est un PROFORMA et ne constitue pas une facture définitive.</div>");
//        html.append("        <div>2. Validité: 7 jours à partir de la date d'émission.</div>");
//        html.append("        <div>3. Les prix sont indiqués en Gourdes (HTG).</div>");
//        html.append("        <div>4. Pour toute question, contactez-nous au 47429057.</div>");
//        html.append("    </div>");
//        
//        html.append("    <div class=\"footer\">");
//        html.append("        <div>*** MERCI DE VOTRE CONFIANCEE ***</div>");
//        html.append("    </div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    /**
     * Version simplifiée pour impression rapide
     */
    public void imprimerProformaSimple(Utilisateur client, Utilisateur utilisateur,
                                     List<Panier> panierItems, HttpServletResponse response) 
                                     throws IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        // Générer un HTML plus simple
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        BigDecimal total = BigDecimal.ZERO;
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: 'Courier New', monospace; font-size: 12pt; margin: 0; padding: 10px; }");
        html.append("@page { margin: 0; }");
        html.append(".text-center { text-align: center; }");
        html.append(".text-right { text-align: right; }");
        html.append(".separator { border-top: 1px dashed #000; margin: 5px 0; }");
        html.append(".header { margin-bottom: 10px; }");
        html.append(".title { font-size: 16px; font-weight: bold; margin: 10px 0; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 10px 0; }");
        html.append("td { padding: 3px 0; }");
        html.append(".total { font-weight: bold; margin-top: 10px; }");
        html.append("</style>");
        html.append("</head><body>");
        
        // En-tête
        html.append("<div class=\"header text-center\">");
        html.append("<div><strong>SPOT69 ROOFTOP</strong></div>");
        html.append("<div>369 Autoroute de Delmas, Delmas 60</div>");
        html.append("<div>Tél: 3901-6969</div>");
        html.append("</div>");
        
        html.append("<div class=\"separator\"></div>");
        
        // Titre
        html.append("<div class=\"title text-center\">");
        html.append("PROFORMA - ").append(sdf.format(new Date()));
        html.append("</div>");
        
        html.append("<div class=\"separator\"></div>");
        
        // Informations client
        if (client != null) {
            html.append("<div>Client: <strong>").append(client.getNomComplet()).append("</strong></div>");
            if (client.getTelephone() != null && !client.getTelephone().isEmpty()) {
                html.append("<div>Tél: ").append(client.getTelephone()).append("</div>");
            }
        }
        
        if (utilisateur != null) {
            html.append("<div>Préparé par: ").append(utilisateur.getNomComplet()).append("</div>");
        }
        
        html.append("<div class=\"separator\"></div>");
        
        // Articles
        html.append("<table>");
        
        if (panierItems != null && !panierItems.isEmpty()) {
            for (Panier item : panierItems) {
                String articleNom = "N/A";
                int quantite = item.getQuantite();
                BigDecimal prixUnitaire = BigDecimal.ZERO;
                BigDecimal sousTotal = item.getTotal() != null ? item.getTotal() : BigDecimal.ZERO;
                
                if (item.getPlat() != null) {
                    Plat plat = item.getPlat();
                    articleNom = plat.getNom();
                    if (plat.getProduit() != null) {
                        prixUnitaire = plat.getProduit().getPrixVente() != null ? 
                                      plat.getProduit().getPrixVente() : BigDecimal.ZERO;
                    } else {
                        prixUnitaire = BigDecimal.valueOf(plat.getPrix());
                    }
                } else if (item.getProduit() != null) {
                    Produit produit = item.getProduit();
                    articleNom = produit.getNom();
                    prixUnitaire = produit.getPrixVente() != null ? produit.getPrixVente() : BigDecimal.ZERO;
                }
                
                if (sousTotal.compareTo(BigDecimal.ZERO) == 0 && prixUnitaire.compareTo(BigDecimal.ZERO) > 0) {
                    sousTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
                }
                
                total = total.add(sousTotal);
                
                html.append("<tr>");
                html.append("<td>").append(quantite).append("x ").append(articleNom).append("</td>");
                html.append("<td class=\"text-right\">").append(formatMontant(sousTotal)).append("</td>");
                html.append("</tr>");
            }
        }
        
        html.append("</table>");
        
        html.append("<div class=\"separator\"></div>");
        
        // Total
        html.append("<div class=\"total\" style=\"display: flex; justify-content: space-between;\">");
        html.append("<span>TOTAL:</span>");
        html.append("<span>").append(formatMontant(total)).append("</span>");
        html.append("</div>");
        
        html.append("<div class=\"separator\"></div>");
        
        // Notes
        html.append("<div style=\"font-size: 10pt; margin-top: 20px;\">");
        html.append("<div><strong>Note:</strong> Ceci est un proforma, pas une facture.</div>");
        html.append("<div>Validité: 7 jours</div>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        out.println(html.toString());
        
        // Script d'impression
        out.println("<script type=\"text/javascript\">");
        out.println("window.onload = function() {");
        out.println("    window.print();");
        out.println("}");
        out.println("</script>");
    }
}