package util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangContext {
    public static final StringProperty currentLang=new SimpleStringProperty("en");
    public static final ObjectProperty<ResourceBundle> currentBundle=
            new SimpleObjectProperty<>(ResourceBundle.getBundle("messages",new Locale("en")));
    public static void setLang(String lang){
        currentBundle.set(ResourceBundle.getBundle("messages",new Locale(lang)));
        currentLang.set(lang);
    }

    public static ResourceBundle getBundle(){
        return currentBundle.get();
    }

}
