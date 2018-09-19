package com.photoweb.piiics.model;

import java.io.Serializable;

/**
 * Created by thomas on 27/04/2017.
 * The price it's in cents (it will be divided by 100 for display)
 */

public class ModificationsManager implements Serializable {

    //------------------------------formats------------------------------------
    //formats ID
    private static String FORMAT_STANDARD = "FORMAT_STANDARD";
    private static String FORMAT_SQUARE = "FORMAT_SQUARE";
    private static String FORMAT_PANORAMIC = "FORMAT_PANORAMIC";

    // formats prices
    private static int FORMAT_STANDARD_PRICE = 15;
    private static int FORMAT_SQUARE_PRICE = 29;
    private static int FORMAT_PANORAMIC_PRICE = 29;
    //-------------------------------------------------------------------------


    //------------------------------margins------------------------------------
    //margins ID
    private static String MARGIN_COLOR_NONE = "MARGIN_COLOR_NONE";
    private static String MARGIN_COLOR_SET = "MARGIN_COLOR_SET";

    //margins prices
    private static int MARGIN_COLOR_NONE_PRICE = 0;
    private static int MARGIN_COLOR_SET_PRICE = 0;
    //-------------------------------------------------------------------------

    //------------------------------gabarits-----------------------------------
    private static String GABARIT_NONE = "GABARIT_NONE";
    private static String GABARIT_DEFAULT = "DefaultGabarit";
    private static String GABARIT_ROUNDED_RECT = "RoundedRectGabarit";
    private static String GABARIT_DIAMOND = "DiamondGabarit";
    private static String GABARIT_SQUARE_LEFT = "SquareLeftGabarit";
    private static String GABARIT_ROUND = "RoundGabarit";
    private static String GABARIT_ROTATE = "RotateGabarit";
    private static String GABARIT_REVERSE_ROTATE = "ReverseRotate";
    private static String GABARIT_POLAROID = "PolaroidGabarit";
    private static String GABARIT_PANA_POLAROID = "PanaPolaroidGabarit";
    private static String GABARIT_SQUARE_POLAROID = "SquarePolaroidGabarit";
    private static String GABARIT_MARGIN_BOTTOM = "MarginBottomGabarit";
    private static String GABARIT_FOUR_TIER = "FourTierGabarit";
    private static String GABARIT_THIRD_HALF = "ThirdHalfGabarit";



    //-------------------------------------------------------------------------

    //duplicated price
   // private static int DUPLICATED_PRICE = 15;

    //modification objects
    Modification formatStandard;
    Modification formatSquare;
    Modification formatPanoramic;

    Modification formatPage;

    Modification marginColorNone;
    Modification marginColorSet;

    public ModificationsManager() {
        formatStandard = new Modification(FORMAT_STANDARD, FORMAT_STANDARD_PRICE);
        formatSquare = new Modification(FORMAT_SQUARE, FORMAT_SQUARE_PRICE);
        formatPanoramic = new Modification(FORMAT_PANORAMIC, FORMAT_PANORAMIC_PRICE);

        formatPage = new Modification(FORMAT_STANDARD, FORMAT_STANDARD_PRICE);

        marginColorNone = new Modification(MARGIN_COLOR_NONE, MARGIN_COLOR_NONE_PRICE);
        marginColorSet = new Modification(MARGIN_COLOR_SET, MARGIN_COLOR_SET_PRICE);
    }

    public Modification getFormatSquare() {
        return formatSquare;
    }
    public Modification getFormatPanoramic() {
        return formatPanoramic;
    }
    public Modification getFormatStandard() {
        return formatStandard;
    }
    public Modification getFormatPage() {
        return formatPage;
    }

    public Modification getMarginColorNone() {
        return marginColorNone;
    }
    public Modification getMarginColorSet() {
        return marginColorSet;
    }

    //public int getDuplicatedPrice() { return DUPLICATED_PRICE; }
}
