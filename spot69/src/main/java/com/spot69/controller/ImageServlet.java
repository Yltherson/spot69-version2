package com.spot69.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.*;

@WebServlet({"/blok/media/*", "/images/*"})
public class ImageServlet extends HttpServlet {
	/*
	 * private static final long serialVersionUID = 1L;
	 * 
	 * // Chemins d'upload pour plats et catégories private static String
	 * PLAT_UPLOAD_ROOT_DIR =""; private static String CATEGORIE_UPLOAD_ROOT_DIR="";
	 * private static String PRODUIT_UPLOAD_ROOT_DIR=""; private static String
	 * UPLOAD_CHAMBRE_DIR=""; private static String UPLOAD_PROMO_DIR=""; private
	 * static String UPLOAD_VIDEO_DIR=""; private static String
	 * UPLOAD_EVENEMENT_IMAGE_DIR=""; private static String DEFAULT_UPLOAD_DIR="";
	 * 
	 * // private static String UPLOAD_PROMO_DIR=""; // private static String
	 * UPLOAD_VIDEO_DIR=""; //
	 * 
	 * @Override public void init() throws ServletException { // ========== Choisir
	 * l’environnement ==========
	 * 
	 * // Local PLAT_UPLOAD_ROOT_DIR = System.getProperty("user.home") +
	 * File.separator + "uploads" + File.separator + "plats";
	 * CATEGORIE_UPLOAD_ROOT_DIR = System.getProperty("user.home") + File.separator
	 * + "uploads" + File.separator + "categories"; PRODUIT_UPLOAD_ROOT_DIR =
	 * System.getProperty("user.home") + File.separator + "uploads" + File.separator
	 * + "produits"; DEFAULT_UPLOAD_DIR = System.getProperty("user.home") +
	 * File.separator + "uploads" + File.separator + "default"; UPLOAD_CHAMBRE_DIR =
	 * System.getProperty("user.home") + File.separator + "uploads" + File.separator
	 * + "chambres"; UPLOAD_EVENEMENT_IMAGE_DIR = System.getProperty("user.home") +
	 * File.separator + "uploads" + File.separator + "evenements";
	 * 
	 * UPLOAD_PROMO_DIR = System.getProperty("user.home") + File.separator +
	 * "uploads" + File.separator + "promos" + File.separator + "promos";
	 * UPLOAD_VIDEO_DIR = System.getProperty("user.home") + File.separator +
	 * "uploads" + File.separator + "promos";
	 * 
	 * 
	 * // Production // ServletContext context = getServletContext(); //
	 * PLAT_UPLOAD_ROOT_DIR = context.getInitParameter("PROD_PLAT_UPLOAD_ROOT_DIR");
	 * // CATEGORIE_UPLOAD_ROOT_DIR =
	 * context.getInitParameter("PROD_CATEGORIE_UPLOAD_ROOT_DIR"); //
	 * PRODUIT_UPLOAD_ROOT_DIR =
	 * context.getInitParameter("PROD_PRODUIT_UPLOAD_ROOT_DIR"); //
	 * DEFAULT_UPLOAD_DIR = context.getInitParameter("DEFAULT_UPLOAD_DIR"); //
	 * UPLOAD_CHAMBRE_DIR =
	 * context.getInitParameter("PROD_CHAMBRE_UPLOAD_ROOT_DIR"); // UPLOAD_PROMO_DIR
	 * = context.getInitParameter("UPLOAD_PROMO_DIR"); // UPLOAD_VIDEO_DIR =
	 * context.getInitParameter("UPLOAD_VIDEO_DIR"); // UPLOAD_EVENEMENT_IMAGE_DIR =
	 * context.getInitParameter("UPLOAD_EVENEMENT_IMAGE_DIR");
	 * 
	 * // Production // ServletContext context = getServletContext(); //
	 * PLAT_UPLOAD_ROOT_DIR = context.getInitParameter("PROD_PLAT_UPLOAD_ROOT_DIR");
	 * // CATEGORIE_UPLOAD_ROOT_DIR =
	 * context.getInitParameter("PROD_CATEGORIE_UPLOAD_ROOT_DIR"); //
	 * PRODUIT_UPLOAD_ROOT_DIR =
	 * context.getInitParameter("PROD_PRODUIT_UPLOAD_ROOT_DIR"); //
	 * DEFAULT_UPLOAD_DIR = context.getInitParameter("DEFAULT_UPLOAD_DIR"); //
	 * UPLOAD_CHAMBRE_DIR =
	 * context.getInitParameter("PROD_CHAMBRE_UPLOAD_ROOT_DIR"); // UPLOAD_PROMO_DIR
	 * = context.getInitParameter("UPLOAD_PROMO_DIR"); // UPLOAD_VIDEO_DIR =
	 * context.getInitParameter("UPLOAD_VIDEO_DIR");
	 * 
	 * // =============================================
	 * 
	 * System.out.println("INIT ImageServlet:");
	 * System.out.println("platUploadDir = " + PLAT_UPLOAD_ROOT_DIR);
	 * System.out.println("categorieUploadDir = " + CATEGORIE_UPLOAD_ROOT_DIR);
	 * System.out.println("chambreUploadDir = " + UPLOAD_CHAMBRE_DIR);
	 * System.out.println("evenementUploadDir = " + UPLOAD_EVENEMENT_IMAGE_DIR); }
	 */
	private static final long serialVersionUID = 1L;

