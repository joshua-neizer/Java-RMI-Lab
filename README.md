# Java RMI & Sockets Lab
### CISC324: Operating Systems - Lab 6
### by Joshua Neizer
<br>

## Contents of the Directory
**README.txt**: this current file, has written exercise solutions<br>
**Central_Server.java**: the central RMI server that handles page fault requests<br>
**Central_Interface.java**: the interface for the central RMI server<br>
**FIFO_Server.java**: the frame request server that follows the First-In-First-Out policy for frame replacement <br>
**LFU_Server.java**: the frame request server that follows the Least Frequently Used policy for frame replacement <br>
**MFU_Server.java** the frame request server that follows the Most Frequently Used policy for frame replacement<br>
**Client_1.java**:  a client process that sends requests to the FIFO, LFU MFU servers to get their request squared and returned <br>
**Client_2java**:  a client process that sends requests to the FIFO, LFU MFU servers to get their request squared and returned <br>
**Socket_IO.java**: contains static functions for connecting processes via sockets protocols. <br>
**L6.pdf**: provides a background on Java's RMI and Socket libraries, as well as
outlining the objectives of the lab

**Side Note:** The reason why I decided to create the Socket_IO.java file is that I 
realized that I would have to use the same socket IO code for 5 of my programs. 
The code would all be identical, however, would have socket host names and socket 
client names differ. So instead, I decided to create generic versions of the code, 
and keep them as static functions that could be referenced to by passing the socket 
host name and socket_client name as arguments. The port numbers were kept as Maps 
who's key was the process associated with it, which can be accessed by the arguments 
passed through. <br>

## Lab Results
### 4 Frames
#### FIFO
    Client_1: 13
    Client_2: 13
    Total:    26
    
#### LFU 
    Client_1: 12
    Client_2: 12
    Total:    24

#### MFU 
    Client_1: 9
    Client_2: 13
    Total:    22


### 5 Frames
#### FIFO 
    Client_1: 7
    Client_2: 11
    Total:    18
    
#### LFU 
    Client_1: 8
    Client_2: 8
    Total:    16

#### MFU 
    Client_1: 8
    Client_2: 6
    Total:    14


### 6 Frames
#### FIFO
    Client_1: 9
    Client_2: 6
    Total:    15
    
#### LFU
    Client_1: 7
    Client_2: 6
    Total:    13

#### MFU
    Client_1: 8
    Client_2: 4
    Total:    12

<br>

## Procedure Explanation
### FIFO Server
The procedure I used for the FIFO server was simply using a FIFO queue to keep 
track of the order frames that were requested and a frame array to keep track of 
the active frames. The queue is only updated whenever a new frame, not currently 
in the queue, is requested. At that point, the head of the queue is chosen as the 
victim, and the remaining frames shift down, index wise, such that there is room
 for the newly requested frame to be the tail. Once the victim frame is chosen, 
 it is found within the frame array and swapped with the new request.
<br>

### LFU Server
The procedure I used for the LFU server was to use a frame array to keep track 
of the active frames, a FIFO queue to keep track of the order frames arrived in, 
and a HashMap to keep track of the frame frequency. For every page fault, if the 
frame array isn't full, the request is appended to the end of the array, the queue
 is updated and the HashMap updates the frequency count. Within the HashMap, the 
 key is the frame value, and the value is the frame value's frequency. If the frame 
 array is full, then each frame's frequency is compared and the frame with the 
 lowest frequency is chosen to be the victim. If there are two frames that could 
 be the victim, whichever frame is closest to the head of the FIFO queue is chosen. 
 After a victim frame is chosen, the FIFO queue is updated by shifting all frames 
 after the victim, to the left, and appending the requested frame to the end of 
 the queue. After, the victim frame is then swapped out with the request frame.
<br>

### MFU Server
The procedure I used for the MFU server mirrors that of the LFU to the extent 
that they are essentially almost the same. The only difference is that the victim 
who has the largest frequency is swapped out. The remaining overheard, data 
structures, variables and logic remains the same. That being using the frame 
array, FIFO queue and frequency HashMap.