package unige.coet.beny.speedracer;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectLoader {

    static Data3d load(AssetManager assetManager,String name){
        Pattern pattern;
        Matcher matcher;
        try {
            InputStream fis = assetManager.open(name);
            ObjectInputStream is = new ObjectInputStream(fis);
            Data3d s = null;
            try {
                s = (Data3d) is.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            is.close();
            fis.close();
            return s;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
