package uk.co.sarahjohnston.museoglasgow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sarahjohnston on 17/11/2015.
 */
public class MyUtil {

    public final static int KIBI = 1024;
    public final static int BYTE = 1;
    public final static int KIBIBYTE = KIBI * BYTE;

    /**
     * Private constructor to prevent instantiation
     */
    private MyUtil() {}

    public static String dateConvert(String D){
        //takes date in form 2015-01-01 changes to 1 January 2015
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("d-MMMM-yyyy");
        Date date = null;
        try {
            date = format1.parse(D);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateString = format2.format(date);
        dateString = dateString.replace("-", " ");
        return ((dateString));
    }

}