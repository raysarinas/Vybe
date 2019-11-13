package com.example.vybe;

import com.example.vybe.vibefactory.AngryVibe;
import com.example.vybe.vibefactory.DisgustedVibe;
import com.example.vybe.vibefactory.HappyVibe;
import com.example.vybe.vibefactory.SadVibe;
import com.example.vybe.vibefactory.ScaredVibe;
import com.example.vybe.vibefactory.SurprisedVibe;
import com.example.vybe.vibefactory.Vibe;

import java.io.Serializable;

/**
 * A factory class that generates instances of different implementations of
 * the Vibe interface depending on the user selected implementation. The
 * generated implementation is chosen based on the vibe selected by the user
 * and returns the appropriate Vibe object
 */
public class VibeFactory implements Serializable {

    /**
     * Returns the vibe object the user has selected
     * @param selectedVibe
     *      This is the string representation of the user selected vibe
     * @return
     *      Get an appropriate implementation and instance of a Vibe object
     */
    public static Vibe getVibe(String selectedVibe) {
        selectedVibe = selectedVibe.toLowerCase();

        if (selectedVibe.equals("angry")) {
            return new AngryVibe();
        }
        else if (selectedVibe.equals("disgusted")) {
            return new DisgustedVibe();
        }
        else if (selectedVibe.equals("scared")) {
            return new ScaredVibe();
        }
        else if (selectedVibe.equals("happy")) {
            return new HappyVibe();
        }
        else if (selectedVibe.equals("sad")) {
            return new SadVibe();
        }
        else if (selectedVibe.equals("surprised")) {
            return new SurprisedVibe();
        }

        return null;
    }
}
