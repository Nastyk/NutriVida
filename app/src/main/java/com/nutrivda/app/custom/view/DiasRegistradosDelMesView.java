package com.nutrivda.app.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiasRegistradosDelMesView extends View {

    private Paint paintBarra;
    private Paint paintTexto;
    private List<Date> fechasCompletadas = new ArrayList<>();;

    public DiasRegistradosDelMesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBarra = new Paint();
        paintBarra.setColor(Color.parseColor("#096C55"));

        paintTexto = new Paint();
        paintTexto.setColor(Color.BLACK);
        paintTexto.setTextSize(28f);
        paintTexto.setAntiAlias(true);
        paintTexto.setTextAlign(Paint.Align.CENTER);
    }

    public void setFechasCompletadas(List<String> fechas) {
        fechasCompletadas = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (String str : fechas) {
            try {
                fechasCompletadas.add(formato.parse(str));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int columnas = 8;
        int anchoColumna = getWidth() / columnas;
        int alturaMax = getHeight() - 80;

        float tamCuadro = alturaMax / 7f;

        List<Date[]> semanas = obtenerSemanasConsecutivas(8);
        SimpleDateFormat formato = new SimpleDateFormat("d/M", Locale.getDefault());

        for (int i = 0; i < semanas.size(); i++) {
            float left = i * anchoColumna + 20f;
            Date inicio = semanas.get(i)[0];
            Date fin = semanas.get(i)[1];

            int diasRegistrados = 0;
            for (Date fecha : fechasCompletadas) {
                if (!fecha.before(inicio) && !fecha.after(fin)) {
                    diasRegistrados++;
                }
            }

            for (int j = 0; j < diasRegistrados; j++) {
                float top = alturaMax - ((j + 1) * tamCuadro);
                canvas.drawRect(left, top, left + anchoColumna - 40f, top + tamCuadro - 5f, paintBarra);
            }

            String fechaInicioStr = formato.format(semanas.get(i)[0]);
            String fechaFinStr = formato.format(semanas.get(i)[1]);

            canvas.drawText(fechaInicioStr, left + anchoColumna / 2f - 20f, getHeight() - 40f, paintTexto);
            canvas.drawText(fechaFinStr, left + anchoColumna / 2f - 20f, getHeight() - 10f, paintTexto);
        }
    }

    private List<Date[]> obtenerSemanasConsecutivas(int cantidadSemanas) {
        List<Date[]> semanas = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < cantidadSemanas; i++) {
            Date inicio = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            Date fin = calendar.getTime();
            semanas.add(new Date[]{inicio, fin});
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return semanas;
    }
}
