package com.spot69.controller;

import com.spot69.dao.ChambreDAO;
import com.spot69.model.Chambre;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
@WebServlet({"/ChambreServlet", "/blok/ChambreServlet"})
public class ChambreServlet extends HttpServlet {

    private ChambreDAO chambreDAO;
    private static String UPLOAD_CHAMBRE_DIR = "";

    @Override
    public void init() throws ServletException {
//        UPLOAD_CHAMBRE_DIR = System.getProperty("user.home") + File.separator + "uploads" + File.separator + "chambres";

//    	production
      ServletContext context = getServletContext();
      UPLOAD_CHAMBRE_DIR = context.getInitParameter("PROD_CHAMBRE_UPLOAD_ROOT_DIR");
      chambreDAO = new ChambreDAO();
    }

    private void setupEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action) {
            case "ajouter":
                handleAjouter(request, response);
                break;
            case "modifier":
                handleModifier(request, response);
                break;
            case "supprimer":
                handleSupprimer(request, response);
                break;
            default:
                response.sendRedirect("index.jsp");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupEncoding(request, response);

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("liste-chambres.jsp");
            return;
        }

        switch (action) {
            case "add":
                handleAddPage(request, response);
                break;
            case "edit":
                handleEditPage(request, response);
                break;
            case "lister":
                handleLister(request, response);
                break;
            case "disponibles":
                handleDisponibles(request, response);
                break;
            default:
                response.sendRedirect("liste-chambres.jsp");
                break;
        }
    }

    // ======= Handlers POST =======

    private void handleAjouter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Chambre chambre = extraireChambreDepuisRequest(request);

            // Gestion des images multiples
            List<Part> fileParts = (List<Part>) request.getParts();
            StringBuilder mediaUrls = new StringBuilder();
            
            for (Part filePart : fileParts) {
                if (filePart.getName().equals("media") && filePart.getSize() > 0) {
                    String imageUrl = sauvegarderFichier(request, filePart, "chambres");
                    if (mediaUrls.length() > 0) {
                        mediaUrls.append(",");
                    }
                    mediaUrls.append(imageUrl);
                }
            }
            
            if (mediaUrls.length() == 0) {
                chambre.setMedia("uploads/default/chambre-default.png");
            } else {
                chambre.setMedia(mediaUrls.toString());
            }

            boolean success = chambreDAO.ajouterChambre(chambre);
            if (success) {
                setSuccessNotif(request, "Chambre ajoutée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de l'ajout de la chambre.");
            }
            response.sendRedirect("ChambreServlet?action=add");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ChambreServlet?action=add");
        }
    }

    private void handleModifier(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Chambre chambre = chambreDAO.chercherParId(id);

            if (chambre == null) {
                setErrorNotif(request, "Chambre introuvable.");
                response.sendRedirect("ChambreServlet?action=lister");
                return;
            }

            Chambre updated = extraireChambreDepuisRequest(request);
            String oldMedia = chambre.getMedia();
            updated.setId(id);

            // Gestion des nouvelles images
            List<Part> fileParts = (List<Part>) request.getParts();
            StringBuilder mediaUrls = new StringBuilder();
            boolean hasNewMedia = false;
            
            for (Part filePart : fileParts) {
                if (filePart.getName().equals("media") && filePart.getSize() > 0) {
                    String imageUrl = sauvegarderFichier(request, filePart, "chambres");
                    if (mediaUrls.length() > 0) {
                        mediaUrls.append(",");
                    }
                    mediaUrls.append(imageUrl);
                    hasNewMedia = true;
                }
            }
            
            if (hasNewMedia) {
                updated.setMedia(mediaUrls.toString());
            } else {
                updated.setMedia(oldMedia);
            }

            boolean success = chambreDAO.modifierChambre(updated);
            if (success) {
                setSuccessNotif(request, "Chambre modifiée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la modification de la chambre.");
            }
            response.sendRedirect("ChambreServlet?action=lister");
        } catch (Exception e) {
            e.printStackTrace();
            setErrorNotif(request, "Erreur technique: " + e.getMessage());
            response.sendRedirect("ChambreServlet?action=lister");
        }
    }

    private void handleSupprimer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            HttpSession session = request.getSession(false);
            Integer deletedBy = session != null && session.getAttribute("userId") != null
                    ? (Integer) session.getAttribute("userId")
                    : 0;

            boolean success = chambreDAO.supprimerChambre(id, deletedBy);
            if (success) {
                setSuccessNotif(request, "Chambre supprimée avec succès.");
            } else {
                setErrorNotif(request, "Erreur lors de la suppression de la chambre.");
            }
            response.sendRedirect("ChambreServlet?action=lister");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    // ======= Handlers GET =======

    private void handleAddPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("ajout-chambre.jsp").forward(request, response);
    }

    private void handleEditPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Chambre chambre = chambreDAO.chercherParId(id);
                if (chambre != null) {
                    request.setAttribute("chambre", chambre);
                    request.getRequestDispatcher("modifier-chambre.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Chambre introuvable");
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            }
        } else {
            response.sendRedirect("liste-chambres.jsp");
        }
    }

    private void handleLister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Chambre> chambres = chambreDAO.listerChambres();
        request.setAttribute("chambres", chambres);

        int nbChambresDisponibles = chambreDAO.compterChambresDisponibles();
        int totalChambres = chambreDAO.compterTotalChambres();

        request.setAttribute("nbChambresDisponibles", nbChambresDisponibles);
        request.setAttribute("totalChambres", totalChambres);

        request.getRequestDispatcher("liste-chambres.jsp").forward(request, response);
    }

    private void handleDisponibles(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Chambre> chambresDisponibles = chambreDAO.listerChambresDisponibles();
        request.setAttribute("chambres", chambresDisponibles);
        request.getRequestDispatcher("chambres-disponibles.jsp").forward(request, response);
    }

    // ======= Méthodes utilitaires =======

    private Chambre extraireChambreDepuisRequest(HttpServletRequest request) {
        Chambre chambre = new Chambre();

        // Nom de la chambre
        String nomChambre = request.getParameter("nomChambre");
        if (nomChambre == null || nomChambre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'nomChambre' est requis.");
        }
        chambre.setNomChambre(nomChambre);

        // Description
        String description = request.getParameter("descriptionChambre");
        chambre.setDescriptionChambre(description);

        // Capacité
        String capaciteStr = request.getParameter("capacite");
        if (capaciteStr == null || capaciteStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'capacite' est requis.");
        }
        chambre.setCapacite(Integer.parseInt(capaciteStr));

        // Installations (tableau de checkboxes)
        String[] installationsArray = request.getParameterValues("installations");
        if (installationsArray != null && installationsArray.length > 0) {
            chambre.setInstallationsList(Arrays.asList(installationsArray));
        } else {
            chambre.setInstallations("");
        }

        // Prix - maintenant 4 types de prix
        chambre.setPrixMoment(parseBigDecimal(request.getParameter("prixMoment")));
        chambre.setPrixNuit(parseBigDecimal(request.getParameter("prixNuit")));
        chambre.setPrixJour(parseBigDecimal(request.getParameter("prixJour")));
        chambre.setPrixSejour(parseBigDecimal(request.getParameter("prixSejour")));

        // Disponibilité
        String disponibleStr = request.getParameter("disponible");
        chambre.setDisponible(disponibleStr != null && disponibleStr.equals("true"));

        // Utilisateur ID
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            chambre.setUtilisateurId((int) session.getAttribute("userId"));
        }

        chambre.setCreatedAt(new Timestamp(new Date().getTime()));
        chambre.setUpdatedAt(new Timestamp(new Date().getTime()));

        return chambre;
    }

    // Méthode utilitaire pour parser les BigDecimal
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private void setSuccessNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmSuccesNotif", message);
        request.getSession().setAttribute("toastType", "success");
    }

    private void setErrorNotif(HttpServletRequest request, String message) {
        request.getSession().setAttribute("ToastAdmErrorNotif", message);
        request.getSession().setAttribute("toastType", "error");
    }

    // Upload d'images
    private String sauvegarderFichier(HttpServletRequest request, Part filePart, String dossier) throws IOException {
        String fileName = getFileName(filePart);
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) extension = fileName.substring(i);
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(UPLOAD_CHAMBRE_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fullPath = uploadDir + File.separator + uniqueFileName;
        filePart.write(fullPath);

        return "uploads/" + dossier + "/" + uniqueFileName;
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return null;
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
        }
        return null;
    }
}