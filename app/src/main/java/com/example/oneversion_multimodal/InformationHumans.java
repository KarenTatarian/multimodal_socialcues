package com.example.oneversion_multimodal;

import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.TransformTime;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.object.human.AttentionState;
import com.aldebaran.qi.sdk.object.human.Human;

import java.util.Arrays;
import java.util.List;

public class InformationHumans {
    private static final String TAG = "multi_socialcues";

    Checkpoint_info getting_info(QiContext qiContext){
        List<Human> humans = qiContext.getHumanAwareness().getHumansAround();
        Actuation actuation = qiContext.getActuation();
        Frame robotFrame = actuation.robotFrame();
        int total = humans.size();
        double[] distance_array = new double[total];
        double[] angles_humans_array = new double[total];
        int [] attention_humans_array = new int[total];
        for (int i = 0; i < humans.size(); i++) {
            // Get the human.
            Human human = humans.get(i);
            AttentionState attentionState = human.getAttention();
            int value_attention = attentionState.getQiValue();

            Frame humanFrame = human.getHeadFrame();
            // Compute the distance and compute angle
            double distance = computeDistance(humanFrame, robotFrame);
            double angle = computeAngle(humanFrame, robotFrame);
            distance_array[i] = distance;
            angles_humans_array[i] = angle;
            attention_humans_array[i] = value_attention;

            /*if (value_attention == 1){
                count ++;
                count_array[i] = count ;
            }*/
        }

        return new Checkpoint_info(distance_array,angles_humans_array,attention_humans_array);
    }

    private double computeDistance(Frame humanFrame, Frame robotFrame) {
        // Get the TransformTime between the human frame and the robot frame.
        TransformTime transformTime = humanFrame.computeTransform(robotFrame);
        Transform transform = transformTime.getTransform();
        Vector3 translation = transform.getTranslation();
        // Get the x and y components of the translation.
        double x = translation.getX();
        double y = translation.getY();
        return Math.sqrt(x * x + y * y);
    }

    private double computeAngle(Frame humanFrame, Frame robotFrame) {
        TransformTime transformTime = humanFrame.computeTransform(robotFrame);
        Transform transform = transformTime.getTransform();
        Vector3 translation = transform.getTranslation();
        double x = translation.getX();
        double y = translation.getY();
        // Compute the angle and return it.
        double rad = Math.atan2(x, y);
        return Math.toDegrees(rad);
    }

}

class Checkpoint_info{
    public double[] distances;
    public double[] angles;
    public int[] attention_states;

    public Checkpoint_info(double[] distances, double[] angles, int[] attention_states){
        this.distances= distances;
        this.angles = angles;
        this.attention_states = attention_states;
    }
}
