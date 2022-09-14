package com.example.oneversion_multimodal;

import android.util.Log;

import androidx.annotation.RawRes;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.ConversationStatus;
import com.aldebaran.qi.sdk.util.FutureUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListeningBehavior {
    private static final String TAG = "multi_socialcues";
    private Long durantionBetweenNods = 2000L;

    void behaviorListening(QiContext qiContext) {
        com.example.oneversion_multimodal.ListeningBehavior listeningBehavior = new com.example.oneversion_multimodal.ListeningBehavior();

        ConversationStatus.OnHearingChangedListener listener = hearing -> {
            if (hearing) {
                nodding_whileListening(qiContext);
            } else {
                nodding_whileListening(qiContext).cancel(true);
            }
        };

        // At the beginning
        if (isRobotHearing(qiContext)) {
            nodding_whileListening(qiContext);
        }
        subscribeToIsRobotHearing(qiContext, listener);

        // When don't want to know
        unsubscribeFromIsRobotHearing(qiContext, listener);
    }


    private boolean isRobotHearing(QiContext qiContext) {
        ConversationStatus conversationStatus = qiContext.getConversation().status(qiContext.getRobotContext());
        return conversationStatus.getHearing();
    }

    private void subscribeToIsRobotHearing(QiContext qiContext, ConversationStatus.OnHearingChangedListener listener) {
        ConversationStatus conversationStatus = qiContext.getConversation().status(qiContext.getRobotContext());
        conversationStatus.addOnHearingChangedListener(listener);
    }

    private void unsubscribeFromIsRobotHearing(QiContext qiContext, ConversationStatus.OnHearingChangedListener listener) {
        ConversationStatus conversationStatus = qiContext.getConversation().status(qiContext.getRobotContext());
        conversationStatus.removeOnHearingChangedListener(listener);
    }

    private Future<Void> nodding_whileListening(QiContext qiContext) {
        Log.i(TAG, "Nodding ");
        AtomicBoolean promiseFulfilled = new AtomicBoolean(false);
        Future futureNodding = Future.of(null);

        Future<Void> waitFuture = FutureUtils.wait(durantionBetweenNods, TimeUnit.MILLISECONDS);

        futureNodding = futureNodding.andThenConsume(nod -> {
            build_animation(R.raw.affirmation_a008, qiContext);
        });

        futureNodding = futureNodding.andThenCompose(wait -> waitFuture);
        return futureNodding;
    }


    private void build_animation(@RawRes Integer mimicResource, QiContext qiContext) {
        // Create an animation from the mimic resource.
        Animation animation = AnimationBuilder.with(qiContext)
                .withResources(mimicResource)
                .build();

        // Create an animate action.
        Animate animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build();

        // Run the animate action asynchronously.
        animate.async().run();
    }
}
