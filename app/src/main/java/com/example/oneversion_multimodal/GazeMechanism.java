package com.example.oneversion_multimodal;

import android.util.Log;
import android.util.Pair;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.Promise;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.LookAtBuilder;
import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.FreeFrame;
import com.aldebaran.qi.sdk.object.actuation.LookAt;
import com.aldebaran.qi.sdk.object.actuation.LookAtMovementPolicy;
import com.aldebaran.qi.sdk.object.actuation.Mapping;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.TransformTime;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.util.FutureUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GazeMechanism {
    private static final String TAG = "multi_socialcues";
    private double angleturnyielding_degree = 12.318;
    private double distanceturnyielding_add = 1.3;
    private double zcomponent = -0.45; //z-component to move head upwards
    private double ycomponent =  -0.3; //y-component to move head upwards yet small inclination

    private Long turnyielding_duration = 1200L;
    private Long turntaking_duration = 2500L;
    private Long floorholding_duration = 1500L;

    Output_GazeMechanisms gaze_mechanisms (Human human, Frame robotFrame, Frame gazeFrame, QiContext qiContext){
        Frame humanFrame = human.getHeadFrame();

        Future<Void> futureTurnTaking = turn_taking_func(human, robotFrame, gazeFrame, 1, 0, qiContext);
        Future<Void> futureTurnYielding = turn_yielding_func(human, robotFrame, gazeFrame, 1, 0, qiContext);
        Future<Void> futureFloorHolding = floor_holding_func(human, robotFrame, gazeFrame, 0,1, qiContext);

        return new Output_GazeMechanisms(futureFloorHolding,futureTurnTaking, futureTurnYielding);
    }

    private Future<Void> lookAt(Frame frame, Long durationMillis, QiContext qiContext) {
        Log.i("Test", "lookAt");

        AtomicBoolean promiseFulfilled = new AtomicBoolean(false);

        LookAt lookAt = LookAtBuilder.with(qiContext)
                .withFrame(frame)
                .build();
        lookAt.setPolicy(LookAtMovementPolicy.HEAD_ONLY);
        Future<Void> lookAtFuture = lookAt.async().run();

        Future<Void> waitFuture = FutureUtils.wait(durationMillis, TimeUnit.MILLISECONDS);

        Promise<Void> promise = new Promise<>();

        promise.setOnCancel(ignored -> {
            lookAtFuture.cancel(true);
            waitFuture.cancel(true);

            if (!promiseFulfilled.getAndSet(true)) {
                promise.setCancelled();
            }
        });

        lookAtFuture.thenConsume(future -> {
            if (future.hasError()) {
                waitFuture.cancel(true);

                if (!promiseFulfilled.getAndSet(true)) {
                    promise.setError(future.getErrorMessage());
                }
            }
        });

        waitFuture.andThenConsume(future -> {
            lookAtFuture.cancel(true);

            if (!promiseFulfilled.getAndSet(true)) {
                promise.setValue(null);
            }
        });

        return promise.getFuture();
    }

    private Future<Void> lookAt_sequence(Pair<Human,Long> pair, Frame robotFrame, Frame gazeFrame, float upward_side, float downward_side, float left_side, float right_side, QiContext qiContext){
        Log.i(TAG, "--------- Full action of look At being composed");

        Future<Void> future = Future.of(null);
        Human human = pair.first;
        Long duration = pair.second;
        Frame humanFrame = human.getHeadFrame();
        Frame frame_created = create_frame(humanFrame, robotFrame, gazeFrame, upward_side, downward_side, left_side, right_side, qiContext);
        future = future
                .andThenCompose(ignored -> lookAt(frame_created, duration, qiContext))
                .thenConsume(voidFuture -> {
                    if (voidFuture.hasError()) {
                        Log.e("Bug", "Oups lookAt_sequence", voidFuture.getError());
                    }
                });
        Log.i(TAG, "--------- Full action of look At DONE");
        return future;
    }

    private Frame create_frame(Frame humanFrame, Frame robotFrame, Frame gazeFrame, float upward_side, float downward_side, float left_side, float right_side, QiContext qiContext){

        double distance = computeDistance(humanFrame, robotFrame);
        Log.i(TAG, "human at distance: "+ Double.toString(distance));

        Mapping mapping = qiContext.getMapping();
        FreeFrame targetFrame = mapping.makeFreeFrame();
        // ------ For sideways Head Aversion ----- TURN-YIELDING OR FLOOR HOLDING ------------------------
        if ((upward_side == 0 || downward_side == 0) && (right_side == 1 || left_side == 1)) {
            double[] transform_xy = transformation_coordinates(humanFrame, gazeFrame, angleturnyielding_degree, distanceturnyielding_add, left_side, right_side);
            Vector3 translation = new Vector3(transform_xy[0], transform_xy[1], -0.6);
            Transform transform = TransformBuilder.create().fromTranslation(translation);
            targetFrame.update(humanFrame, transform, 0L);
        }

        // ------ For upwards//downwards Head Aversion ----- TURN-TAKING ------------------------
        if ((upward_side == 1 || downward_side == 1) && (right_side == 0 || left_side == 0)) {
            if(downward_side == 1){
                zcomponent = 0 - zcomponent;
            }
            Vector3 translation = new Vector3(0,ycomponent,zcomponent);
            Transform transform = TransformBuilder.create().fromTranslation(translation);
            targetFrame.update(humanFrame, transform, 0L);
        }


        Log.i(TAG, "------------------------------------------");
        Log.i(TAG, "NEW INFORMATION RELATIVE TO FRAME: " + Double.toString(computeDistance(targetFrame.frame(), robotFrame)));
        return targetFrame.frame();
    }

    private double[] transformation_coordinates(Frame humanFrame, Frame gazeFrame, double rotation_angle, double distance, float right_side,float left_side){
        TransformTime transformTime = humanFrame.computeTransform(gazeFrame);
        Transform transform = transformTime.getTransform();
        Vector3 translation = transform.getTranslation();
        double x = translation.getX();
        double y = translation.getY();
        double coordinates[] = new double[2];
        coordinates[0] = (x*Math.cos(Math.toRadians(rotation_angle))) - y*Math.sin(Math.toRadians(rotation_angle));
        coordinates[1] = (x*Math.sin(Math.toRadians(rotation_angle))) + y*Math.cos(Math.toRadians(rotation_angle));
        Log.i(TAG, "NEW COORDINATES CALCULATED: " + Arrays.toString(coordinates));
        if (left_side == 0 && right_side == 1 ) {
            Log.i("TAG", "right side selected");
            coordinates[0] = coordinates[0] - x - distance*1.0;
            coordinates[1] = coordinates[1] - y + distance/2;
        }else{
            Log.i(TAG, "left side selected");
            coordinates[0] = (x) - coordinates[0] -1.2;
            coordinates[1] = y - coordinates[1];
        }
        Log.i(TAG, "TRANSFORM FILLED WITH: " + Arrays.toString(coordinates));
        return coordinates;
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

    private Future<Void> turn_yielding_func(Human human, Frame robotFrame, Frame gazeFrame, float left_side, float right_side, QiContext qiContext){
        Log.i(TAG, "executing turn_yielding mechanism ");
        Future<Void> future = Future.of(null);


        Pair<Human, Long> pair_humantime = new Pair<>(human, turnyielding_duration);
        future = future.andThenCompose(ignore -> lookAt_sequence(pair_humantime, robotFrame, gazeFrame, 0,0 , left_side, right_side, qiContext))
                .thenConsume( voidFuture -> {
                    if (voidFuture.hasError()) {
                        Log.e("Bug", "Oups problem at turn_yielding", voidFuture.getError());
                    }
                });
        return  future;
    }

    private Future<Void> floor_holding_func(Human human, Frame robotFrame, Frame gazeFrame,float upward_side, float downward_side, QiContext qiContext){
        Log.i(TAG, "executing turn_taking mechanism ");
        Future<Void> future = Future.of(null);

        Pair<Human, Long> pair_humantime = new Pair<>(human, floorholding_duration);
        future = future.andThenCompose(ignore -> lookAt_sequence(pair_humantime, robotFrame, gazeFrame,upward_side,downward_side,0,0, qiContext))
                //.andThenConsume(wait -> waitMilliSeconds(wait_time))
                .thenConsume( voidFuture -> {
                    if (voidFuture.hasError()) {
                        Log.e("Bug", "Oups problem at turn_yielding", voidFuture.getError());
                    }
                });
        return  future;
    }


    private Future<Void> turn_taking_func(Human human,Frame robotFrame, Frame gazeFrame, float upward_side, float downward_side, QiContext qiContext){
        Log.i(TAG, "executing turn_taking mechanism ");
        Future<Void> future = Future.of(null);

        Pair<Human, Long> pair_humantime = new Pair<>(human, turntaking_duration);
        future = future.andThenCompose(ignore -> lookAt_sequence(pair_humantime, robotFrame, gazeFrame,upward_side,downward_side,0,0, qiContext))
                //.andThenConsume(wait -> waitMilliSeconds(wait_time))
                .thenConsume( voidFuture -> {
                    if (voidFuture.hasError()) {
                        Log.e("Bug", "Oups problem at turn_yielding", voidFuture.getError());
                    }
                });
        return  future;
    }
}

class Output_GazeMechanisms {
    public Future<Void> floor_holding ;
    public Future<Void> turn_taking;
    public Future<Void> turn_yielding;

    public Output_GazeMechanisms (Future<Void> floor_holding, Future<Void> turn_taking, Future<Void> turn_yielding){
        this.floor_holding = floor_holding;
        this.turn_taking = turn_taking;
        this.turn_yielding = turn_yielding;
    }
}