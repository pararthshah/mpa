/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pararth
 */
public class MPA {

    /*
     * readGraph()
     * triangulateGraph()
     * createJunctionTree()
     * computeMAP()
     * computeMarginals()
     */
    
    public static UGM graph = new UGM();
    public static ArrayList<CPT2> potentials = new ArrayList<CPT2>();
    public static JTree junc_tree;
    public static Map<Integer, Integer> ordering = null;
    
    public static Double z_val;
    public static Double MAP_prob;
    
    public static void readGraph(String filename){
        graph = new UGM();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String sr = br.readLine();
            String[] toks = sr.split(" ");
            //System.out.println("'" + toks[0] + "'");
            graph.num_nodes = Integer.parseInt(toks[0]);
            
            sr = br.readLine();
            toks = sr.split(" ");
            graph.num_edges = Integer.parseInt(toks[0]);
            
            graph.node_ranges = new int[graph.num_nodes];
            for (int i = 0; i < graph.num_nodes; i++){
                sr = br.readLine();
                toks = sr.split(" ");
                graph.node_ranges[i] = Integer.parseInt(toks[0]);
            }
            
            for (int i = 0; i < graph.num_edges; i++){
                sr = br.readLine();
                toks = sr.split(" ");
                int x = Integer.parseInt(toks[0]);
                int y = Integer.parseInt(toks[1]);
                graph.edge_set.add(new Edge(x,y));
            }
        } catch (Exception e) {
            System.err.println("error: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void readPotentials(String filename){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String sr = br.readLine();
            boolean flag = false;
            CPT2 pot = null;
            ArrayList<Integer> indices = null;
            while (sr != null){
                String[] toks = sr.split(" ");
                if (flag){
                    if ("#".equals(toks[0])){
                        flag = false;
                        potentials.add(pot);
                        continue;
                    }
                    ArrayList<Integer> temp = new ArrayList<Integer>();
                    for (int i = 0; i < pot.num_nodes; i++){
                        temp.add(Integer.parseInt(toks[i]));
                        pot.node_values.get(indices.get(i)).add(Integer.parseInt(toks[i]));
                    }
                    pot.pot_values.add(Double.parseDouble(toks[pot.num_nodes]));
                    pot.num_rows++;
                    
                } else {
                    if (!"#".equals(toks[0])){
                        System.err.println("Error: bad input file");
                        System.exit(1);
                    }
                    pot = new CPT2();
                    indices = new ArrayList<Integer>();                            
                    pot.num_nodes = toks.length - 1;
                    for (int i = 0; i < pot.num_nodes; i++){
                        pot.node_values.put(Integer.parseInt(toks[i+1]), new ArrayList<Integer>());
                        indices.add(Integer.parseInt(toks[i+1]));
                    }
                    pot.node_indices = new HashSet<Integer>(indices);
                    flag = true;
                }
                sr = br.readLine();
            }
        } catch (Exception e) {
            System.err.println("error: " + e);
            System.exit(1);
        }
    }
    
