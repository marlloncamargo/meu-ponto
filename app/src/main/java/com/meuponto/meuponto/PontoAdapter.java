package com.meuponto.meuponto;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Marllon on 8/7/16.
 */
public class PontoAdapter extends BaseAdapter {

    private Context context;
    private List<Ponto> pontoList;

    public PontoAdapter(Context context, List<Ponto> pontoList) {
        this.context = context;
        this.pontoList = pontoList;
    }

    @Override
    public int getCount() {
        return pontoList.size();
    }

    @Override
    public Object getItem(int i) {
        return pontoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Ponto ponto = pontoList.get(i);

        final View layout;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.ponto, null);
        } else{
            layout = view;
        }

        TextView txtData = (TextView) layout.findViewById(R.id.txtData);
        txtData.setText(dateFormat(ponto.getHorario()));
        txtData.setTextColor(Color.BLACK);

        TextView txtTipo = (TextView) layout.findViewById(R.id.txtTipo);
        if (ponto.isEntrada()){
            txtTipo.setText("Entrada");
        } else {
            txtTipo.setText("Saida");
        }
        txtTipo.setTextColor(Color.BLACK);

        TextView txtAlmoco = (TextView) layout.findViewById(R.id.txtAlmoco);
        if (ponto.isAlmoco()){
            txtAlmoco.setText("Almoco");
        }
        txtAlmoco.setTextColor(Color.BLACK);

        return layout;
    }


    private String dateFormat(Date date){
        return new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", new Locale("pt_BR")).format(date);
    }
}
