package com.example.vybe.vibefactory;

import com.example.vybe.R;

public class DisgustVibe implements Vibe {
    private int color;
    private int emoticon;

    /**
     * This Constructor sets the new vibe's color and emoticon
     */
    public DisgustVibe() {
        this.color = R.color.Green;
        this.emoticon = R.drawable.ic_disgusted;
    }

    /**
     * This gets the string value of the Vibe class selected by a user
     * @return
     * The string that describes the Vibe selected
     */
    public String getVibe() {
        return "Disgust";
    }

    @Override
    public int getEmoticon() {
        return this.emoticon;
    }

    @Override
    public int getColor() {
        return this.color;
    }
}
