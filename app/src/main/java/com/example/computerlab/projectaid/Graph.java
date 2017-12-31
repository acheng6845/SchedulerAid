package com.example.computerlab.projectaid;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by achen on 4/18/2017.
 */


public class Graph {

    private class Node implements Comparable<Node>{
        private long duration, criticalTime, earlyStart, earlyFinish, lateStart, lateFinish;
        private String name, id;
        private Node(long duration, String name, String id) {
            this.duration = duration;
            this.name = name;
            this.criticalTime = 0;
            this.earlyStart = 0;
            this.earlyFinish = 0;
            this.lateStart = 0;
            this.lateFinish = 0;
            this.id = id;
        }
        private long getDuration() {
            return this.duration;
        }
        private void setDuration(int duration) { this. duration = duration; }
        private String getName() { return this.name; }
        private void setName(String name) { this.name = name; }
        private long getEarlyStart() { return this.earlyStart; }
        private void setEarlyStart(long start) { this.earlyStart = start; }
        private long getEarlyFinish() { return this.earlyFinish; }
        private void setEarlyFinish(long finish) { this.earlyFinish = finish; }
        private long getLateStart() { return this.lateStart; }
        private void setLateStart(long start) { this.lateStart = start; }
        private long getLateFinish() { return this.lateFinish; }
        private void setLateFinish(long finish) { this.lateFinish = finish; }

        @Override
        public int compareTo(@NonNull Node node) {
            if(this.getEarlyStart() < node.getEarlyStart()) return -1;
            else if(this.getEarlyStart() == node.getEarlyStart()) return 0;
            else return 1;
        }
    }

    private class Edge {
        private Node origin, destination;
        private int weight;
        private Edge(Node origin, Node destination) {
            this.origin = origin;
            this.destination = destination;
            this.weight = 0;
        }
        public Node[] endpoints() {
            return new Node[]{this.origin, this.destination};
        }
        public Node opposite(Node v) throws IllegalArgumentException {
            if (v == this.origin || v == this.destination)
                return v == this.origin ? this.destination : this.origin;
            else throw new IllegalArgumentException();
        }
        public int getWeight() {
            return this.weight;
        }
    }

    private HashMap<Node, HashMap<Node, Edge>> outgoing;
    private HashMap<Node, HashMap<Node, Edge>> incoming;
    private Node start, end;

    public Graph() {
        this.outgoing = new HashMap<Node, HashMap<Node, Edge>>();
        this.incoming = new HashMap<Node, HashMap<Node, Edge>>();
        start = new Node(0, null, null);
        end = new Node(0, null, null);
        this.outgoing.put(start, new HashMap<Node, Edge>());
        this.incoming.put(end, new HashMap<Node, Edge>());
        set_parent(end, start);
    }
    public int node_count() { return this.outgoing.size()-1; }
    private Set<Node> nodes() { return this.outgoing.keySet(); }
    public Set<String> node_names() {
        Set<String> names = new HashSet<>();
        for(Node n : nodes()) {
            if(n.getName() != null) names.add(n.getName());
        }
        return names;
    }
    public int edge_count() {
        int count = 0;
        for (Node v : nodes()) count += this.outgoing.get(v).size();
        return count;
    }

