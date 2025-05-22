package com.nutrivda.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class CompartidoViewModel extends ViewModel {

    private final MutableLiveData<Date> fechaSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<String> fechaSeleccionadaString = new MutableLiveData<>();
    private final MutableLiveData<Integer> userId = new MutableLiveData<>();
    //private final MutableLiveData<MiObjeto> objetoSeleccionado = new MutableLiveData<>();

    public void setFecha(Date fecha) {
        fechaSeleccionada.setValue(fecha);
    }

    public LiveData<Date> getFecha() {
        return fechaSeleccionada;
    }

    public void setFechaSeleccionadaString(String fechaString) {
        this.fechaSeleccionadaString.setValue(fechaString);
    }

    public MutableLiveData<String> getFechaSeleccionadaString() {
        return fechaSeleccionadaString;
    }

    public void setUserId(Integer userId) {
        this.userId.setValue(userId);
    }

    public MutableLiveData<Integer> getUserId() {
        return this.userId;
    }

   /* public void setObjeto(MiObjeto obj) {
        objetoSeleccionado.setValue(obj);
    }

    public LiveData<MiObjeto> getObjeto() {
        return objetoSeleccionado;
    }*/
}
