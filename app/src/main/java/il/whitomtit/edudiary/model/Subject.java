package il.whitomtit.edudiary.model;

import android.graphics.Color;

public class Subject {
    private String name;
    private String color;
    private int image;

    public Subject(String name, String color, int icon) {
        this.name = name;
        this.color = color;
        this.image = icon;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return Color.parseColor(this.color);
    }

    public int getImage() {
        return this.image;
    }

}
