# multimodal_socialcues
Author: Karen Tatarian

Multi-modal Social Cues for Socially Intelligent Human-Robot Interaction 

This project is part of the following publication:  "How does modality matter? Investigating the synthesis and effects of multi-modal robot behavior on social intelligence". 
This work presents a multi-modal interaction focusing on the following modalities: proxemics for social navigation, gaze mechanisms (for turn-taking floor-holding, turn-yielding and joint attention), kinesics (for symbolic, deictic, and beat gestures), and social verbal content.

This application was implemented on Pepper robot (by SoftBank Robotics) running on Android and used for the study on the investigation and analysis of multi-modal social cues. 

This version is compatible with Pepper running Naoqi OS 2.9.5.172 and more.

In this application, Pepper acts a travel agent planning a holiday with the user. 

# Getting Started

## Prerequisites
A robotified project for Pepper with QiSDK. Read the [documentation](https://developer.softbankrobotics.com/pepper-qisdk) if needed. 

## Running the Application 
The project comes complete with a sample project. You can clone the repository, open it in Android Studio, and run this directly onto a robot.

## Project Content 
The project focuses on the following modalities: 
### GazeMechanism
The GazeMechanism's are designed are lookAt actions with respect to the current Human's HeadFrame and Robot's robotFrame.

GazeMechanisms: TurnTaking (called at beginning of Robot's speaking turn as %tun_taking Bookmark in scipt), FloorHolding (called at in the middle of long speaking turns and called in the script using %floor_holding Bookmark), and TurnYielding (called at the end of a speaking turn as %turn_yielding Bookmark in script)

### GreetingNavigation 
Computes distance between robot and human and approaches human at speed 0.25 m/s until a distance of 0.85 meters is acheived 

