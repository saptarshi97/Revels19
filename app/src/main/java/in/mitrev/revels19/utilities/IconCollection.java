package in.mitrev.revels19.utilities;


import android.content.Context;
import android.util.Log;

import in.mitrev.revels19.R;

public class IconCollection {


    private final int animania = R.drawable.animania;
    private final int anubhuti = R.drawable.anubhuti;
    private final int crescendo = R.drawable.crescendo;
    private final int dramebaaz = R.drawable.dramebaaz;
    private final int eq_iq = R.drawable.eqiq;
    private final int ergo = R.drawable.ergo;
    private final int film_festival = R.drawable.film_festival;
    private final int footloose = R.drawable.footloose;
    private final int haute_couture = R.drawable.hautecouture;
    private final int human_library = R.drawable.human_library;
    private final int iridescent = R.drawable.iridescent;
    private final int kalakriti = R.drawable.kalakriti;
    private final int lensation = R.drawable.lensation;
    private final int mitdt = R.drawable.mitdt;
    private final int radioactive = R.drawable.radioactive;
    private final int psychus = R.drawable.psychus;
    private final int gaming = R.drawable.gaming;
    private final int xventure = R.drawable.xventure;
    private final int wordsworth = R.drawable.wordsworth;

    String TAG = "IconCollection";

    public IconCollection() {
    }

    public int getIconResource(Context context, String catName) {
        if (catName == null) return R.drawable.revels_logo_trans;

        switch (catName.toLowerCase()) {
            case "crescendo":
                return crescendo;
            case "eq-iq":
                return eq_iq;
            case "lensation":
                return lensation;
            case "dramebaaz":
                return dramebaaz;
            case "footloose":
                return footloose;
            case "iridescent":
                return iridescent;
            case "animania":
                return animania;
            case "anubhuti":
                return anubhuti;
            case "psychus":
                return psychus;
            case "haute couture":
                return haute_couture;
            case "xventure":
                return xventure;
            case "kalakriti":
                return kalakriti;
            case "debate tournament":
                return mitdt;
            case "ergo":
                return ergo;
            case "gaming":
                return gaming;
            case "film festival":
                return film_festival;
            case "human library":
                return human_library;
            case "radioactive":
                return radioactive;
            case "wordsworth":
                return wordsworth;
//            case "sports": return R.mipmap.ic_launcher;


            default: {
                Log.i(TAG, catName);
                return R.drawable.revels_logo_trans;
            }
        }

    }


}