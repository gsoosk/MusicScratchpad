package com.viviose.musicscratchpad;

import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by Patrick on 2/3/2016.
 */
public class Note{
    public float x;
    public float y;
    float interval = DensityMetrics.spaceHeight;
    float subInt = interval / 2;
    float subSubInt = subInt / 2;
    NoteName name;
    double rhythm;
    int octave = Octave.octave;
    int accidental;
    NoteName[][] SHARPS = { {NoteName.f, NoteName.fs},
            {NoteName.c, NoteName.cs},
            {NoteName.g, NoteName.gs},
            {NoteName.d, NoteName.ds},
            {NoteName.a, NoteName.as},
            {NoteName.e, NoteName.f},
            {NoteName.b, NoteName.c}};

    NoteName[][] FLATS = {{NoteName.b, NoteName.as},
            {NoteName.e, NoteName.ds},
            {NoteName.a, NoteName.gs},
            {NoteName.d, NoteName.cs},
            {NoteName.g, NoteName.gs},
            {NoteName.c, NoteName.b},
            {NoteName.f, NoteName.e}};
    public enum NoteName{
        c,
        cs,
        d,
        ds,
        e,
        f,
        fs,
        g,
        gs,
        a,
        as,
        b
    }
    private NoteName[] altoStandardNotes =      {NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c};
    private NoteName[] trebleStandardNotes =    {NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b};
    private NoteName[] bassStandardNotes =      {NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d};

    public class NotePosn{
        NoteName noteName;
        float yLower;
        float yUpper;
        int octave;
        public NotePosn(NoteName n, float yL, float yU, int oct){
            noteName = n;
            yLower = yL;
            yUpper = yU;
            octave = oct;
        }
    }

    private ArrayList ALTO_POSN = new ArrayList();

    private ArrayList<NotePosn> getNotePosn(float y, Clef clef){
        NoteName[] iterArray = new NoteName[9];
        ArrayList<NotePosn> res = new ArrayList();
        switch (clef){
            case ALTO:
                iterArray = altoStandardNotes;
                res = ALTO_POSN;
                break;
            case TREBLE:
                iterArray = trebleStandardNotes;
                res = ALTO_POSN;
                Octave.octave = 5;
                break;
            case BASS:
                iterArray = bassStandardNotes;
                res = ALTO_POSN;
                Octave.octave = 4;
        }
        for (int i = 14; i > -1; i--){
            float yL = (interval * 8 - subInt) - subInt * (i + 1);
            float yU = (interval * 8) - subInt * (i + 1);
            int o = octave;
            if (clef == Clef.ALTO){
                if (i < 7){
                    if (i == 0){
                        o = octave - 2;
                    }else {
                        o = octave - 1;
                    }
                }else if (i == 14){
                    o = octave + 1;
                }
            }else if (clef == Clef.TREBLE){
                if (i < 8){

                    o = octave - 1;
                }else if(i == 0){
                    o = octave - 2;
                }
            }else if (clef == Clef.BASS) {
                if (i < 6) {
                    o = octave - 2;
                }else if ( i < 13 && i >= 6){
                    o = octave - 1;
                }

            }

            res.add(new NotePosn(iterArray[i], yL, yU, o));
        }
        return res;
    }

    private NoteName getNoteFromPosn(float y, Clef clef){
        ArrayList<NotePosn> noteList = getNotePosn(y, clef);
        for (int i = 0; i < noteList.size(); i++) {
            if (y > noteList.get(i).yLower && y <= noteList.get(i).yUpper){
                octave = noteList.get(i).octave;
                return noteList.get(i).noteName;
            }
        }
        octave += 1;
        return NoteName.c;
    }



    private NoteName accidental(NoteName note, int accidental){
        if (accidental == 1){
            for (NoteName[] sharpNote: SHARPS){
                if (note == sharpNote[0]){
                    return sharpNote[1];
                }
            }
        }
        if (accidental == -1){
            for (NoteName[] flatNote: FLATS){
                if (note == flatNote[0]){
                    return flatNote[1];
                }
            }
        }
        return note;
    }

    private NoteName keyAccidental(NoteName note){
        if (Key.COUNT == 0){
            return note;
        }
        if (Key.SHARP){

            for (int i = 0; i < Key.COUNT; i++){
                if (note == SHARPS[i][0]){
                    return SHARPS[i][1];
                }
            }
        }
        return note;
    }




    public Note(float xC, float yC, int acc){
        x = xC;
        y = snapNoteY(yC);
        name = accidental(keyAccidental(getNoteFromPosn(snapNoteY(yC) - DensityMetrics.getToolbarHeight(), ClefSetting.clef)), acc);
        rhythm = LastRhythm.value;

    }


//Change this to a rounding statement
    private float snapNoteY(float y) {

        float snapY = 0;
        for (int i = 0; i < 13; i++) {
            if (interval * i - subSubInt < y && interval * i + subSubInt >= y) {
                snapY = interval * i + DensityMetrics.getToolbarHeight();
                break;
            }else if(interval * i + subInt - subSubInt< y&& interval * i + subInt + subSubInt  >= y){
                snapY = interval * i + subInt + DensityMetrics.getToolbarHeight();
                break;
            }
        }
        return snapY;
    }


}
