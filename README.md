# multimodal_socialcues
Author: Karen Tatarian

Multi-modal Social Cues for Socially Intelligent Human-Robot Interaction in a Travel Planning Application (as a possible B2B solution)

This project is part of the following Journal Paper:  "How does modality matter? Investigating the synthesis and effects of multi-modal robot behavior on social intelligence" published at the International Journal of Social Robotics  (IJSR)

Publication Link: https://link.springer.com/article/10.1007/s12369-021-00839-w 

Researchgate Link: https://www.researchgate.net/publication/356473237_How_does_Modality_Matter_Investigating_the_Synthesis_and_Effects_of_Multi-modal_Robot_Behavior_on_Social_Intelligence 


This work presents a multi-modal interaction focusing on the following modalities: proxemics for social navigation ```GreetingNavigation``` , gaze mechanisms ```GazeMechanism```(for turn-taking floor-holding, turn-yielding and joint attention), kinesics (for symbolic, deictic, and beat gestures), and social verbal content.

This application was implemented on Pepper robot (by SoftBank Robotics) running on Android and used for the study on the investigation and analysis of multi-modal social cues. 

This version is compatible with Pepper running Naoqi OS 2.9.5.172 and more.

In this application, Pepper acts a travel agent planning a holiday with the user (script in French). ```DestinationSelection``` is a class that takes the decisions made on the tablet by the user and accordingly returns the destination suggestion.  ```InformationHumans``` is a class that returns distances, angles, and attention state of the human around the robot. 

# Getting Started

## Prerequisites
A robotified project for Pepper with QiSDK. Read the [documentation](https://developer.softbankrobotics.com/pepper-qisdk) if needed. 

## Running the Application 
The project comes complete with a sample project. You can clone the repository, open it in Android Studio, and run this directly onto a robot.

## Project Content 
The project focuses on the following modalities: 
#### Social Gaze in GazeMechanism
The GazeMechanism's are designed are lookAt actions with respect to the current Human's HeadFrame and Robot's robotFrame.

GazeMechanisms: TurnTaking (called at beginning of Robot's speaking turn as ```%tun_taking``` ```Bookmark``` in scipt of ```Chat```), FloorHolding (called at in the middle of long speaking turns and called in the script using ```%floor_holding``` ```Bookmark```), and TurnYielding (called at the end of a speaking turn as ```%turn_yielding``` ```Bookmark``` in script)

#### Proxemics in GreetingNavigation 
Computes distance between robot and human and approaches human at speed 0.25 m/s until a distance of 0.85 meters is acheived. It is called in ```Chat``` script using ```%greeting_navigation```

### Gestures
Gestures presented in this work are: 1) Symbolic Gestures as Waving and Nodding for Backchaneling, 2) Diectic Gestures as Object Pointing and Other and Self pointing gestures 3) Beat Gestures as Emphasizing choices in speech. 

Waving gesture: created as an animation in ```wave_animation``` function in ```MainActivity.java``` and built in ```built_animation``` function. Gesture called in ```Chat``` script as an execute ```wave```

Nodding gesture for backchaneling: 

Oject pointing gesture is accompanied by joint attention: created as an animation in ```point_animation``` function in ```MainActivity.java``` and built in ```built_animation``` function. Gesture called in ```Chat``` script as an ```Bookmark``` ```%point_object```

Self-pointing gesture:  created as an animation in ```pointMyself_animation``` function in ```MainActivity.java``` and built in ```built_animation``` function. Gesture called in ```Chat``` script as an ```Bookmark``` ```%point_self```

Other-pointing gesture:  created as an animation in ```pointyou_rightArm``` function in ```MainActivity.java``` and built in ```built_animation``` function. Gesture called in ```Chat``` script as an ```Bookmark``` ```%point_you```

Beat gestures to emphasize two choices:  created as an animation in ```righthand_animation``` function in ```MainActivity.java``` and built in ```built_animation``` function. Gesture called in ```Chat``` script as an ```Bookmark``` ```%hand_one```

# Acknowledgment
This project has received funding from the European Union’s Horizon 2020 research and innovation programme under grant agreement No 765955. 



