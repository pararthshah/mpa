/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author pararth
 */
public class JTree {
    public int num_cliques = 0;
    public int max_clique = Integer.MIN_VALUE;
    
    public HashMap<Integer, HashSet<Integer> > nodes;
    public HashMap<Edge, HashSet<Integer> > separators;
    
    public HashMap<Integer, HashSet<Integer> > adjacency;
    
    public HashMap<Integer, HashSet<CPT2> > potentials;
    
    JTree(){
        nodes = new HashMap<Integer, HashSet<Integer>>();
        separators = new HashMap<Edge, HashSet<Integer>>();
        adjacency = new HashMap<Integer, HashSet<Integer>>();
        potentials = new HashMap<Integer, HashSet<CPT2>>();
    }
    
    JTree(JTree jt){
        nodes = new HashMap<Integer, HashSet<Integer>>(jt.nodes);
        separators = new HashMap<Edge, HashSet<Integer>>(jt.separators);
        adjacency = new HashMap<Integer, HashSet<Integer>>(jt.adjacency);
        potentials = new HashMap<Integer, HashSet<CPT2>>(jt.potentials);
        num_cliques = jt.num_cliques;
        max_clique = jt.max_clique;
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
    
    public void addClique(HashSet<Integer> new_nodes){
        boolean flag = true;
        HashMap<Integer,HashSet<Integer>> neigh = new HashMap<Integer, HashSet<Integer>>();
        for (int i = 0; i < num_cliques; i++){
            Set s = nodes.get(i);
            //System.err.println(new_nodes.toString());
            //System.err.println(i + " " + num_cliques);
            if (s.containsAll(new_nodes)){
                flag = false;
                break;
            }
            HashSet<Integer> intersection = new HashSet<Integer>(s);
            intersection.retainAll(new_nodes);
            neigh.put(i, intersection);
        }
        if (!flag) return;
        
        nodes.put(num_cliques, new_nodes);
        
        for (Integer i : neigh.keySet()){
            separators.put(new Edge(i,num_cliques,neigh.get(i).size()), neigh.get(i));
        }
        
        if (new_nodes.size() > max_clique) max_clique = new_nodes.size();
        
        if (!potentials.containsKey(num_cliques))
            potentials.put(num_cliques, new HashSet<CPT2>());
        
        num_cliques++;
    }
    
    public void calcMaxSpanningTree(){
        PriorityQueue<Edge> pq = new PriorityQueue<Edge>(separators.size(), new Comparator<Edge>() {
            @Override
            public int compare(Edge a, Edge b){
                return b.compareTo(a);
            }
        });
        
        for (Edge e : separators.keySet()) pq.add(e);
        
        HashMap<Integer,Integer> kruskal = new HashMap<Integer, Integer>();
        
        for (int i = 0; i < num_cliques; i++)
            kruskal.put(i, i);
        
        int count = num_cliques-1;
        
        while (count > 0){
            Edge next = pq.poll();
            if (next == null){
                System.err.println("Error: queue empty!");
                System.exit(1);
            }
            int x = next.a, y = next.b;
            int x1 = x, y1 = y;
            int c1 = 0, c2 = 0;
            while (x1 != kruskal.get(x1)) {c1++; x1 = kruskal.get(x1);}
            while (y1 != kruskal.get(y1)) {c2++; y1 = kruskal.get(y1);}
            if (x1 == y1) continue;
            addEdge(x,y);
            if (c1 < c2) kruskal.put(x1, y1);
            else kruskal.put(y1, x1);
            count--;
        }
    }
    
    public void addCPT(Integer n, CPT2 p){
        //if (!potentials.containsKey(n))
        //    potentials.put(n, new HashSet<CPT>());
        potentials.get(n).add(p);
    }
    
    public void print(){
        //System.out.println("Printing junction tree: ");
        
        for (int i = 0; i < num_cliques; i++){
            System.out.print(i + " : " + nodes.get(i).size() + " : ");
            for (Integer n : nodes.get(i)){
                System.out.print(n + " ");
            }
            
            /*
            System.out.print("; Edges: ");
            
            HashSet<Integer> neigh = adjacency.get(i);
            for (Integer n : neigh){
                System.out.print(n + " ");
            }
             */
             
            System.out.println();
        }
    }
    
    public int getMaxSepSize(){
        int ans = 0;
        for (HashSet<Integer> s : separators.values()){
            if (ans <= s.size()) ans = s.size();
        }
        return ans;
    }
    
    public void printSep(){
        for (Entry<Edge, HashSet<Integer> > e : separators.entrySet()){
            System.out.println(e.getKey().a + " " + e.getKey().b + " " + e.getValue().toString());
        }
    }
    
}
