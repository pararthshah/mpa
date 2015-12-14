/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author pararth
 */
public class UGM {
    public int num_nodes;
    public int num_edges;
    public int[] node_ranges;
    
    public int extra_edges = 0;
    
    public HashSet<Edge> edge_set;
    
    public HashMap<Integer, HashSet<Integer> > adjacency;
    
    UGM(){
        edge_set = new HashSet<Edge>();
        adjacency = new HashMap<Integer, HashSet<Integer> >();
    }
    
    public void addEdge(int a, int b){
        if (!adjacency.containsKey(a)){
            adjacency.put(a, new HashSet<Integer>());
        }
        adjacency.get(a).add(b);
        
        if (!adjacency.containsKey(b)){
            adjacency.put(b, new HashSet<Integer>());
        }
        adjacency.get(b).add(a);
    }
    
    public void calcAdjList(){
        for (Edge e : edge_set){
            addEdge(e.a,e.b);
        }
    }
    
    public boolean isClique(HashSet<Integer> nodes){
        for (Integer n1 : nodes){
            for (Integer n2 : nodes){
                if (n1 == n2) continue;
                if (!adjacency.get(n1).contains(n2)) return false;
            }
        }
        return true;
    }
    
    public void makeClique(HashSet<Integer> nodes){
        for (Integer n1 : nodes){
            for (Integer n2 : nodes){
                if (n1 == n2) continue;
                if (!adjacency.get(n1).contains(n2)){
                    addEdge(n1,n2);
                    extra_edges++;
                }
            }
        }
    }
    
    public void print(){
        System.out.println("Printing graph: ");
        for (int i = 0; i < num_nodes; i++){
            System.out.print("Node " + i + ": ");
            HashSet<Integer> neigh = adjacency.get(i);
            for (Integer n : neigh){
                System.out.print(n + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