    public static void triangulateGraphMCS(){
        
        //System.out.println("Triangulating graph");
        
        ordering = null;
        boolean triangulated = false;
        int count = 0;
        
        while (!triangulated){
            //System.out.println("Triangulation iteration " + count++);
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            ordering = new HashMap<Integer, Integer>();
            
            for (int i = 0; i < graph.num_nodes; i++) map.put(i, 0);
            
            triangulated = true;
            
            for (int i = graph.num_nodes-1; i >= 0 ; i--){
                Entry<Integer, Integer> max = Collections.max(map.entrySet(), new Comparator<Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2) {
                        return entry1.getValue().compareTo(entry2.getValue());
                    }
                });
                
                ordering.put(i, max.getKey());
                map.remove(max.getKey());
                
                HashSet<Integer> neighbours = graph.adjacency.get(max.getKey());
                HashSet<Integer> neigh = new HashSet<Integer>();
                
                for (Integer n : neighbours){
                    if (map.containsKey(n)) {
                        map.put(n, map.get(n)+1);
                    } else {
                        neigh.add(n);
                    }
                }
                
                if (!graph.isClique(neigh)){
                    graph.makeClique(neigh);
                    triangulated = false;
                    //System.out.println("Not a clique: " + neigh.toString());
                    break;
                }
                
            }
            
        }
        
        /*
        System.out.print("Ordering: ");
        for (int i = 0; i < graph.num_nodes; i++){
            System.out.print(ordering.get(i) + " ");
        }
        System.out.println();
        System.out.println("Number of extra edges added: " + graph.extra_edges);
        System.out.println();
        */
    }
        
    public static void createJunctionTree(){
        junc_tree = new JTree();
        
        //System.out.println("Creating junction tree from graph");
        
        HashSet<Integer> visited = new HashSet<Integer>();
        
        for (int i = 0; i < graph.num_nodes; i++){
            Integer next = ordering.get(i);
            HashSet<Integer> neigh = graph.adjacency.get(next);
            neigh.removeAll(visited);
            if (neigh != null) {
                neigh.add(next);
                junc_tree.addClique(neigh);
            }
            visited.add(next);
        }
        
        junc_tree.calcMaxSpanningTree();
        
        //System.out.println("Size of largest clique: " + junc_tree.max_clique);
        //System.out.println("Size of largest sepset: " + junc_tree.getMaxSepSize());
        //System.out.println();
        
        //assign potentials
        for (CPT2 p : potentials){
            Set s = new HashSet(p.node_indices);
            for (Integer k : junc_tree.nodes.keySet()){
                if (junc_tree.nodes.get(k).containsAll(s)){
                    junc_tree.addCPT(k, p);
                }
            }
        }
    }
    
    
    public static HashMap<Integer,Integer> calculateMAP(){
        Integer root = 0;
        HashMap<Integer,Integer> ans = new HashMap<Integer, Integer>();
        
        JTree my_junc_tree = new JTree(junc_tree);
                
        //assign message passing order
        HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
        Queue<Integer> q = new LinkedList<Integer>();
        ArrayList<Integer> order = new ArrayList<Integer>();
        
        q.add(root);
        
        while (!q.isEmpty()){
            Integer top = q.poll();
            order.add(top);
            for (Integer n : my_junc_tree.adjacency.get(top)){
                if (!parents.containsKey(n) && n != root){
                    parents.put(n, top);
                    q.add(n);
                }
            }
        }
        
        Collections.reverse(order);
        
        for (Integer i : order){
            System.out.println("i: " + i);
            if (i == root) break;
            Integer p = parents.get(i);
            int x = i, y = p;
            if (x > y){
                int temp = x;
                x = y;
                y = temp;
            }
            
            CPT2 temp_cpt = new CPT2();
            for (CPT2 cp : my_junc_tree.potentials.get(i)){
                temp_cpt = CPT2.join(temp_cpt, cp);
            }
            
            HashSet<Integer> sep = new HashSet<Integer>(my_junc_tree.nodes.get(i));
            sep.removeAll(my_junc_tree.separators.get(new Edge(x,y)));
            
            MAPResult res = temp_cpt.maximize(sep);
            temp_cpt = res.cp;
            ans.putAll(res.labels);
            
            my_junc_tree.potentials.get(p).add(temp_cpt);
            
        }
        
        CPT2 temp_cpt = new CPT2();
        for (CPT2 cp : my_junc_tree.potentials.get(root)){
            temp_cpt = CPT2.join(temp_cpt, cp);
        }
        
        MAPResult res = temp_cpt.maximize(my_junc_tree.nodes.get(root));
        ans.putAll(res.labels);
        
               
        return ans;
    }
    
    
    public static CPT2 calculateMarginals(HashSet<Integer> inp){
        Integer root = -1;
        
        JTree my_junc_tree = new JTree(junc_tree);
        
        for (Integer i : my_junc_tree.nodes.keySet()){
            if (my_junc_tree.nodes.get(i).containsAll(inp)){
                root = i;
                break;
            }
        }
        
        if (root == -1){
            System.err.println("Error: no clique contains input set of nodes!");
            System.exit(1);
        }
        
        //assign message passing order
        HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
        Queue<Integer> q = new LinkedList<Integer>();
        ArrayList<Integer> order = new ArrayList<Integer>();
        
        q.add(root);
        
        while (!q.isEmpty()){
            Integer top = q.poll();
            order.add(top);
            for (Integer n : my_junc_tree.adjacency.get(top)){
                if (!parents.containsKey(n) && n != root){
                    parents.put(n, top);
                    q.add(n);
                }
            }
        }
        
        Collections.reverse(order);
        
        for (Integer i : order){
            if (i == root) break;
            Integer p = parents.get(i);
            int x = i, y = p;
            if (x > y){
                int temp = x;
                x = y;
                y = temp;
            }
            HashSet<Integer> sep = new HashSet<Integer>(my_junc_tree.nodes.get(i));
            sep.removeAll(my_junc_tree.separators.get(new Edge(x,y)));
            for (CPT2 cp : my_junc_tree.potentials.get(i)){
                for (Integer s : sep){
                    if (inp.contains(s)) continue;
                    cp = cp.marginalize(s);
                }
                my_junc_tree.addCPT(p,cp);
            }
        }
        
        HashSet<Integer> inv = new HashSet<Integer>(my_junc_tree.nodes.get(root));
        inv.removeAll(inp);
        
        for (CPT2 cp : my_junc_tree.potentials.get(root)){
            for (Integer s : inv){
                cp = cp.marginalize(s);
            }
        }
        
        CPT2 ans = new CPT2();
        ans.num_nodes = inp.size();
        ans.node_indices = new HashSet<Integer>(inp);
                
        for (CPT2 cp : my_junc_tree.potentials.get(root)){
            ans = CPT2.join(ans,cp);
        }
        
        return ans;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dir = "/home/pararth/Projects/cs726/";
        String gr_file = "graph.log";
        String cpt_file = "potentials.log";
                
        readGraph(dir + gr_file);
        readPotentials(dir + cpt_file);
        graph.calcAdjList();
        //graph.print();
        
        triangulateGraphMCS();
        //graph.print();
        System.out.print(graph.extra_edges + " ");
        
        createJunctionTree();
        System.out.println(junc_tree.max_clique + " " + junc_tree.getMaxSepSize());
        System.out.println();
        junc_tree.print();
        System.out.println();
        //junc_tree.printSep();
        
        //HashMap<Integer,Integer> map = calculateMAP();
        System.out.println("MAP vector: ");
        System.out.println();
        
        try {
            System.out.print("Enter nodes whose marginal probability table is required: ");
        
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            String inp_str = br.readLine();
            String[] inp_arr = inp_str.split(" ");
            HashSet<Integer> inp = new HashSet<Integer>();
            for (String inp_tok : inp_arr){
                inp.add(Integer.parseInt(inp_tok));
            }
            
            CPT2 ans = calculateMarginals(inp);
            System.out.println();
            ans.print();
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
        
        
    }
}
