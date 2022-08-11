import java.util.*;

public class Ex02 {

    /*
    This class includes several static classes,
    Normally I would put these in seperate files, one for each class
    Since it is specified that the java file is to be names ex02, I have
    chosen to do so, and instead have multiple static files within the parent class (ex02)

    NOTE: I have used code from the Github repo, lessons/src/main/java/org/pg4200/les09/UndirectedGraph.java
    and solutions/src/main/java/org/pg4200/sol09/AllPathsGraph.java
    As it stands in the text, it is the base of my implementation, but is, at least
    I feel, customized to the given problem.
    I Have also included the test file for this assignment, as it is written in the text:
    You must provide appropriate test examples and test data, to show that your code works as
    required.
    I know that it may suffice with the initGraph() method, but since it was unclear,
    I chose to also sign in the test file Ex02Test.java
     */

    public static class TransportationGraph{

        public TransportationGraph() {
            initGraph();
        }

        public Map<Integer, Set<Integer>> graph = new HashMap<>();
        public ArrayList<Stop> stops = new ArrayList<>();

        public void addVertex(Integer stopId){
            Objects.requireNonNull(stopId);

            graph.putIfAbsent(stopId, new HashSet<>());
        }

        public void addEdge(Integer from, Integer to){
            Objects.requireNonNull(from);
            Objects.requireNonNull(to);

            addVertex(from);
            addVertex(to);

            //Adding edge between the stops
            graph.get(from).add(to);
            if(! from.equals(to)) {
                graph.get(to).add(from);
            }
        }

        //As the shortestPath(start, end) returns integer values of the stops,
        //I made this helper for having a method that returns the names of the stops
        public List<String> shortestPathString(String start, String end){
            List<Integer> stopIds = shortestPath(start, end);
            List<String> stopNames = new ArrayList<>();
            for(int i : stopIds){
                stopNames.add(getStopById(i).stopName);
            }
            return stopNames;
        }

        private int getStopByPlaceName(String placeName){
            Integer stopId = null;
            for(Map.Entry<Integer, Set<Integer>> e: graph.entrySet()){
                for(String place : getStopById(e.getKey()).placesInReach){
                    String placeLowerCase = place.toLowerCase();
                    if (placeLowerCase.equals(place)){
                        stopId = e.getKey();
                    }
                }
            }
            return stopId;
        }
        public List<Integer> shortestPath(String start, String end){
            String startLowerCase = start.toLowerCase();
            String endToLowerCase = end.toLowerCase();
            Integer startStop = null;
            Integer endStop = null;
            //Worst case O notation is n*m (see ex03.txt)
            for(Map.Entry<Integer, Set<Integer>> e: graph.entrySet()){
                for(String place : getStopById(e.getKey()).placesInReach){
                    String placeLowerCase = place.toLowerCase();
                    if(placeLowerCase.equals(startLowerCase)){
                        startStop = e.getKey();
                    }if (placeLowerCase.equals(endToLowerCase)){
                        endStop = e.getKey();
                    }
                }
            }

            if(startStop == null || endStop == null){
                return null;
            }

            return findPathBetweenStopsBFS(startStop, endStop);
        }

        public List<List<Integer>> findAllPaths(String start, String end) {

            if (start.equals(end)) {
                //we do not cons
                // -ider cycles
                throw new IllegalArgumentException();
            }

            String startLowerCase = start.toLowerCase();
            String endToLowerCase = end.toLowerCase();
            Integer startStop = null;
            Integer endStop = null;
            for(Map.Entry<Integer, Set<Integer>> e: graph.entrySet()){
                for(String place : getStopById(e.getKey()).placesInReach){
                    String placeLowerCase = place.toLowerCase();
                    if(placeLowerCase.equals(startLowerCase)){
                        startStop = e.getKey();
                    }if (placeLowerCase.equals(endToLowerCase)){
                        endStop = e.getKey();
                    }
                }
            }

            if (!graph.containsKey(startStop) && !graph.containsKey(endStop)) {
                return Collections.emptyList();
            }

            Deque<Integer> stack = new ArrayDeque<>();

            List<List<Integer>> paths = new ArrayList<>();

            dfs(paths, stack, startStop, endStop);

            return paths;
        }

        private void dfs(List<List<Integer>> paths, Deque<Integer> stack, Integer current, Integer end) {

            stack.push(current);

            if (isPathTo(stack, end)) {
                List<Integer> path = new ArrayList<>(stack);
                Collections.reverse(path);
                paths.add(path);
                return;
            }

            for (Integer connected : getConnectingStops(current)) {
                if (stack.contains(connected)) {
                    continue;
                }

                dfs(paths, stack, connected, end);
                //backtrack
                stack.pop();
            }
        }

        protected boolean isPathTo(Deque<Integer> stack, Integer vertex){
            return !stack.isEmpty() && stack.peek().equals(vertex);
        }

