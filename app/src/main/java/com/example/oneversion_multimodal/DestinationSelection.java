package com.example.oneversion_multimodal;

import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class DestinationSelection {

    FullOptions combinations (String destination_type, String mode_transport, TextView berlin_recommend, TextView rome_recommend, TextView corfou_recommend, TextView corse_recommend, TextView londres_recommend, TextView amsterdam_recommend, TextView barcelone_recommend,
             TextView nice_recommend, Button berlin_button, Button rome_button, Button corfou_button, Button corse_button, Button londres_button, Button amsterdam_button, Button barcelone_button, Button nice_button) {
        String[] bothOptions = new String[3];
        String destination_one = "";
        String destination_two = "";
        String recommendation = "";

        Random ran = new Random(); //random function
        int n = ran.nextInt(2); // n will be 0 50% and 1 50% of the time

        if (mode_transport.equals("avion ") && destination_type.equals("la ville ")) {
            destination_one = destination_one + "Premiere option Berlin ";
            destination_two = destination_two + "deuxième option Rome ";
            if (n == 0) {
                recommendation = "Premiere Option Berlin ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,berlin_button,rome_button,berlin_recommend);
            } else {
                recommendation = "deuxième option Rome ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,berlin_button,rome_button,rome_recommend);
            }
        } else if(mode_transport.equals("avion ") && destination_type.equals("la plage ")){
            destination_one = destination_one + "Premiere option Corfou ";
            destination_two = destination_two + "Deuxième option Corse ";
            if (n == 0) {
                recommendation = "Premiere option Corfou  ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,corfou_button,corse_button,corfou_recommend);
            } else {
                recommendation = "deuxième option Corse  ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,corfou_button,corse_button,corse_recommend);

            }
        } else if (mode_transport.equals("train ") && destination_type.equals("la ville ")){
            destination_one = destination_one + "premiere option Londres ";
            destination_two = destination_two + "deuxième option Amsterdam ";
            if (n == 0) {
                recommendation = "Premiere Option Londres ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,londres_button,amsterdam_button,londres_recommend);

            } else {
                recommendation = "deuxième option Amsterdam ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,londres_button,amsterdam_button,amsterdam_recommend);

            }
        } else if (mode_transport.equals("train ") && destination_type.equals("la plage ")){
            destination_one = destination_one + "premiere option Barcelone ";
            destination_two = destination_two + "deuxième option Nice ";
            if (n == 0) {
                recommendation = "Premiere Option Barcelone ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,barcelone_button,nice_button,barcelone_recommend);

            } else {
                recommendation = "deuxième option Nice ";
                bothOptions[0] = destination_one;
                bothOptions[1] = destination_two;
                bothOptions[2] = recommendation;
                return new FullOptions(bothOptions,barcelone_button,nice_button,nice_recommend);
            }
        }else{
            recommendation = "Premiere Option Barcelone ";
            bothOptions[0] = destination_one;
            bothOptions[1] = destination_two;
            bothOptions[2] = recommendation;
            return new FullOptions(bothOptions,barcelone_button, nice_button, barcelone_recommend);
        }
    }
}
class FullOptions {
    public String[] everythingToSay;
    public Button option_one;
    public Button option_two;
    public TextView recommendation_view;

    public FullOptions(String[] everythingToSay, Button option_one, Button option_two, TextView recommendation_view) {
        this.everythingToSay = everythingToSay;
        this.option_one = option_one;
        this.option_two = option_two;
        this.recommendation_view = recommendation_view;
    }
}