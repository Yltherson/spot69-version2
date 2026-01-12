package com.spot69.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Générer un hash sécurisé
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Vérifier un mot de passe clair avec le hash stocké
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
