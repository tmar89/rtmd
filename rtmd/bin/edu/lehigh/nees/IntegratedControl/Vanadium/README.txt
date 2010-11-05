RTMD Vanadium Steel HOW TO
--------------------------

This program is the control algorithm for the Vanadium Steel project.  

It contains two manually controlled actuators tied to the control system through SCRAMNet.
The SCRAMNet address parameters are configured through a properties file.
Actuator Displacement and Load are read in real-time and sampled 10 times a second.
The total force between the two actuators are shows on the screen and the total load box will
turn RED if the absolute value of the total load exceeds the tolerance.
The actuators are manually controlled through the combination of increment and movement type.
Hitting Move Once will determine if the actuator should extend or retract and move at the 
interval size immediately.

Below is the Automation section where a user can set up a test loop.
The test loop will move an actuator based on MINIMUM_INCREMENT_SIZE defined in the properties.
Each step will determine which actuator(s) should move based on total load.
The Actuator defined as Compression will move alone when the total load is greater than 
the positive tolerance.
The Actuator defined as Tension will move alone when the total load is less than 
the negative tolerance.
Both Actuators will move when the total load is within the tolerance.
Hitting Execute will run the automated process.
Hitting Stop will terminate the automation.
Hitting Pause will pause the termination.
A CSV file is created at the end of the test with the data at each action.

