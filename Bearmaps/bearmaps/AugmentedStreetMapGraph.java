package bearmaps;

import bearmaps.utils.graph.streetmap.StreetMapGraph;
import bearmaps.utils.graph.streetmap.Node;
import bearmaps.utils.trie.MyTrieSet;
import java.util.*;
import bearmaps.utils.ps.Point;
import bearmaps.utils.ps.WeirdPointSet;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {

    private HashMap<Point, Node> pointToNode;
    private List<Node> allNodes;
    private List<Node> nodesWithNeighbors;
    private List<Point> points;
    private MyTrieSet locationNameTrie;
    private Map<String, List<Node>> cleanNameToNode;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        allNodes = this.getNodes();
        nodesWithNeighbors = new ArrayList<>();
        for (Node n : allNodes) {
            if (neighbors(n.id()).size() != 0) {
                nodesWithNeighbors.add(n);
            }
        }
        points = new LinkedList<>();
        pointToNode = new HashMap<>();
        locationNameTrie = new MyTrieSet();
        for (Node node : nodesWithNeighbors) {
            Point point = new Point(node.lon(), node.lat());
            points.add(point);
            pointToNode.put(point, node);
        }

        cleanNameToNode = new HashMap<>();
        for (Node node : allNodes) {
            if (node.name() != null) {
                String cleanedName = cleanString(node.name());
                locationNameTrie.add(cleanedName);
                if (!cleanNameToNode.containsKey(cleanedName)) {
                    cleanNameToNode.put(cleanedName, new LinkedList<>());
                }
                cleanNameToNode.get(cleanedName).add(node);
            }
        }

    }

    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        WeirdPointSet pointSet = new WeirdPointSet(points);
        Point closestPoint = pointSet.nearest(lon, lat);
        Node closestNode = pointToNode.get(closestPoint);
        return closestNode.id();
    }


    /**
     * For Project Part III (extra credit)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        List<String> matchingCleanedNames = locationNameTrie.keysWithPrefix(cleanString(prefix));
        List<String> matchingFullLocationNames = new LinkedList<>();
        for (String matchingCleanedName : matchingCleanedNames) {
            List<Node> l = cleanNameToNode.get(matchingCleanedName);
            for (int i = 0; i < l.size(); i++) {
                Node node = l.get(i);
                matchingFullLocationNames.add(node.name());
            }
        }
        return matchingFullLocationNames;
    }

    /**
     * For Project Part III (extra credit)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> locations = new LinkedList<>();
        String cleanName = cleanString(locationName);
        if (!cleanNameToNode.containsKey(cleanName)) {
            return locations;
        }
        for (Node node : cleanNameToNode.get(cleanName)) {
            Map<String, Object> locationInfo = new HashMap<>();
            locationInfo.put("lat", node.lat());
            locationInfo.put("lon", node.lon());
            locationInfo.put("name", node.name());
            locationInfo.put("id", node.id());
            locations.add(locationInfo);
        }
        return locations;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
