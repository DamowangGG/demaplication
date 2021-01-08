package com.zejian.myapplication.ui.loading.animation;

/**
 * Created by ybq.
 */
public class SpriteFactory {

    public static Sprite create(Style style) {
        Sprite sprite = null;
        switch (style) {

            case CIRCLE:
                sprite = new Circle();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            default:
                break;
        }
        return sprite;
    }


    public enum Style {

        CIRCLE(0),
        FADING_CIRCLE(1);

        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private int value;

        Style(int value) {
            this.value = value;
        }
    }

}