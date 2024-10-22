/**
 * Copyright 2012 Grzegorz Oleksy.
 * Zezwala się na używanie, kopiowanie, modyfikowanie i dystrybuowanie tego oprogramowania, 
 * pod warunkiem zachowania powyższej informacji o prawach autorskich 
 * i niniejszej informacji o zezwoleniu.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ean13 {
    
    final static Color cWhite = Color.white;
    final static Color cBlack = Color.black;
    public static byte bars[];
    public static String EAN13;

    private static final byte parityTable[][] = {
        {0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 1, 1},
        {0, 0, 1, 1, 0, 1},
        {0, 0, 1, 1, 1, 0},
        {0, 1, 0, 0, 1, 1},
        {0, 1, 1, 0, 0, 1},
        {0, 1, 1, 1, 0, 0},
        {0, 1, 0, 1, 0, 1},
        {0, 1, 0, 1, 1, 0},
        {0, 1, 1, 0, 1, 0}
    };

    private static final byte right[][] = {
        {1, 1, 1, 0, 0, 1, 0},
        {1, 1, 0, 0, 1, 1, 0},
        {1, 1, 0, 1, 1, 0, 0},
        {1, 0, 0, 0, 0, 1, 0},
        {1, 0, 1, 1, 1, 0, 0},
        {1, 0, 0, 1, 1, 1, 0},
        {1, 0, 1, 0, 0, 0, 0},
        {1, 0, 0, 0, 1, 0, 0},
        {1, 0, 0, 1, 0, 0, 0},
        {1, 1, 1, 0, 1, 0, 0}
    };

    private static final byte left[][][] = {
        {
                {0, 0, 0, 1, 1, 0, 1},
                {0, 0, 1, 1, 0, 0, 1},
                {0, 0, 1, 0, 0, 1, 1},
                {0, 1, 1, 1, 1, 0, 1},
                {0, 1, 0, 0, 0, 1, 1},
                {0, 1, 1, 0, 0, 0, 1},
                {0, 1, 0, 1, 1, 1, 1},
                {0, 1, 1, 1, 0, 1, 1},
                {0, 1, 1, 0, 1, 1, 1},
                {0, 0, 0, 1, 0, 1, 1}
        },
        {
                {0, 1, 0, 0, 1, 1, 1},
                {0, 1, 1, 0, 0, 1, 1},
                {0, 0, 1, 1, 0, 1, 1},
                {0, 1, 0, 0, 0, 0, 1},
                {0, 0, 1, 1, 1, 0, 1},
                {0, 1, 1, 1, 0, 0 ,1},
                {0, 0, 0, 0, 1, 0, 1},
                {0, 0, 1, 0, 0, 0 ,1},
                {0, 0, 0, 1, 0, 0, 1},
                {0, 0, 1, 0, 1, 1, 1}
        }
    };

    public static void settingBars(byte input[], String
            code){
        bars = input;
        EAN13 = code;
    }

    public static byte[] convertToByteArray(byte EAN13[])
    {
        /*
         * Sprawdzamy czy kod można wygenerować
         */
        if ((EAN13 == null) ||
                (EAN13.length != 13) ||
                (checkControlSum(EAN13) == false)) {
            return null;
        }
        /*
         * Definiujemy tablice bitów dla potrzeby  pasków kodu
         */
        byte cbars[] = new byte[95];
        for (int i=0; i<95; i++) {
            cbars[i] = 0;
        }
        /*
         * Początek kodu znacznik bitowy 101
         */
        cbars[0] = 1;
        cbars[1] = 0;
        cbars[2] = 1;

        for (int i=1; i<7; i++)
        {
            for (int j=0; j<7; j++) {
                cbars[(i-1)*7 + 3 + j] =
                        left[parityTable[EAN13[0]][i-
                                1]][EAN13[i]][j];
            }
        }
        /*
         * Wewnętrzny separator kodu podział‚ w postaci 01010
         */
        cbars[45] = 0;
        cbars[46] = 1;
        cbars[47] = 0;
        cbars[48] = 1;
        cbars[49] = 0;
        for (int i=7; i<13; i++) {
            for (int j=0; j<7; j++) {
                cbars[(i-7)*7 + 50 + j] =
                        right[EAN13[i]][j];
            }
        }
        /*
         * Znacznik końca kodu w postaci 101
         */
        cbars[92] = 1;
        cbars[93] = 0;
        cbars[94] = 1;
        return cbars;
    }

    /***
     * Zasadnicza funkcja malowania paskó.
     * @param g
     */
    public static void paintEAN(Graphics g) {
        int length;
        Graphics2D gEAN = (Graphics2D) g;

        gEAN.setPaint(cWhite);
        gEAN.fillRect(0, 0, 222-1, 120-1);
        gEAN.setFont(new Font("Arial", 0, 10));
        gEAN.setPaint(cBlack);

        if (bars != null) {
            for (int i=0; i<bars.length; i++) {
                if ((i >= 0 && i<= 2) ||
                        (i >= 45 && i<= 49) ||
                        (i >= 92 && i<= 94)) {
                    length = 100;
                } else {
                    length = 90;
                }
                if (bars[i] == 1) {
                    gEAN.fillRect(i*2+20, 10, 2, length);
                }
            }
            gEAN.drawString(EAN13.substring(0, 1), 13,
                    110);
            for (int i=1; i<7; i++) {
                gEAN.drawString(EAN13.substring(i, i+1),
                        i*14+16, 110);
            }
            for (int i=7; i<13; i++) {
                gEAN.drawString(EAN13.substring(i, i+1),
                        i*14+26, 110);
            }
        }
    }

    /**
     * Obliczenie sumy kontrolnej EAN13
     * Jedną z ważniejszych cech kodu EAN 13 jest samosprawdzalność
     * Realizujemy ją za pomocą wartości sumy kontrolnej.
     */
    public static boolean checkControlSum(byte EAN13[]) {
        int sum = 1 * EAN13[0] +
                3 * EAN13[1] +

                1 * EAN13[2] +
                3 * EAN13[3] +
                1 * EAN13[4] +
                3 * EAN13[5] +
                1 * EAN13[6] +
                3 * EAN13[7] +
                1 * EAN13[8] +
                3 * EAN13[9] +
                1 * EAN13[10] +
                3 * EAN13[11];
        sum %= 10;
        sum = 10 - sum;
        sum %= 10;
        if (sum == EAN13[12]) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void generuj(String code,String
            filePath) {
        byte input[] = new byte[13];
        byte result[] = null;
        try {
            if (code.length() == 13) {
                for (int i=0; i<13; i++) {
                    input[i] = Byte.parseByte(code.
                            substring(i, i+1));
                }
                result = convertToByteArray(input);
            }
        } catch (Exception e) {
            result = null;
        }
        if (result != null)
        {
            settingBars(result,code);
            System.out.println("Prawidlowy kod EAN");
            BufferedImage image = new BufferedImage(222
                    ,120,BufferedImage.TYPE_INT_RGB);
            Graphics2D g =  image.createGraphics();
            paintEAN(g);
            try
            {
                ImageIO.write(image, "PNG", new File(filePath+code+".png"));
            }
            catch(Exception exc) {
                //W przypadku gdy nasza klasa mogłaby zwrócić zmienną z błędem
                System.out.println(exc.getMessage());
            }
        }
        else {
            //W przypadku gdy nasza klasa mogłaby zwrócić zmienną z błędem
            System.out.println("Newłaściwy kod EAN");
        }
    }
}
