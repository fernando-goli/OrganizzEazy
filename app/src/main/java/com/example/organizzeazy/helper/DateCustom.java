package com.example.organizzeazy.helper;


import java.text.SimpleDateFormat;

public class DateCustom {

    public  static String dataAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);
        return dataString;
    }

    public static String mounthYearSelect(String data){

        String dataRege[] = data.split("/");
        String dia= dataRege[0];
        String mes = dataRege[1];
        String ano = dataRege[2];

        String mesAno = mes + ano;

        return mesAno;
    }

}