	// Variables pour les chemins d'upload
	private static String PLAT_UPLOAD_ROOT_DIR = "";
	private static String CATEGORIE_UPLOAD_ROOT_DIR = "";
	private static String PRODUIT_UPLOAD_ROOT_DIR = "";
	private static String UPLOAD_CHAMBRE_DIR = "";
	private static String UPLOAD_PROMO_DIR = "";
	private static String UPLOAD_VIDEO_DIR = "";
	private static String UPLOAD_EVENEMENT_IMAGE_DIR = "";
	private static String DEFAULT_UPLOAD_DIR = "";
	private static String UPLOAD_PROMO_IMAGES_DIR = "";
	private static String UPLOAD_PROMO_VIDEOS_DIR = "";

	@Override
	public void init() throws ServletException {
	    ServletContext context = getServletContext();
	    
	    // ========== APPROCHE UNIFIÉE ==========
	    // 1. D'abord essayer de récupérer depuis les paramètres de contexte
	    // 2. Fallback sur le répertoire utilisateur si non configuré
	    
	    // Déterminer le répertoire racine d'upload
//	    String uploadRoot = context.getInitParameter("UPLOAD_ROOT_DIR");
//	    if (uploadRoot == null || uploadRoot.isEmpty()) {
	        // Fallback au répertoire home en local
//	    String   uploadRoot = System.getProperty("user.home") + File.separator + "uploads";
	    String uploadRoot = context.getInitParameter("UPLOAD_ROOT_DIR");
//	    }
	    
	    // Définir tous les chemins de manière cohérente
	    PLAT_UPLOAD_ROOT_DIR = uploadRoot + File.separator + "plats";
	    CATEGORIE_UPLOAD_ROOT_DIR = uploadRoot + File.separator + "categories";
	    PRODUIT_UPLOAD_ROOT_DIR = uploadRoot + File.separator + "produits";
	    UPLOAD_CHAMBRE_DIR = uploadRoot + File.separator + "chambres";
	    UPLOAD_EVENEMENT_IMAGE_DIR = uploadRoot + File.separator + "evenements";
	    DEFAULT_UPLOAD_DIR = uploadRoot + File.separator + "default";
	    
	    // Chemins pour les promos (avec organisation en sous-dossiers)
	    UPLOAD_PROMO_DIR = uploadRoot + File.separator + "promos";
	    UPLOAD_PROMO_IMAGES_DIR = uploadRoot + File.separator + "promos" + File.separator + "images";
	    UPLOAD_PROMO_VIDEOS_DIR = uploadRoot + File.separator + "promos" + File.separator + "videos";
	    UPLOAD_VIDEO_DIR = uploadRoot + File.separator + "promos"; // Alias pour compatibilité
	    
	    // Créer les répertoires s'ils n'existent pas
	    createDirectories();
	}

	private void createDirectories() {
	    String[] directories = {
	        PLAT_UPLOAD_ROOT_DIR,
	        CATEGORIE_UPLOAD_ROOT_DIR,
	        PRODUIT_UPLOAD_ROOT_DIR,
	        UPLOAD_CHAMBRE_DIR,
	        UPLOAD_EVENEMENT_IMAGE_DIR,
	        DEFAULT_UPLOAD_DIR,
	        UPLOAD_PROMO_DIR,
	        UPLOAD_PROMO_IMAGES_DIR,
	        UPLOAD_PROMO_VIDEOS_DIR
	    };
	    
	    for (String dir : directories) {
	        File directory = new File(dir);
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }
	    }
	}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=====> DÉBUT doGet ImageServlet");

        // Récupère le path après "/blok/images/"
        String imageName = request.getPathInfo();
        System.out.println("ImageServlet - Path info brut : " + imageName);

        if (imageName == null || imageName.equals("/") || imageName.trim().length() <= 1) {
            System.out.println("ImageServlet - Aucun nom de fichier fourni.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nom de fichier manquant");
            return;
        }

        // Nettoie le nom : /abc.webp → abc.webp
        imageName = imageName.substring(1);
        System.out.println("ImageServlet - Nom du fichier demandé : " + imageName);

        // Logique pour déterminer si c'est une image de plat ou de catégorie
        String imagePath = null;

        // Check si c'est une image de plat ou de catégorie en fonction de l'URL
