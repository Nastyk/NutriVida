package com.nutrivda.app.utils;

import java.util.List;
import java.util.Map;

public class Utilidades {

    // Utilidad para generar update body con nuevo array + calorías
    public static Map<String, Object> prepararCuerpoActualizado(
            String tipoComida, List<Long> nuevaListaIds, int nuevasCalorias) {
        return Map.of(
                tipoComida.toLowerCase(), nuevaListaIds,
                "caloria_" + tipoComida.toLowerCase(), nuevasCalorias);
    }
}