        public List<Integer> findPathBetweenStopsBFS(Integer start, Integer end) {

            if(! graph.containsKey(start) || ! graph.containsKey(end)){
            /*
                We return null if there are no stops matching in our graph
             */
                return null;
            }

            if(start.equals(end)){
                throw new IllegalArgumentException();
            }

            Queue<Integer> queue = new ArrayDeque<>();
            Map<Integer,Integer> bestParent = new HashMap<>();

            queue.add(start);

            mainLoop: while(! queue.isEmpty()){

                Integer parent = queue.poll();

                for(Integer child : graph.get(parent)){

                    if( child.equals(start) || bestParent.get(child) != null){
                        continue;
                    }
                    bestParent.put(child, parent);

                    if(child.equals(end)){
                    /*
                        found a path, no need to analyze
                        the rest of the queue
                     */
                        break mainLoop;
                    }

                    queue.add(child);
                }
            }

            if(! bestParent.containsKey(end)){
                return null;
            }

        /*
            At this point, we know that there is a path.
            So, starting from "end", we need to use the
            bestParent map to backtrack the path from "end"
            to "start"
         */

            List<Integer> path = new ArrayList<>();
            Integer current = end;
            while (current != null){
                path.add(0, current);
                current = bestParent.get(current);
            }

            return path;
        }

        public Stop getStopById(int id){
            for(Map.Entry<Integer, Set<Integer>> e: graph.entrySet()){
                if(e.getKey() == id){
                    return stops.get(e.getKey());
                }
            }
            return null;
        }

        public int getNumberOfVertices(){
            return graph.size();
        }

        public int getNumberOfEdges(){
            /*
            All the values (connecting points)
            To one vertex, is stored in the values
            sum up all the stops in the values through the map
             */
            long edges = graph.values().stream()
                    .mapToInt(s -> s.size())
                    .sum();

            /*
                We divide by two
                As we go through the map, we will get two connections, which is actually the same edge
                ex
                edge 1: Majorstuen-NationalTeatret
                edge 2: NationalTeatret-Majorstuen
                Since this is only one edge, we divide by 2
             */

            edges += graph.entrySet().stream()
                    .filter(e -> e.getValue().contains(e.getKey()))
                    .count();

            return (int) edges / 2;
        }

        //Get all the vertices connected to a given vertex
        public Collection<Integer> getConnectingStops(Integer stop){
            Objects.requireNonNull(stop);
            return graph.get(stop);
        }

        /*
        The two methods below are unnecessary, but since it stands in the text
        that we were to implement all the
         */

        public void removeEdge(Integer from, Integer to) {

            Objects.requireNonNull(from);
            Objects.requireNonNull(to);

            Set<Integer> connectedFrom = graph.get(from);
            Set<Integer> connectedTo = graph.get(to);

            if(connectedFrom != null){
                connectedFrom.remove(to);
            }
            if(connectedTo != null){
                connectedTo.remove(from);
            }
        }

        public void removeVertex(Stop vertex) {

            Objects.requireNonNull(vertex);

            if(! graph.containsKey(vertex)){
                //nothing to do
                return;
            }

        /*
            Before we can remove the vertex, we have to
            remove all other connections to such vertex in the
            other vertices.
            Note: we can call "forEach" directly on a collection
            without opening a stream() first.
         */
            graph.get(vertex).forEach(v -> graph.get(v).remove(vertex));

            graph.remove(vertex);
        }


        private void initGraph() {
            ArrayList<String> holmenkollenList = new ArrayList<>();
            holmenkollenList.add("Holmenkollen Ski Museum");
            holmenkollenList.add("Holmenkollen Ski Jump");

            ArrayList<String> sentralStationList = new ArrayList<>();
            sentralStationList.add("Opera");
            sentralStationList.add("Akerhus Festning");
            sentralStationList.add("Nasjonalmuseet Arkitektur");

            ArrayList<String> majorstuenList = new ArrayList<>();
            majorstuenList.add("Frogner Parken");
            majorstuenList.add("Frogner Badet");

            ArrayList<String> nationalteatretList = new ArrayList<>();
            nationalteatretList.add("Oslo University");
            nationalteatretList.add("Karl Johan");

            ArrayList<String> nydalenList = new ArrayList<>();
            nydalenList.add("Nydalen Bryggeri & Spiseri");
            nydalenList.add("Nydalen Sushi");

            Stop holmenkollen = new Stop(
                    0,
                    "Holmenkollen",
                    holmenkollenList
            );

            Stop sentralstation = new Stop(
                    1,
                    "Sentralstation",
                    sentralStationList
            );

            Stop majorstuen = new Stop(
                    2,
                    "Majorstuen",
                    majorstuenList
            );

            Stop nationalteatret = new Stop(
                    3,
                    "Nationalteatret",
                    nationalteatretList
            );

            Stop nydalen = new Stop(
                    4,
                    "Nydalen",
                    nydalenList
            );

            stops.add(holmenkollen);
            stops.add(sentralstation);
            stops.add(majorstuen);
            stops.add(nationalteatret);
            stops.add(nydalen);

            //Connecting Holmenkollen to Majorstuen
            addEdge(holmenkollen.id, majorstuen.id);

            //Connecting Majorstuen to Nationalteatret
            addEdge(majorstuen.id, nationalteatret.id);

            //Connecting Nationalteatret to Sentralstation
            addEdge(nationalteatret.id, sentralstation.id);

            //Connecting Sentralstation to Nydalen
            addEdge(sentralstation.id, nydalen.id);

            //Connecting Nydalen to Majorstuen
            addEdge(majorstuen.id, nydalen.id);
        }
    }

    public static class Stop{

        private int id;
        private String stopName;
        private ArrayList<String> placesInReach;

        public Stop(int id, String stopName, ArrayList<String> placesInReach) {
            this.id = id;
            this.stopName = stopName;
            this.placesInReach = placesInReach;
        }
    }
}
