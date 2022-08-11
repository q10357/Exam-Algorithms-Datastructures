(NB: EXPLANATION OF CODE FURTHER DOWN, UNDER -------)

Before I explain my code, I want to address one issue I encountered.
I had some issues understanding this in the Ex02:
NOTE: The name of the file will be Ex02.java. You will have to make some decisions about
how to implement your method, and how to use the two graphs describing the two networks:
metro and tram. You should write the assumptions you made, the reasoning, and describe
your solution in comments or in a separate file called README.md.

This was rather tricky to understand. In the assignment (Ex02), there is no mention of tram/ metro
before this note. I don't really get what the teacher is trying to communicate to me.
I therefore ignored this part, as I got rather confused.
I didn't see the mentioning of two graphs either. Maybe I just didn't understand it, but I choose to have all the 
stops in the same graph, and not implement anthing regarding tram or metro.

"Consider a city that has a public transport network that can be described as a graph"
Here it is said it's a graph (as in one), I therefore chose to implement it as one.

-------------------------------------------------

To the code then:
For the values in the graph, I chose to have Integers, where each Integer value refers a certain
stop's id. Each stop has a unique id, a name, and an ArrayList of places in reach.
I thought of memory and performance while doing this. I could have chosen Stop objects as values,
but it didn't make sense to me to store whole objects, when we could just get the object instance 
when we have the correct ID, which we retrieve through the different methods. 

I chose to implement Breath First Search and not Depth First Search in the method where we find
the shortest path (shortestPath(start, end)). I came to this conclusion because of 
this:
We have only 5 stops, it is a big possibility that we will find the stop 
close to the start point. It therefore seems logical to me to examine all the nodes close to start
before we move deeper in the graph.

In the findAllPaths(start, end), i did a depth first search. I did this 
1. Because I wanted to show both methods
2. Because we need to examine the graph deeply, since we are going to find all the possible paths.


