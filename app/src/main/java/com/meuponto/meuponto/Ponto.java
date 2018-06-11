package com.meuponto.meuponto;

import java.util.Date;

/**
 * Created by Marllon on 8/7/16.
 */
public class Ponto extends Usuario{

    private Date horario;
    private boolean entrada;
    private boolean almoco;

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public boolean isEntrada() {
        return entrada;
    }

    public void setEntrada(boolean entrada) {
        this.entrada = entrada;
    }

    public boolean isAlmoco() {
        return almoco;
    }

    public void setAlmoco(boolean almoco) {
        this.almoco = almoco;
    }
}
