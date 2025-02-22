package com.nutrivda.app.utils;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> fechas;
    private final int color;

    public EventDecorator(HashSet<CalendarDay> fechas) {
        this.fechas = fechas;
        this.color = Color.RED;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return fechas.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(color));
    }
}
