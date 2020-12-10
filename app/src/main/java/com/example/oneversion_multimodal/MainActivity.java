package com.example.oneversion_multimodal;

/* ============================================================
Author: Karen Tatarian
================================================================== */

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Promise;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.HolderBuilder;
import com.aldebaran.qi.sdk.builder.LookAtBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TakePictureBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.localization.Localization;
import com.aldebaran.qi.sdk.localization.Place;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.LookAt;
import com.aldebaran.qi.sdk.object.actuation.LookAtMovementPolicy;
import com.aldebaran.qi.sdk.object.autonomousabilities.DegreeOfFreedom;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionImportance;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionValidity;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.BookmarkStatus;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Chatbot;
import com.aldebaran.qi.sdk.object.conversation.ConversationStatus;
import com.aldebaran.qi.sdk.object.conversation.EditablePhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.holder.Holder;
import com.aldebaran.qi.sdk.object.human.AttentionState;
import com.aldebaran.qi.sdk.object.human.Human;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.util.FutureUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private static final String TAG = "multi_socialcues";

    private QiContext qiContext;

    private QiChatbot qiChatbot;
    private Chat chat;

    private EditablePhraseSet combination_one;
    private EditablePhraseSet combination_two;
    private EditablePhraseSet recommendation;
    private EditablePhraseSet location;
    private EditablePhraseSet transportation;
    private EditablePhraseSet company;

    private String mode_transport;
    private String destination_type;
    private String type_traveller;

    // Bookmarks
    private Bookmark startBookmark;
    private Bookmark part_twoBookmark;
    private Bookmark part_twoNextBookmark;
    private Bookmark part_twoOKAYBookmark;
    private Bookmark part_lengthBookmark;
    private Bookmark part_threeBookmark;
    private Bookmark part_fourTrainBookmark;
    private Bookmark part_fourPlaneBookmark;
    private Bookmark part_fourExtraBookmark;
    private Bookmark part_fiveBookmark;
    private Bookmark part_fiveExtraBookmark;
    private Bookmark part_sixBookmark;
    private Bookmark part_sevenBookmark;
    private Bookmark greetingNavigationBookmark;

    //Bookmark Status
    private BookmarkStatus greetingNavigationBookmarkStatus;
    private BookmarkStatus righthandUP_BookmarkStatus;
    private BookmarkStatus pointmyself_BookmarkStatus;
    private BookmarkStatus pointYou_BookmarkStatus;
    private BookmarkStatus pointObject_BookmarkStatus;
    private BookmarkStatus  turn_yielding_BookmarkStatus;
    private BookmarkStatus  turn_taking_BookmarkStatus;
    private BookmarkStatus floor_holding_BookmarkStatus;
    private BookmarkStatus endPartFiveBookmarkStatus;

    // Buttons
    private Button start_button;
    private Button family_button;
    private Button friends_button;
    private Button city_button;
    private Button beach_button;
    private Button plane_button;
    private Button train_button;
    private Button result_button;
    private Button okay_button;
    private Button oneweek_button;
    private Button oneweekplus_button;
    private Button logement_button;
    private Button activity_button;
    private Button berlin_button;
    private Button rome_button;
    private Button corfou_button;
    private Button corse_button;
    private Button londres_button;
    private Button amsterdam_button;
    private Button barcelone_button;
    private Button nice_button;

    // Textview
    private TextView berlin_recommend;
    private TextView rome_recommend;
    private TextView corfou_recommend;
    private TextView corse_recommend;
    private TextView londres_recommend;
    private TextView amsterdam_recommend;
    private TextView barcelone_recommend;
    private TextView nice_recommend;
    private TextView listentab;
    private TextView compilingTab;

    private HumanAwareness humanAwareness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE);
        setContentView(R.layout.activity_main);

        listentab = (TextView) findViewById(R.id.listenText);
        listentab.setVisibility(TextView.GONE);

        compilingTab = (TextView) findViewById(R.id.compilingText);
        compilingTab.setVisibility(TextView.GONE);

        okay_button = (Button) findViewById(R.id.okayTest);
        okay_button.setVisibility(Button.GONE);
        okay_button.setOnClickListener(v ->{
            Log.i(TAG, "okay button clicked");
            qiChatbot.async().goToBookmark(part_twoOKAYBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            okay_button.setVisibility(Button.GONE);
        });

        start_button = (Button) findViewById(R.id.start_button) ;
        start_button.setVisibility(Button.GONE);
        start_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Start button clicked " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_twoBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            start_button.setVisibility(Button.GONE);
        });

        family_button = (Button) findViewById(R.id.family);
        family_button.setVisibility(Button.GONE);
        family_button.setOnClickListener(v -> {
            Log.i(TAG,"Family/couple chosen");
            friends_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_lengthBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            family_button.setVisibility(Button.GONE);
            type_traveller = "En famille ou en couple";
        });

        friends_button = (Button) findViewById(R.id.friends);
        friends_button.setVisibility(Button.GONE);
        friends_button.setOnClickListener(v -> {
            Log.i(TAG,"By yourself/friends chosen");
            friends_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_lengthBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            family_button.setVisibility(Button.GONE);
            type_traveller = "Tout seul ou avec des ami";
        });

        oneweek_button = (Button) findViewById(R.id.oneweek);
        oneweek_button.setVisibility(Button.GONE);
        oneweek_button.setOnClickListener(v -> {
            Log.i(TAG, "one week or less chosen");
            oneweek_button.setVisibility(Button.GONE);
            oneweekplus_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_threeBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
        });

        oneweekplus_button = (Button) findViewById(R.id.oneweek_plus);
        oneweekplus_button.setVisibility(Button.GONE);
        oneweekplus_button.setOnClickListener(v -> {
            Log.i(TAG, "more than one week was chosen");
            oneweek_button.setVisibility(Button.GONE);
            oneweekplus_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_threeBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
        });

        train_button =(Button) findViewById(R.id.train);
        train_button.setVisibility(Button.GONE);
        train_button.setOnClickListener(v -> {
            Log.i(TAG,"Train chosen");
            plane_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fourTrainBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            train_button.setVisibility(Button.GONE);
            mode_transport = "train ";
        });

        plane_button = (Button) findViewById(R.id.plane);
        plane_button.setVisibility(Button.GONE);
        plane_button.setOnClickListener(v -> {
            Log.i(TAG,"Plane chosen");
            train_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fourPlaneBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            plane_button.setVisibility(Button.GONE);
            mode_transport = "avion ";
        });


        city_button = (Button) findViewById(R.id.city);
        city_button.setVisibility(Button.GONE);
        city_button.setOnClickListener(v -> {
            Log.i(TAG,"City chosen");
            destination_type = "la ville ";
            final String destination_add = destination_type;
            addToDynamic(location,destination_add);
            beach_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fourExtraBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            city_button.setVisibility(Button.GONE);
            //removeFromDynamic(location,destination_add);
        });

        beach_button =(Button) findViewById(R.id.beach);
        beach_button.setVisibility(Button.GONE);
        beach_button.setOnClickListener(v -> {
            Log.i(TAG,"Beach chosen");
            destination_type = "la plage ";
            final String destination_add = destination_type;
            addToDynamic(location, destination_add);
            city_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fourExtraBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            beach_button.setVisibility(Button.GONE);
            //removeFromDynamic(location,destination_add);
        });

        logement_button = (Button) findViewById(R.id.logement);
        logement_button.setVisibility(Button.GONE);
        logement_button.setOnClickListener(v -> {
            Log.i(TAG, "LOGEMENT chosen");
            logement_button.setVisibility(Button.GONE);
            activity_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fiveBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
        });

        activity_button = (Button) findViewById(R.id.activity);
        activity_button.setVisibility(Button.GONE);
        activity_button.setOnClickListener(v -> {
            Log.i(TAG, "ACTIVITY chosen");
            logement_button.setVisibility(Button.GONE);
            activity_button.setVisibility(Button.GONE);
            qiChatbot.async().goToBookmark(part_fiveBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
        });


        result_button = (Button) findViewById(R.id.result);
        result_button.setVisibility(Button.GONE);
        result_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "result button clicked 11 at " + Long.toString(startTime));

            listentab.setVisibility(TextView.GONE);
            compilingTab.setVisibility(TextView.GONE);

            DestinationSelection destinationSelection = new DestinationSelection();
            String[] both = destinationSelection.combinations(destination_type,mode_transport,berlin_recommend,rome_recommend,corfou_recommend,corse_recommend,londres_recommend
                    ,amsterdam_recommend,barcelone_recommend,nice_recommend,berlin_button,rome_button,corfou_button,corse_button,londres_button,amsterdam_button,barcelone_button,nice_button).everythingToSay;
            final String first_option = both[0];
            final String second_option = both[1];
            final String recommended = both[2];

            addToDynamic(combination_one, first_option);
            addToDynamic(combination_two, second_option);
            addToDynamic(recommendation, recommended);
            final String destination = destination_type;
            final String transport = mode_transport;
            final String traveller = type_traveller;
            addToDynamic(location, destination);
            addToDynamic(transportation, transport);
            addToDynamic(company, traveller);

            Log.i(TAG, "robot will recommend: " + recommended);

            qiChatbot.async().goToBookmark(part_sixBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);

            /*removeFromDynamic(combination_one, both[0]);
            removeFromDynamic(combination_two, both[1]);
            removeFromDynamic(recommendation, both[2]);
            removeFromDynamic(location, destination_type);
            removeFromDynamic(transportation, mode_transport);
            removeFromDynamic(company, type_traveller);*/
            result_button.setVisibility(Button.GONE);

        });

        berlin_recommend =(TextView) findViewById(R.id.berlin_recommend);
        berlin_recommend.setVisibility(TextView.GONE);
        rome_recommend =(TextView) findViewById(R.id.rome_recommend);
        rome_recommend.setVisibility(TextView.GONE);
        corfou_recommend =(TextView) findViewById(R.id.corfou_recommend);
        corfou_recommend.setVisibility(TextView.GONE);
        corse_recommend =(TextView) findViewById(R.id.corse_recommend);
        corse_recommend.setVisibility(TextView.GONE);
        londres_recommend =(TextView) findViewById(R.id.londre_recommend);
        londres_recommend.setVisibility(TextView.GONE);
        amsterdam_recommend =(TextView) findViewById(R.id.amsterdam_recommend);
        amsterdam_recommend.setVisibility(TextView.GONE);
        barcelone_recommend =(TextView) findViewById(R.id.barcelone_recommend);
        barcelone_recommend.setVisibility(TextView.GONE);
        nice_recommend =(TextView) findViewById(R.id.nice_recommend);
        nice_recommend.setVisibility(TextView.GONE);

        berlin_button = (Button) findViewById(R.id.berlin_button);
        berlin_button.setVisibility(Button.GONE);
        berlin_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Berlin button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            berlin_button.setVisibility(Button.GONE);
            rome_button.setVisibility(Button.GONE);
            berlin_recommend.setVisibility(TextView.GONE);
            rome_recommend.setVisibility(TextView.GONE);
        });

        rome_button = (Button) findViewById(R.id.rome_button);
        rome_button.setVisibility(Button.GONE);
        rome_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Rome button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            berlin_button.setVisibility(Button.GONE);
            rome_button.setVisibility(Button.GONE);
            berlin_recommend.setVisibility(TextView.GONE);
            rome_recommend.setVisibility(TextView.GONE);
        });

        corfou_button = (Button) findViewById(R.id.corfou_button);
        corfou_button.setVisibility(Button.GONE);
        corfou_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Corfou button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            corse_button.setVisibility(Button.GONE);
            corfou_button.setVisibility(Button.GONE);
            corfou_recommend.setVisibility(TextView.GONE);
            corse_recommend.setVisibility(TextView.GONE);
        });

        corse_button = (Button) findViewById(R.id.corse_button);
        corse_button.setVisibility(Button.GONE);
        corse_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Corse button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            corse_button.setVisibility(Button.GONE);
            corfou_button.setVisibility(Button.GONE);
            corse_recommend.setVisibility(TextView.GONE);
            corfou_recommend.setVisibility(TextView.GONE);
        });

        londres_button = (Button) findViewById(R.id.londres_button);
        londres_button.setVisibility(Button.GONE);
        londres_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "London button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            londres_button.setVisibility(Button.GONE);
            amsterdam_button.setVisibility(Button.GONE);
            amsterdam_recommend.setVisibility(TextView.GONE);
            londres_recommend.setVisibility(TextView.GONE);
        });

        amsterdam_button = (Button) findViewById(R.id.amsterdam_button);
        amsterdam_button.setVisibility(Button.GONE);
        amsterdam_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Amsterdam button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            londres_button.setVisibility(Button.GONE);
            amsterdam_button.setVisibility(Button.GONE);
            amsterdam_recommend.setVisibility(TextView.GONE);
            londres_recommend.setVisibility(TextView.GONE);
        });


        barcelone_button = (Button) findViewById(R.id.barcelone_button);
        barcelone_button.setVisibility(Button.GONE);
        barcelone_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Barcelona button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            barcelone_button.setVisibility(Button.GONE);
            nice_button.setVisibility(Button.GONE);
            barcelone_recommend.setVisibility(TextView.GONE);
            nice_recommend.setVisibility(TextView.GONE);
        });

        nice_button = (Button) findViewById(R.id.nice_button);
        nice_button.setVisibility(Button.GONE);
        nice_button.setOnClickListener(v -> {
            long startTime = System.currentTimeMillis();
            Log.i(TAG, "Nice button was selected " + Long.toString(startTime));
            qiChatbot.async().goToBookmark(part_sevenBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            nice_button.setVisibility(Button.GONE);
            barcelone_button.setVisibility(Button.GONE);
            barcelone_recommend.setVisibility(TextView.GONE);
            nice_recommend.setVisibility(TextView.GONE);
        });

        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i(TAG, "onRobotFocusGained === Everything");
        //Localization of robot
        Localization.async().requestPlace(qiContext).andThenConsume(place -> {
            // The Place is nullable, don't forget to null-check it.
            if (place != null) {
                // Here you can use your Place.
                // Don't forget to get the qiContext from onRobotFocusGained.
                place.async().getExplorationMap(qiContext).andThenConsume(explorationMap -> {
                    // Here you can use your ExplorationMap.
                });
            } else {
                Log.d("Test", "No Place found.");
            }
        });
        
        //=================================================================
        continuouslyRunInfo(qiContext);
        //=================================================================

        Topic topic = TopicBuilder.with(qiContext)
                .withResource(R.raw.script)
                .build();

        qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topic)
                .build();

        //Setting Bookmarks
        Map<String, Bookmark> bookmarks = topic.getBookmarks();
        startBookmark = bookmarks.get("start");
        part_twoBookmark = bookmarks.get("part_two");
        part_twoNextBookmark = bookmarks.get("part_twoNext");
        part_twoOKAYBookmark = bookmarks.get("part_twoOKAY");
        part_lengthBookmark = bookmarks.get("part_length");
        part_threeBookmark = bookmarks.get("part_three");
        part_fourTrainBookmark = bookmarks.get("part_fourTrain");
        part_fourPlaneBookmark = bookmarks.get("part_fourPlane");
        part_fourExtraBookmark = bookmarks.get("part_fourExtra");
        part_fiveBookmark = bookmarks.get("part_five");
        part_fiveExtraBookmark = bookmarks.get("part_fiveExtra");
        part_sixBookmark = bookmarks.get("part_six");
        part_sevenBookmark = bookmarks.get("part_seven");
        greetingNavigationBookmark = bookmarks.get("greeting_navigation");


        Bookmark right_hand = bookmarks.get("hand_one");
        Bookmark pointYou = bookmarks.get("point_you");
        Bookmark point_self = bookmarks.get("point_self");
        Bookmark pointObject = bookmarks.get("point_object");
        Bookmark turn_yielding = bookmarks.get("turn_yielding");
        Bookmark turn_taking = bookmarks.get("turn_taking");
        Bookmark floor_holding = bookmarks.get("floor_holding");
        Bookmark listening = bookmarks.get("endPartFive");

        Map<String, QiChatExecutor> executors = new HashMap<>();
        // Map the executor name from the topic to our qiChatExecutor
        executors.put("myExecutor", new MyQiChatExecutor(qiContext));

        // Set the executors to the qiChatbot
        qiChatbot.setExecutors(executors);
        List<Chatbot> chatbots = new ArrayList<>();
        chatbots.add(qiChatbot);

        // Run the Chat action asynchronously.
        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        // Connecting with the qiChatbot
        greetingNavigationBookmarkStatus = qiChatbot.bookmarkStatus(greetingNavigationBookmark);
        righthandUP_BookmarkStatus = qiChatbot.bookmarkStatus(right_hand);
        pointYou_BookmarkStatus = qiChatbot.bookmarkStatus(pointYou);
        pointmyself_BookmarkStatus = qiChatbot.bookmarkStatus(point_self);
        pointObject_BookmarkStatus = qiChatbot.bookmarkStatus(pointObject);
        endPartFiveBookmarkStatus = qiChatbot.bookmarkStatus(listening);
        turn_taking_BookmarkStatus = qiChatbot.bookmarkStatus(turn_taking);
        turn_yielding_BookmarkStatus = qiChatbot.bookmarkStatus(turn_yielding);
        floor_holding_BookmarkStatus = qiChatbot.bookmarkStatus(floor_holding);

        chat.addOnStartedListener(this::sayProposal);

        greetingNavigationBookmarkStatus.addOnReachedListener(() -> getGreetingNavigation(qiContext) );
        righthandUP_BookmarkStatus.addOnReachedListener(() -> righthand_animation(qiContext));
        pointmyself_BookmarkStatus.addOnReachedListener(() -> pointMyself_animation(qiContext));
        pointObject_BookmarkStatus.addOnReachedListener(() -> point_animation(qiContext));
        pointYou_BookmarkStatus.addOnReachedListener(() -> pointyou_rightArm(qiContext));
        turn_yielding_BookmarkStatus.addOnReachedListener(() -> getTurnYielding(qiContext));
        turn_taking_BookmarkStatus.addOnReachedListener(() -> getTurnTaking(qiContext));
        floor_holding_BookmarkStatus.addOnReachedListener(() -> getFloorHolding(qiContext));
        endPartFiveBookmarkStatus.addOnReachedListener(() -> fullListen(qiContext));

        combination_one = qiChatbot.dynamicConcept("combination_partone");
        combination_two = qiChatbot.dynamicConcept("combination_parttwo");
        recommendation = qiChatbot.dynamicConcept("recommendation");
        location = qiChatbot.dynamicConcept("location");
        transportation = qiChatbot.dynamicConcept("transportation");
        company = qiChatbot.dynamicConcept("company");


        chat.async().run();
    }

    @Override
    public void onRobotFocusLost() {
       /* if (conversationStatus != null) {
            conversationStatus.removeOnHearingChangedListener(listener);
        }*/
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }

    private void sayProposal() {
        qiChatbot.goToBookmark(startBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
    }

    // Gathering of information and characteristics about human
    private Future<Void> continuouslyRunInfo(QiContext qiContext){
        Future<Void> waitFuture = FutureUtils.wait(4000L, TimeUnit.MILLISECONDS);

        waitFuture = waitFuture.andThenConsume(v ->{
            InformationHumans informationHumans = new InformationHumans();
            Log.i(TAG, "=====================================-");
            Log.i(TAG, "DISTANCES: " + Arrays.toString(informationHumans.getting_info(qiContext).distances));
            Log.i(TAG , "ANGLES: " + Arrays.toString(informationHumans.getting_info(qiContext).angles));
            Log.i(TAG, "ATTENTION: " + Arrays.toString(informationHumans.getting_info(qiContext).attention_states));
            Log.i(TAG, "=====================================-");
        }).andThenCompose(again ->{
            return continuouslyRunInfo(qiContext);
        });
        return waitFuture;
    }

    private Future<Void> getGreetingNavigation(QiContext qiContext){
        GreetingNavigation greetingNavigation = new GreetingNavigation();

        return greetingNavigation.greeting_movement(qiContext).thenConsume(v ->
                runOnUiThread(() -> {
                    start_button.setVisibility(Button.VISIBLE);
                    long startTime = System.currentTimeMillis();
                    Log.i(TAG, "Start button VISIBLE " + Long.toString(startTime));
                    //start_button.setEnabled(true);
                }))
                .thenConsume(voidFuture ->{
                    if (voidFuture.hasError()) {
                        Log.e("Bug", "Oups GREETING_MOVEMENT ERROR: ", voidFuture.getError());
                    }
                });
    }

    private Future<Void> gettingHold(QiContext qiContext){
        Log.i(TAG, "Holding rotation of pepper");
        // Build the holder for the degree of freedom.
        Holder holder = HolderBuilder.with(qiContext)
                .withDegreesOfFreedom(DegreeOfFreedom.ROBOT_FRAME_ROTATION)
                .build();

        // Hold the ability asynchronously.
        Future<Void> futureHolder = holder.async().hold();

        return futureHolder;
    }

    private Future<Void> getTurnTaking(QiContext qiContext){
        List<Human> humans = qiContext.getHumanAwareness().getHumansAround();
        Human human = humans.get(0); //for now taking the first human ,, assuming one-on-one interaction
        Actuation actuation = qiContext.getActuation();
        Frame robotFrame = actuation.robotFrame();
        Frame gazeFrame = actuation.gazeFrame();

        GazeMechanism gazeMechanism = new GazeMechanism();
        return gazeMechanism.gaze_mechanisms(human,robotFrame,gazeFrame,qiContext).turn_taking;
    }

    private Future<Void> getTurnYielding(QiContext qiContext){
        List<Human> humans = qiContext.getHumanAwareness().getHumansAround();
        Human human = humans.get(0); //for now taking the first human
        Actuation actuation = qiContext.getActuation();
        Frame robotFrame = actuation.robotFrame();
        Frame gazeFrame = actuation.gazeFrame();

        GazeMechanism gazeMechanism = new GazeMechanism();
        return gazeMechanism.gaze_mechanisms(human,robotFrame,gazeFrame,qiContext).turn_yielding;
    }

    private Future<Void> getFloorHolding(QiContext qiContext){
        List<Human> humans = qiContext.getHumanAwareness().getHumansAround();
        Human human = humans.get(0); //for now taking the first human
        Actuation actuation = qiContext.getActuation();
        Frame robotFrame = actuation.robotFrame();
        Frame gazeFrame = actuation.gazeFrame();

        GazeMechanism gazeMechanism = new GazeMechanism();
        return gazeMechanism.gaze_mechanisms(human,robotFrame,gazeFrame,qiContext).floor_holding;
    }

    // Calling the Animations
    private void wave_animation(QiContext qiContext) {
        Log.i(TAG, "waving");
        build_animation(R.raw.hello_a010, qiContext);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // other-pointing animation/gesture
    private void pointyou_rightArm(QiContext qiContext){
        Log.i(TAG, "point you with right arm animation ");
        build_animation(R.raw.animation_you, qiContext);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // two choice animation/gesture
    private void righthand_animation(QiContext qiContext){
        Log.i(TAG, "Both arms up for this or this gesture");
        build_animation(R.raw.animation_00,qiContext);
    }
    // point animation/gesture with joint attention gaze
    private void point_animation(QiContext qiContext){
        Log.i(TAG, "Point animation: right arm up");
        build_animation(R.raw.animation_point,qiContext);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //self-pointing gesture
    private void pointMyself_animation (QiContext qiContext){
        Log.i(TAG, "Pointing at myself animation ");
        build_animation(R.raw.animation_myself,qiContext);
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

    private void addToDynamic (EditablePhraseSet dynamic,final String addition ){
        Log.i(TAG, "adding to dynamics.");
        dynamic.async().addPhrases(Collections.singletonList(new Phrase(addition)));
    }

    private void removeFromDynamic (EditablePhraseSet dynamic, final String addition){
        Log.i(TAG, "removing from dynamics");
        dynamic.async().removePhrases(Collections.singletonList(new Phrase(addition)));
    }

    // EDITING THE UI & executes
    class MyQiChatExecutor extends BaseQiChatExecutor {
        private final QiContext qiContext;
        MyQiChatExecutor(QiContext context){
            super(context);
            this.qiContext = context;
        }

        @Override
        public void runWith(List<String> params){
            Log.i(TAG, "inside executor");

            if(params.get(0).equals("wave")){
                Log.i(TAG, "Execute wave animation");
                wave_animation(qiContext);
            }
            if(params.get(0).equals("okaytime")){
                Log.i(TAG, "okay suggestion appearing");
                runOnUiThread(()->{
                    findViewById(R.id.main_layout).setBackground(null);
                    okay_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartTwo")){
                Log.i(TAG, "End of Part_two");
                runOnUiThread(() -> {
                    family_button.setVisibility(Button.VISIBLE);
                    friends_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("goToOKAY")){
                Log.i(TAG, "going to Okay part of part two");
                qiChatbot.async().goToBookmark(part_twoNextBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            }
            if(params.get(0).equals("endPartLength")){
                Log.i(TAG, "End of Part_length");
                runOnUiThread(() -> {
                    oneweekplus_button.setVisibility(Button.VISIBLE);
                    oneweek_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartThree")){
                Log.i(TAG, "End of Part_three");
                runOnUiThread(() -> {
                    plane_button.setVisibility(Button.VISIBLE);
                    train_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartFour")){
                Log.i(TAG, "End of Part_four");
                runOnUiThread(() -> {
                    city_button.setVisibility(Button.VISIBLE);
                    beach_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartFourExtra")){
                Log.i(TAG, "End of Part_fourExtra");
                runOnUiThread(() -> {
                    logement_button.setVisibility(Button.VISIBLE);
                    activity_button.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartFiveB")){
                Log.i(TAG, "End of Part_fiveB");
                runOnUiThread(() -> {
                    result_button.setVisibility(Button.VISIBLE);
                });
            }

            if(params.get(0).equals("endPartSix")){
                long startTime = System.currentTimeMillis();
                Log.i(TAG, "display recommendation buttons " + Long.toString(startTime));
                Log.i(TAG, "display recommendation buttons ");
                DestinationSelection destinationSelection = new DestinationSelection();
                runOnUiThread(() -> {
                    destinationSelection.combinations(destination_type,mode_transport,berlin_recommend,rome_recommend,corfou_recommend,corse_recommend,londres_recommend
                            ,amsterdam_recommend,barcelone_recommend,nice_recommend,berlin_button,rome_button,corfou_button,corse_button,londres_button,amsterdam_button,barcelone_button,nice_button).option_one.setVisibility(Button.VISIBLE);

                    destinationSelection.combinations(destination_type,mode_transport,berlin_recommend,rome_recommend,corfou_recommend,corse_recommend,londres_recommend
                            ,amsterdam_recommend,barcelone_recommend,nice_recommend,berlin_button,rome_button,corfou_button,corse_button,londres_button,amsterdam_button,barcelone_button,nice_button).option_two.setVisibility(Button.VISIBLE);

                    destinationSelection.combinations(destination_type,mode_transport,berlin_recommend,rome_recommend,corfou_recommend,corse_recommend,londres_recommend
                            ,amsterdam_recommend,barcelone_recommend,nice_recommend,berlin_button,rome_button,corfou_button,corse_button,londres_button,amsterdam_button,barcelone_button,nice_button).recommendation_view.setVisibility(Button.VISIBLE);
                });
            }
            if(params.get(0).equals("endPartSeven")){
                Log.i(TAG, "ending, waving goodbye");
                wave_animation(qiContext);

                DestinationSelection destinationSelection = new DestinationSelection();
                String[] both = destinationSelection.combinations(destination_type,mode_transport,berlin_recommend,rome_recommend,corfou_recommend,corse_recommend,londres_recommend
                        ,amsterdam_recommend,barcelone_recommend,nice_recommend,berlin_button,rome_button,corfou_button,corse_button,londres_button,amsterdam_button,barcelone_button,nice_button).everythingToSay;
                final String first_option = both[0];
                final String second_option = both[1];
                final String recommended = both[2];
                removeFromDynamic(combination_one, first_option);
                removeFromDynamic(combination_two, second_option);
                removeFromDynamic(recommendation, recommended);
                removeFromDynamic(location, destination_type);
                removeFromDynamic(transportation, mode_transport);
                removeFromDynamic(company, type_traveller);
            }

        }
        @Override
        public void stop() {
            // This is called when chat is canceled or stopped
            Log.i("Inside", "QiChatExecutor stopped");
        }
    }

    /* Listening State Behavior */

    private Future<Void> nodding_whileListening (QiContext qiContext){
        Log.i(TAG, "Nodding ");
        AtomicBoolean promiseFulfilled = new AtomicBoolean(false);
        Future futureNodding = Future.of(null);

        Future<Void> waitFuture = FutureUtils.wait(2000L, TimeUnit.MILLISECONDS);

        futureNodding = futureNodding.andThenConsume(nod ->{
            build_animation(R.raw.affirmation_a008,qiContext);
        });

        futureNodding = futureNodding.andThenCompose(wait -> waitFuture);
        return futureNodding;
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

    private Future<Void> behaviorListening(QiContext qiContext) {
        runOnUiThread(() -> {
            listentab.setVisibility(TextView.VISIBLE);
        });
        Log.i(TAG, "ENTERED LISTENING ///////////////////");
        Future<Void> futurelistencompile = Future.of(null);

       return futurelistencompile = futurelistencompile.andThenConsume(voidfuture -> {
            ConversationStatus.OnHearingChangedListener listener = hearing -> {
                if (hearing) {
                    nodding_whileListening(qiContext);
                    Log.i(TAG, "hearing //////");
                } else {
                    Log.i(TAG, "NOT HEARING ///////");
                    nodding_whileListening(qiContext).cancel(true);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> {
                        compilingTab.setVisibility(TextView.VISIBLE);
                    });
                }
            };
            // At the beginning
            if (isRobotHearing(qiContext)) {
                nodding_whileListening(qiContext);
            }
            subscribeToIsRobotHearing(qiContext, listener);

            // When don't want to know
            unsubscribeFromIsRobotHearing(qiContext, listener);
        });
    }

    private Future<Void> totalListening (QiContext qiContext){
        Log.i(TAG, "TOTAL LISTENING");

        AtomicBoolean promiseFulfilled = new AtomicBoolean(false);

        Future<Void> lookAtFuture = behaviorListening(qiContext);

        Future<Void> waitFuture = FutureUtils.wait(25000L, TimeUnit.MILLISECONDS);

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

    private Future<Void> fullListen (QiContext qiContext){
        Future<Void> futureListen = totalListening(qiContext);

        return futureListen.andThenConsume(nod ->{
            Log.i(TAG, "going to bookmark");
            qiChatbot.async().goToBookmark(part_fiveExtraBookmark, AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
        });
    }
}
