package com.spot69.controller;

import com.spot69.dao.UtilisateurDAO;
import com.spot69.model.Utilisateur;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/blok/*")
public class AuthFilter implements Filter {

    private UtilisateurDAO utilisateurDAO;

    public void init(FilterConfig filterConfig) throws ServletException {
        utilisateurDAO = new UtilisateurDAO(); // Instancier le DAO ici
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
    	request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String action = req.getParameter("action");
        String whoislogin = req.getParameter("whoislogin"); // ADM ou CLIENT

        // Autoriser les ressources publiques (à adapter selon ton projet)
//        if (
//            uri.endsWith("/blok/login.jsp") ||
//            (uri.contains("UtilisateurServlet") && "login".equals(action)) ||
//            uri.endsWith("/blok/header.jsp") ||
//            uri.endsWith("/blok/footer.jsp") ||
//            uri.contains("/css/") || uri.contains("/js/") || uri.contains("/image/") || uri.contains("/fonts/")
//        ) {
//            chain.doFilter(request, response);
//            return;
//        }

        HttpSession session = req.getSession(false);
     // Vérifier si la session ou userId est manquant
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");

        Utilisateur user = utilisateurDAO.findById(userId);


        // Accès autorisé
        chain.doFilter(request, response);
    }

    public void destroy() {}

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res, String whoislogin) throws IOException {
        if ("ADM".equals(whoislogin)) {
            res.sendRedirect(req.getContextPath() + "/blok/login.jsp");
        } else {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
}