    public Set<Edge> edges() {
        Set<Edge> edges = new HashSet<>();
        for (Node v : nodes()) {
            edges.addAll(this.outgoing.get(v).values());
        }
        return edges;
    }
    public Node get_node(String name) {
        for(Node node : nodes()) {
            if(node.getName() != null && node.getName().equals(name)) return node;
        }
        return null;
    }
    public Edge get_edge(Node u, Node v) { return this.outgoing.get(u).get(v); }
    public int degree(Node v) { return this.outgoing.get(v).size(); }
    public Set<Edge> incident_outgoing_edges(Node v) {
        Set<Edge> edges = new HashSet<>();
        for(Object edge : this.outgoing.get(v).values()) edges.add((Edge)edge);
        return edges;
    }
    public Set<Edge> incident_incoming_edges(Node v) {
        Set<Edge> edges = new HashSet<>();
        for(Object edge : this.incoming.get(v).values()) edges.add((Edge)edge);
        return edges;
    }
    public Node insert_node(long duration, String name, String id) {
        return insert_node(duration, start, name, id);
    }
    public Node insert_node(long duration, Node parent, String name, String id) throws IllegalArgumentException {
        //if name already exists, throw an exception
        for(Node v : nodes()) {
            if(v.name != null && v.name.equals(name)) throw new IllegalArgumentException();
        }

        Node v = new Node(duration, name, id);
        this.outgoing.put(v, new HashMap<Node, Edge>());
        this.incoming.put(v, new HashMap<Node, Edge>());
        //connect all new nodes to the end node, i.e., they're childless
        insert_edge(v, end);
        insert_edge(parent, v);
        return v;
    }
    public Node insert_node(long duration, String name, List<String> parents, String id) {
        Node node = insert_node(duration, name, id);
        for(String parent_name : parents) {
            if(parent_name != null) {
                Node parent = get_node(parent_name);
                if(parent != null) set_parent(node, parent);
            }
        }
        return node;
    }
    public void set_parent(Node v, Node parent) {
        //if node was previously parentless, i.e., its parent was the start node, remove that edge.
        if(this.outgoing.get(start).containsKey(v)) this.outgoing.get(start).remove(v);
        //if parent was previously childless, i.e., its child was the end node, remove that edge.
        if(this.incoming.get(end).containsKey(parent)) this.incoming.get(end).remove(parent);
        insert_edge(parent, v);
    }

    private Edge insert_edge(Node u, Node v) {
        Edge edge = new Edge(u, v);
        //Log.d("Outgoing HashMap BEFORE", this.outgoing.toString());
        //Log.d("Incoming HashMap BEFORE", this.incoming.toString());
        this.outgoing.get(u).put(v, edge);
        this.incoming.get(v).put(u, edge);
        //Log.d("Outgoing HashMap AFTER", this.outgoing.toString());
        //Log.d("Incoming HashMap AFTER", this.incoming.toString());
        return edge;
    }

    public void remove_node_and_isolate(Node node) {
        //disconnect node from parents/outgoing edges
        disinherit_parents(node);
        //disconnect node from children/incoming edges
        disinherit_children(node);
        //finally, remove node
        this.outgoing.remove(node);
        this.incoming.remove(node);
    }

    //remove and connect parents with children
    public void remove_node_and_merge(Node node) {
        disinherit_parents(node);
        disinherit_children(node);
        for(Edge incoming_edge : incident_incoming_edges(node)) {
            Node parent = incoming_edge.opposite(node);
            for(Edge outgoing_edge : incident_outgoing_edges(node)) {
                Node child = outgoing_edge.opposite(node);
                insert_edge(parent, child);
            }
        }
        this.outgoing.remove(node);
        this.incoming.remove(node);
    }

    private void disinherit_parents(Node node) {
        //optimized, untested search
        for(Edge edge : incident_incoming_edges(node)) {
            Node parent = edge.opposite(node);
            this.outgoing.get(parent).remove(node);
            //if parent is now childless, connect it to the end node
            if(this.outgoing.get(parent).isEmpty()) insert_edge(parent, end);
        }
        //unoptimized, tested search
//        for(Node parent : nodes()) {
//            if(this.outgoing.get(parent).containsKey(node)) this.outgoing.get(parent).remove(node);
//            //if parent is now childless, connect it to the end node
//            if(this.outgoing.get(parent).isEmpty()) insert_edge(parent, end);
//        }
    }