//        if (imageName.startsWith("plats/")) {
//            imagePath = PLAT_UPLOAD_ROOT_DIR + File.separator + imageName.substring("plats/".length());
//        } else if (imageName.startsWith("categories/")) {
//            imagePath = CATEGORIE_UPLOAD_ROOT_DIR + File.separator + imageName.substring("categories/".length());
//        } else if (imageName.startsWith("produits/")) {
//            imagePath = PRODUIT_UPLOAD_ROOT_DIR + File.separator + imageName.substring("produits/".length());
//        } else if (imageName.startsWith("chambres/")) {
//        	imagePath = UPLOAD_CHAMBRE_DIR + File.separator + imageName.substring("chambres/".length());
//        } else if (imageName.startsWith("evenements/")) {
//        	imagePath = UPLOAD_EVENEMENT_IMAGE_DIR + File.separator + imageName.substring("evenements/".length());
//        }else if (imageName.startsWith("default/")) {
//        	imagePath = DEFAULT_UPLOAD_DIR + File.separator + imageName.substring("default/".length());
//        }  else {
//            // Si l'image ne commence pas par plats ou categories, on tente de chercher dans les deux répertoires
//            imagePath = PLAT_UPLOAD_ROOT_DIR + File.separator + imageName;
//            if (!new File(imagePath).exists()) {
//                imagePath = CATEGORIE_UPLOAD_ROOT_DIR + File.separator + imageName;
//                if (!new File(imagePath).exists()) {
//                    imagePath = PRODUIT_UPLOAD_ROOT_DIR + File.separator + imageName;
//                }
//            }
//        }
     // Check si c'est une image de plat ou de catégorie en fonction de l'URL
        if (imageName.startsWith("plats/")) {
            imagePath = PLAT_UPLOAD_ROOT_DIR + File.separator + imageName.substring("plats/".length());
        } else if (imageName.startsWith("categories/")) {
            imagePath = CATEGORIE_UPLOAD_ROOT_DIR + File.separator + imageName.substring("categories/".length());
        } else if (imageName.startsWith("produits/")) {
            imagePath = PRODUIT_UPLOAD_ROOT_DIR + File.separator + imageName.substring("produits/".length());
        } else if (imageName.startsWith("chambres/")) {
            imagePath = UPLOAD_CHAMBRE_DIR + File.separator + imageName.substring("chambres/".length());
        } else if (imageName.startsWith("evenements/")) {
            imagePath = UPLOAD_EVENEMENT_IMAGE_DIR + File.separator + imageName.substring("evenements/".length());
        } else if (imageName.startsWith("default/")) {
            imagePath = DEFAULT_UPLOAD_DIR + File.separator + imageName.substring("default/".length());
        } else if (imageName.startsWith("promos/images/")) {
            // Image de promo
            imagePath = UPLOAD_PROMO_IMAGES_DIR + File.separator + imageName.substring("promos/images/".length());
        } else if (imageName.startsWith("promos/videos/")) {
            // Vidéo de promo
            imagePath = UPLOAD_PROMO_VIDEOS_DIR + File.separator + imageName.substring("promos/videos/".length());
        } else {
            // Pour les noms sans préfixe ou format ancien
            // Construire le chemin en ajoutant le préfixe manquant si nécessaire
            String resolvedPath = resolveWithoutPrefix(imageName);
            imagePath = resolvedPath;
        }
        
        

        if (imagePath == null) {
            System.out.println("ImageServlet - ❌ Type d'image inconnu");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Type d'image inconnu");
            return;
        }

        File imageFile = new File(imagePath);
        System.out.println("ImageServlet - Chemin absolu du fichier : " + imagePath);

        if (!imageFile.exists() || !imageFile.isFile()) {
            System.out.println("ImageServlet - ❌ Fichier introuvable");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Type MIME
        String mimeType = getServletContext().getMimeType(imageFile.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setContentLengthLong(imageFile.length());

        System.out.println("ImageServlet - ✅ MIME : " + mimeType + ", taille : " + imageFile.length());

        // Envoie l'image
        try (FileInputStream fis = new FileInputStream(imageFile);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("ImageServlet - ✅ Fichier envoyé avec succès.");
        } catch (IOException e) {
            System.out.println("ImageServlet - ❌ Erreur pendant l'envoi : " + e.getMessage());
            throw e;
        }

        System.out.println("=====> FIN doGet ImageServlet");
    }
    
 // Méthode auxiliaire pour gérer les chemins sans préfixe
    private String resolveWithoutPrefix(String imageName) {
        // D'abord vérifier si le fichier existe avec un préfixe promos/images/
        String testPath = UPLOAD_PROMO_IMAGES_DIR + File.separator + imageName;
        if (new File(testPath).exists()) {
            return testPath;
        }
        
        // Puis vérifier promos/videos/
        testPath = UPLOAD_PROMO_VIDEOS_DIR + File.separator + imageName;
        if (new File(testPath).exists()) {
            return testPath;
        }
        
        // Puis vérifier autres répertoires
        String[] directories = {
            PLAT_UPLOAD_ROOT_DIR,
            CATEGORIE_UPLOAD_ROOT_DIR,
            PRODUIT_UPLOAD_ROOT_DIR,
            UPLOAD_CHAMBRE_DIR,
            UPLOAD_EVENEMENT_IMAGE_DIR,
            UPLOAD_PROMO_DIR
        };
        
        for (String dir : directories) {
            testPath = dir + File.separator + imageName;
            if (new File(testPath).exists()) {
                return testPath;
            }
        }
        
        // Si rien n'est trouvé, utiliser le répertoire par défaut
        return DEFAULT_UPLOAD_DIR + File.separator + imageName;
    }
}
