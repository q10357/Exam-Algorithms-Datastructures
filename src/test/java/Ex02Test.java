import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Ex02Test {

    @Test
    public void testNumberOfVertices() {

        Ex02.TransportationGraph graph = new Ex02.TransportationGraph();

        assertEquals(5, graph.getNumberOfVertices());
        assertEquals(5, graph.getNumberOfEdges());
    }

    @Test
    public void testConnectingStops(){

        Ex02.TransportationGraph graph = new Ex02.TransportationGraph();

        //Holmenkollen
        Ex02.Stop holmenkollen = graph.getStopById(1);

        //Sentralstation
        Ex02.Stop sentralStation = graph.getStopById(2);

        List<Integer> stopIds = graph.shortestPath("Opera", "Holmenkollen Ski Museum");

        assertEquals(4, graph.shortestPath("Opera", "Holmenkollen Ski Museum").size());
    }

    @Test
    public void testConnectingStopsString(){

        Ex02.TransportationGraph graph = new Ex02.TransportationGraph();

        List<String> stopNames = graph.shortestPathString("Opera", "Holmenkollen Ski Museum");

        assertEquals(4, stopNames.size());

        //We know that when travelling from Holmenkollen to Sentralstation, we need to
        //Go through Majorstuen, as it is the only vertix adjacent to Holmenkollen
        assertTrue(stopNames.contains("Majorstuen"));
    }

    @Test
    public void findAllPaths(){
        Ex02.TransportationGraph graph = new Ex02.TransportationGraph();

        List<List<Integer>> allPathsHolmenkollenSentralstation = graph.findAllPaths("Opera", "Holmenkollen Ski Museum");
        List<List<Integer>> stopIds = graph.findAllPaths("Opera", "Holmenkollen Ski Museum");

        assertEquals(2, stopIds.size());
    }

}