    private void disinherit_children(Node node) {
        //optimized, untested search
        for(Edge edge : incident_outgoing_edges(node)) {
            Node child = edge.opposite(node);
            this.incoming.get(child).remove(node);
            //if child is now parentless, connect it to the start node
            if(this.incoming.get(child).isEmpty()) insert_edge(start, child);
        }
//        for(Node child : nodes()) {
//            if(this.incoming.get(child).containsKey(node)) this.incoming.get(child).remove(node);
//            //if child is now parentless, connect it to the start node
//            if(this.incoming.get(child).isEmpty()) insert_edge(start, child);
//        }
    }

    public ArrayList<String[]> find_critical_path() {
        for (Node node : nodes()) node.criticalTime = 0;
        back_flow_algorithm(end);
        // lambda comparator class sometimes results in error ???
        TreeMap<Long, Node> critical_path_priority_queue = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long t1, Long t2) {
                return (int) (t2 - t1);
            }
        });
        for(Node node : nodes()) {
            if(node != start) {
                critical_path_priority_queue.put(node.criticalTime, node);
            }
        }
        Set<Map.Entry<Long, Node>> set = critical_path_priority_queue.entrySet();
        Iterator<Map.Entry<Long, Node>> i = set.iterator();

        ArrayList<String[]> sorted_node_names = new ArrayList<>();

        while(i.hasNext()) {
            Map.Entry<Long, Node> entry = i.next();
            //System.out.print(entry.getValue().getName() + ": ");
            //System.out.println(entry.getKey());
            if(entry.getValue().getName() != null) {
                //System.out.println(entry.getValue().getName());
                sorted_node_names.add(new String[]
                        {entry.getValue().getName(), String.valueOf(entry.getKey())});
            }

        }

        return sorted_node_names;
    }

    public ArrayList<String[]> find_rudimentary_critical_path() {
        //reset nodes before calculation in case new nodes were introduced
        for (Node node : nodes()) {
            node.setEarlyFinish(0);
            node.setEarlyStart(0);
        }

        //begin with the nodes adjacent to the start
        for (Edge outgoing_edge : incident_outgoing_edges(start)) {
            Node child = outgoing_edge.opposite(start);
            early_start_and_finish_algorithm(child, new HashSet<Node>());
        }

        //Log.d("Early Start Algorith", "No problem here!");

        //to find critical path, begin with the end node and add the highest early finish to list until start
        ArrayList<String[]> critical_path = new ArrayList<>();
        Node critical_path_node = end;

        //bug: can fail if early_finish = early_finish for one of the nodes
        while(critical_path_node != null) {
            long latest_early_finish = 0;
            Node latest_early_finish_node = null;
            for(Edge incoming_edge : incident_incoming_edges(critical_path_node)) {
                Node parent = incoming_edge.opposite(critical_path_node);
                if(parent.getEarlyFinish() > latest_early_finish) {
                    latest_early_finish_node = parent;
                    latest_early_finish = parent.getEarlyFinish();
                }
            }
            if (latest_early_finish_node != null) {
                critical_path.add(new String[]{
                        latest_early_finish_node.getName(),
                        Long.toString(latest_early_finish_node.getDuration()),
                        Long.toString(latest_early_finish_node.getEarlyStart()),
                        Long.toString(latest_early_finish_node.getEarlyFinish())
                });
            }
            critical_path_node = latest_early_finish_node;
        }

        //Log.d("While Loop", "No problem here!");

        //since we started from the end, we need to reverse the arraylist
        Collections.reverse(critical_path);
        return critical_path;
    }

    public HashMap<String, ArrayList<String[]>> find_complete_critical_path() {
        //reset nodes
        for (Node node : nodes()) {
            node.setEarlyStart(0);
            node.setEarlyFinish(0);
            node.setLateStart(0);
            node.setLateFinish(0);
        }

        //begin with the nodes adjacent to the start
        for (Edge outgoing_edge : incident_outgoing_edges(start)) {
            Node child = outgoing_edge.opposite(start);
            early_start_and_finish_algorithm(child, new HashSet<Node>());
        }

        //set all fields in the end node to the earliest project end time
        end.setLateStart(end.getEarlyFinish());
        end.setLateFinish(end.getEarlyFinish());

        //begin with the nodes adjacent to the end
        for (Edge incoming_edge : incident_incoming_edges(end)) {
            Node parent = incoming_edge.opposite(end);
            late_start_and_finish_algorithm(parent, new HashSet<Node>());
        }

        HashMap<String, ArrayList<String[]>> paths = new HashMap<>();
        Set<Node> unvisited_nodes = new HashSet<>();
        //if unvisited_nodes = nodes() then removing an element from unvisited_nodes, removes it from nodes()
        for(Node n : nodes()) {
            if (n != start && n != end) unvisited_nodes.add(n);
        }
        Set<Node> visited_nodes = new HashSet<>();
        Node critical_path_node = start;
        int counter = 1;
        //initialize critical path arrayList
        paths.put("path0", new ArrayList<>());
        //Log.d("Checking start's edges", this.outgoing.get(critical_path_node).toString());

        while(critical_path_node != null &&
                this.outgoing.get(critical_path_node) != null && !unvisited_nodes.isEmpty()) {
            // break if only successor is the end node
            if(incident_outgoing_edges(critical_path_node).size() == 1 &&
                    this.outgoing.get(critical_path_node).containsKey(end)) break;
            for(Edge outgoing_edge : incident_outgoing_edges(critical_path_node)) {
                Node child = outgoing_edge.opposite(critical_path_node);
                long slack = child.getLateFinish() - child.getEarlyStart() - child.getDuration();
                if(slack == 0 && child != end) {
                    //Log.d("Slack == 0", child.getName() != null ? child.getName() : "Null");
                    paths.get("path0").add(new String[]{
                            child.getName(),
                            Long.toString(child.getEarlyStart()),
                            Long.toString(child.getLateStart()),
                            Long.toString(child.getEarlyFinish()),
                            Long.toString(child.getLateFinish()),
                            Long.toString(child.getDuration()),
                            child.id
                    });
                    unvisited_nodes.remove(child);
                    //visited_nodes.add(child);
                    critical_path_node = child;
                    break;
                }
                //Log.d("Slack != 0", child.getName() != null ? child.getName() : "NULL");
            }
        }
        //Iterator<Node> iterator = unvisited_nodes.iterator();
        BinaryHeap binaryHeap = new BinaryHeap();
        for (Node unVisitedNode : unvisited_nodes) binaryHeap.insert(unVisitedNode);
        while(!binaryHeap.isEmpty()) {
            //next node should have the smallest early start time
            //should use a binary heap, but tree map doesn't allow for duplicates!
//            long earliest_start_time = Long.MAX_VALUE;
//            Node node = null;
//            for(Node n : unvisited_nodes) {
//                if(n.getEarlyStart() <= earliest_start_time) {
//                    node = n;
//                    earliest_start_time = n.getEarlyStart();
//                }
//            }


            Node node = (Node)binaryHeap.deleteMin();

            if(node == null) {
                Log.wtf("Unvisited Nodes While Loop", "Node is Null!");
                break;
            }

            if(unvisited_nodes.contains(node)) {
                String startingNodeName = "path"+counter;
                paths.put(startingNodeName, new ArrayList<>());
                ++counter;
                //visited_nodes.add(node);
                paths.get(startingNodeName).add(new String[] {
                        node.getName(),
                        Long.toString(node.getEarlyStart()),
                        Long.toString(node.getLateStart()),
                        Long.toString(node.getEarlyFinish()),
                        Long.toString(node.getLateFinish()),
                        Long.toString(node.getDuration()),
                        node.id
                });
                //go down a path until reaching either the end node or a visited node
                while(unvisited_nodes.contains(node)){
                    unvisited_nodes.remove(node);
                    //loop over node's successors and add node to path if it's not already visited
                    for(Edge outgoing_edge : incident_outgoing_edges(node)) {
                        Node child = outgoing_edge.opposite(node);
                        if(child != null && unvisited_nodes.contains(child)) {
                            //unvisited_nodes.remove(child);
                            paths.get(startingNodeName).add(new String[] {
                                    child.getName(),
                                    Long.toString(child.getEarlyStart()),
                                    Long.toString(child.getLateStart()),
                                    Long.toString(child.getEarlyFinish()),
                                    Long.toString(child.getLateFinish()),
                                    Long.toString(child.getDuration()),
                                    child.id
                            });
                            node = child;
                            break;
                        }
                    }
                }
            }
        }
//        for(ArrayList<String[]> path : paths.values()) {
//            for(String[] array : path) {
//                Log.d("Check paths", Arrays.toString(array));
//            }
//        }
        //Log.d("Check paths", paths.values().toString());
        return paths;
    }

    //inspired by mathematical backflow algorithm according to Dr. Larry Bowen
    private void back_flow_algorithm(Node node) {
        //starts with end node and ends with start node
        if(node != start) {
            for (Edge incoming_edge : incident_incoming_edges(node)) {
                Node parent = incoming_edge.opposite(node);
                long largest_incident_critical_time = 0;
                //cycle through incident edges to find the one with the largest critical time
                for (Edge outgoing_edge : incident_outgoing_edges(parent)) {
                    Node child = outgoing_edge.opposite(parent);
                    if (child.criticalTime > largest_incident_critical_time)
                        largest_incident_critical_time = child.criticalTime;
                }
                //node's critical time = largest incident critical time + its duration
                parent.criticalTime = parent.getDuration() + largest_incident_critical_time;
                //recurse from the parent until reaching start
                back_flow_algorithm(parent);
            }
        }
    }

    //begin at start and mark ES and EF for all nodes by the greatest of its prerequisites
    private void early_start_and_finish_algorithm(Node node, HashSet<Node> visited_nodes) {
        if (!visited_nodes.contains(node)) {
            //begins by calling early_start_algorithm on start's incident edges.
            long largest_predecessor_early_finish = 0;
            for (Edge incoming_edge : incident_incoming_edges(node)) {
                Node parent = incoming_edge.opposite(node);
                largest_predecessor_early_finish =
                        parent.getEarlyFinish() > largest_predecessor_early_finish ?
                                parent.getEarlyFinish() : largest_predecessor_early_finish;
            }
            node.setEarlyStart(largest_predecessor_early_finish);
            node.setEarlyFinish(node.getEarlyStart() + node.getDuration());
            visited_nodes.add(node);

            if (node != end) {
                for (Edge outgoing_edge : incident_outgoing_edges(node)) {
                    Node child = outgoing_edge.opposite(node);
                    early_start_and_finish_algorithm(child, visited_nodes);
                }
            }
        }
    }

    //begin at end and mark LS and LF for all nodes by the smallest LS of its successors
    private void late_start_and_finish_algorithm(Node node, HashSet<Node> visited_nodes) {
        if(!visited_nodes.contains(node)) {
            //begins by calling late_start_and_finish_algorithm on end's incident edges.
            long smallest_successor_late_start = Long.MAX_VALUE;
            for (Edge outgoing_edge : incident_outgoing_edges(node)) {
                Node child = outgoing_edge.opposite(node);
                smallest_successor_late_start = child.getLateStart() < smallest_successor_late_start ?
                        child.getLateStart() : smallest_successor_late_start;
            }
            node.setLateFinish(smallest_successor_late_start);
            node.setLateStart(node.getLateFinish() - node.getDuration());

            if (node != start) {
                for (Edge incoming_edge : incident_incoming_edges(node)) {
                    Node parent = incoming_edge.opposite(node);
                    late_start_and_finish_algorithm(parent, visited_nodes);
                }
            }
        }
    }

}
