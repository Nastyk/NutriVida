package com.nutrivda.app.utils;

import java.util.Objects;

public class StringUtil {

    public static boolean isCadenaVacia(String cadena) {
        if (Objects.isNull(cadena)) {
            return true;
        }
        if (Objects.nonNull(cadena)) {
            if (cadena.length() == 0 || cadena.equals("")) {
                return true;
            }
        }
        return false;
    }
}
