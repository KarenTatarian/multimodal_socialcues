package com.example.oneversion_multimodal;

import android.util.Log;

import androidx.annotation.Nullable;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.GoToBuilder;
import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.object.actuation.AttachedFrame;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.FreeFrame;
import com.aldebaran.qi.sdk.object.actuation.GoTo;
import com.aldebaran.qi.sdk.object.actuation.Mapping;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.TransformTime;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.util.FutureUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GreetingNavigation {
    private static final String TAG = "multi_socialcues";

    // Information for turn_yielding .
    private float max_speed = 0.25f; //speed to approach human in
    private double distance_max = 0.85; // max distance to keep between human and robot

    Future<Void> greeting_movement(QiContext qiContext){
        Log.i(TAG, "executing greeting movement mechanism ");

        Future<Void> futureGo = Future.of(null);
        Frame robotFrame = qiContext.getActuation().robotFrame();

        Human toGotoHuman = getClosestHuman(qiContext);
        if (toGotoHuman == null) return Future.of(null);

        Log.i(TAG, "closest human detected at: " + computeDistance(toGotoHuman.getHeadFrame(), robotFrame));
        Log.i(TAG, "Need to move: " + compute_distancedifference(toGotoHuman.getHeadFrame(), qiContext));
        futureGo = futureGo.andThenCompose(ignored -> socialnavig_greeting(toGotoHuman, robotFrame, qiContext));

        return futureGo;
    }

    private Future<Void> socialnavig_greeting(Human human, Frame robotFrame, QiContext qiContext){
        Log.i(TAG, "PROXEMICS:  Social navigation to greeting distance :   ");

        Frame goToFrame = goTo_targetFrame(human, robotFrame, qiContext);
        Long delay_time = 500L;

        // Create a GoTo action.
        Future<Void> goToFuture = GoToBuilder.with(qiContext)
                .withFrame(goToFrame)
                .withMaxSpeed(max_speed)
                .buildAsync()
                .andThenCompose(socialnavig_greeting -> socialnavig_greeting.async().run());

        return goToFuture;
    }

    private Future<Void> continuouslyRun(GoTo goTo, Long delay_time, QiContext qiContext){
        // get initial frame
        FreeFrame initialFrame = qiContext.getMapping().makeFreeFrame();
        Transform transform = TransformBuilder.create().fromXTranslation(0);
        Frame robotFrame = qiContext.getActuation().robotFrame();
        initialFrame.update(robotFrame,transform,0L);
        double togodistance =  computeDistance(getClosestHuman(qiContext).getHeadFrame(), initialFrame.frame());
        Future<Void> future = goTo.async().run();

        future = future.thenCompose(composed -> {
            if (composed.hasError()) {
                Log.e(TAG, "Previous GoTo failed, retrying continuous GoTo with error:  " + composed.getError());
                return FutureUtils.wait(delay_time, TimeUnit.MILLISECONDS)
                        .andThenCompose(andcomposed -> {
                            Log.i(TAG, "Retrying ...");
                            return continuouslyRun(goTo, delay_time, qiContext);
                        });
            } else {
                Log.i(TAG, "Stopping continuous GoTo");
                return composed;
            }
        });
        return future;
    }

    @Nullable
    private Human getClosestHuman(QiContext qiContext){
        List<Human> humans = qiContext.getHumanAwareness().getHumansAround();
        if (humans.isEmpty()) return null;

        final Frame robotFrame = qiContext.getActuation().robotFrame();
        Comparator<Human> comparator = (human1, human2) -> Double.compare(computeDistance(human1.getHeadFrame(), robotFrame), computeDistance(human2.getHeadFrame(), robotFrame));
        return Collections.min(humans, comparator);
    }

    private Frame goTo_targetFrame(Human humanToGoto, Frame robotFrame, QiContext qiContext) {
        Frame humanFrame = humanToGoto.getHeadFrame();

        double distancetogo = compute_distancedifference(humanFrame, qiContext);
        Transform transform_difference = TransformBuilder.create().fromXTranslation(distancetogo);
        //AttachedFrame attachedFrame = humanFrame.makeAttachedFrame(transform_difference);
        // return attachedFrame.frame();
        Mapping mapping = qiContext.getMapping();
        FreeFrame targetFrame = mapping.makeFreeFrame();
        targetFrame.update(robotFrame, transform_difference, 0L);
        return targetFrame.frame();
    }

    private double compute_distancedifference(Frame humanToGotoFrame, QiContext qiContext){
        final Frame robotFrame = qiContext.getActuation().robotFrame();
        double distance_between = computeDistance(humanToGotoFrame, robotFrame);
        double distance_difference;

        if (distance_between > distance_max){
            distance_difference = distance_between - distance_max;
        }else{
            distance_difference = 0.0;
        }
        return distance_difference;
    }
    private double computeDistance(Frame humanFrame, Frame robotFrame) {
        // Get the TransformTime between the human frame and the robot frame.
        TransformTime transformTime = humanFrame.computeTransform(robotFrame);
        Transform transform = transformTime.getTransform();
        Vector3 translation = transform.getTranslation();
        double x = translation.getX();
        double y = translation.getY();
        return Math.sqrt(x * x + y * y);
    }

}
