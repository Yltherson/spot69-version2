package com.spot69.utils;

import com.spot69.model.Permissions;
import com.spot69.model.Utilisateur;

public class PermissionChecker {
    public static boolean hasPermission(Utilisateur user, Permissions permission) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        
        String[] droits = user.getRole().getDroits().split(",");
        for (String droit : droits) {
            if (droit.trim().equals(permission.name())) {
                return true;
            }
        }
        return false;
    }
}