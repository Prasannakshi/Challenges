import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Prasannakshi on 6/22/2018.
 */
public class Challenge1 {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String args[]){

        if (args.length == 0) {
            System.err.print("No command line argument found. Please provide file path as a command line argument");
            System.exit(-1);
        }

        if (args.length != 1) {
            System.err.println("More than one command line argument found.");
            System.exit(-1);
        }

        try {

            final Tree tree = MAPPER.readValue(new File(args[0]), Tree.class);
            final Node root = tree.getRoot();
            insertNewNode(tree, root, tree.getNewNode());

            System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(tree));
        } catch (Exception e) {
            System.err.println("Exception Encountered : " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void insertNewNode(Tree tree, Node root, int newValue) {
        Queue<Node> queue = new LinkedList<>();
        Node newNode =new Node(newValue);
        tree.addNode(newNode);
        queue.add(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            for (int i = 0; i < node.getChilds().size(); i++) {
                Node child = node.getChilds().get(i);
                queue.add(child);
            }
            if (node.getChilds().isEmpty()) {
                node.addChild(newNode);
                tree.addEdge(node.getValue(), newNode.getValue());
            }
        }
    }

    static final class Node {
        private final int value;
        private final List<Node> childs;

        public Node(int val){
            this.value = val;
            this.childs = new ArrayList<>();
        }

        public List<Node> getChilds() {
            return childs;
        }

        public void addChild(final Node n) {
            this.childs.add(n);
        }

        public int getValue() {
            return value;
        }
    }

    static final class Tree {

        private final List<Node> nodes;
        private final List<List<Integer>> edges;
        private final int newNode;

        @JsonCreator
        public Tree(@JsonProperty("nodes") final List<Node> nodes,
                    @JsonProperty("edges") final List<List<Integer>> edges,
                    @JsonProperty("newNode") final int newNode) {
            if (nodes == null || nodes.isEmpty()) {
                throw new IllegalArgumentException("Nodes are empty");
            }
            this.nodes = nodes;
            this.edges = (edges == null || edges.isEmpty()) ? new ArrayList<>() : edges;
            this.newNode = newNode;
            this.populate();
        }

        public List<Integer> getNodes() {
            return nodes.stream().map(Node::getValue).collect(Collectors.toList());
        }

        public List<List<Integer>> getEdges(){
            return edges;
        }

        @JsonIgnore
        public Node getRoot() {
            return nodes.get(0);
        }

        @JsonIgnore
        public int getNewNode() {
            return newNode;
        }

        public void addNode(final Node n) {
            nodes.add(n);
        }

        public void addEdge(int from, int to) {
            edges.add(Arrays.asList(from, to));
        }

        private void populate() {
            edges.stream()
                    .forEach(edge -> this.addEdge(edge.get(0), edge.get(1)));
        }

        private void addEdge(final Integer from, final Integer to) {
            Node fromNode = nodes.stream()
                    .filter(n -> n.getValue() == from)
                    .findAny()
                    .get();
            Node toNode = nodes.stream()
                    .filter(n -> n.getValue() == to)
                    .findAny()
                    .get();
            fromNode.addChild(toNode);
        }
    }
}
