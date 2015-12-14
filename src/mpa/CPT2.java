/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author pararth
 */
public class CPT2 {
    
    public static CPT2 join(CPT2 cp1, CPT2 cp2){
        if (cp1 == null || cp1.pot_values.isEmpty()) return cp2;
        if (cp2 == null || cp2.pot_values.isEmpty()) return cp1;
        
        if (cp2.node_indices.isEmpty() && cp2.pot_values.size() == 1){
            Double val = cp2.pot_values.get(0);
            ArrayList<Double> new_pot = new ArrayList<Double>();
            for (int i = 0; i < cp1.num_rows; i++){
                new_pot.add(val*cp1.pot_values.get(i));
            }
            cp1.pot_values = new_pot;
            return cp1;
        }
        
        if (cp1.node_indices.isEmpty() && cp1.pot_values.size() == 1){
            Double val = cp1.pot_values.get(0);
            ArrayList<Double> new_pot = new ArrayList<Double>();
            for (int i = 0; i < cp2.num_rows; i++){
                new_pot.add(val*cp2.pot_values.get(i));
            }
            cp2.pot_values = new_pot;
            return cp2;
        }
        
        HashSet<Integer> common = new HashSet<Integer>(cp1.node_indices);
        common.retainAll(cp2.node_indices);
        
        CPT2 result = new CPT2();
        
        result.node_indices = new HashSet<Integer>(cp1.node_indices);
        result.node_indices.addAll(cp2.node_indices);
        
        result.num_nodes = result.node_indices.size();
        
        for (Integer i : result.node_indices){
            result.node_values.put(i, new ArrayList<Integer>());
        }
        
        for (Integer i = 0; i < cp1.num_rows; i++){
            TreeMap<Integer,Integer> temp_row = new TreeMap<Integer, Integer>();
            for (Integer k : result.node_values.navigableKeySet()){
                if (cp1.node_indices.contains(k))
                    temp_row.put(k,cp1.node_values.get(k).get(i));
                else
                    temp_row.put(k,-1);
            }
            for (Integer j = 0; j < cp2.num_rows; j++){
                boolean flag = true;
                for (Integer k : result.node_values.navigableKeySet()){
                    if (cp2.node_indices.contains(k))
                        if (temp_row.get(k) == -1 || temp_row.get(k) == cp2.node_values.get(k).get(j))
                            temp_row.put(k,cp2.node_values.get(k).get(j));
                        else {
                            flag = false;
                            break;
                        }
                }
                if (flag){
                    for (Integer k : result.node_values.navigableKeySet()){
                        if (temp_row.get(k) == -1){
                            System.err.println("Error: undefined value in cpt!");
                            System.exit(1);
                        }
                        result.node_values.get(k).add(temp_row.get(k));
                    }
                    result.pot_values.add(cp1.pot_values.get(i)*cp2.pot_values.get(j));
                    result.num_rows++;
                }
            }
        }
        
        return result;
    }
    
    public int num_nodes;
    public int num_rows;
    
    public HashSet<Integer> node_indices;
    public TreeMap<Integer, ArrayList<Integer>> node_values;
    public ArrayList<Double> pot_values;
    
    CPT2(){
        node_indices = new HashSet<Integer>();
        node_values = new TreeMap<Integer, ArrayList<Integer>>();
        pot_values = new ArrayList<Double>();
        num_rows = 0;
        num_nodes = 0;
    }
    
    CPT2(CPT2 cp){
        node_indices = new HashSet<Integer>(cp.node_indices);
        node_values = new TreeMap<Integer, ArrayList<Integer>>(cp.node_values);
        pot_values = new ArrayList<Double>(cp.pot_values);
        num_nodes = cp.num_nodes;
        num_rows = cp.num_rows;
    }
    
    
    
    public CPT2 marginalize(Integer index){
        if (!node_indices.contains(index)) return this;
        
        CPT2 result = new CPT2();
        result.num_nodes = num_nodes-1;
        for (Integer i : node_indices){
            if (i == index) continue;
            result.node_indices.add(i);
            result.node_values.put(i, new ArrayList<Integer>());
        }
        
        TreeMap<String,Double> temp_values = new TreeMap<String, Double>();
        TreeMap<String,ArrayList<Integer> > orig_array = new TreeMap<String, ArrayList<Integer>>();
        
        for (Integer i = 0; i < num_rows; i++){
            ArrayList<Integer> temp_row = new ArrayList<Integer>();
            for (Integer j : node_values.navigableKeySet()){
                if (j == index) continue;
                temp_row.add(node_values.get(j).get(i));
            }
            String temp_str = temp_row.toString();
            if (!temp_values.containsKey(temp_str)){
                temp_values.put(temp_str,pot_values.get(i));
                orig_array.put(temp_str, temp_row);
            }
            else
                temp_values.put(temp_str,pot_values.get(i) + temp_values.get(temp_str));
        }
        
        result.num_rows = temp_values.size();
        
        for (String s : temp_values.navigableKeySet()){
            ArrayList<Integer> orig = orig_array.get(s);
            int count = 0;
            for (Integer i : result.node_values.navigableKeySet()){
                result.node_values.get(i).add(orig.get(count));
                count++;
            }
            result.pot_values.add(temp_values.get(s));
        }
                
        return result;
    }
    /*
    public MAPResult maximize(HashSet<Integer> indices){
        MAPResult ans = new MAPResult();
        
        if (!node_indices.containsAll(indices)) {
            System.out.println("exiting: indices = " + indices.toString());
            print();
            ans.cp = this;
            return ans;
        }
        
        TreeMap<Integer,Integer> max_labels = new TreeMap<Integer, Integer>();
        Double max_prob = 0.0;
        CPT2 result = new CPT2();
        result.num_nodes = num_nodes-1;
        for (Integer i : node_indices){
            if (i == index) continue;
            result.node_indices.add(i);
        }
        
        for (ArrayList<Integer> a : value.keySet()){
            ArrayList<Integer> b = new ArrayList<Integer>();
            Integer n = -1;
            for (int i = 0; i < a.size(); i++){
                if (i == loc){
                    n = a.get(i);
                } else {
                    b.add(a.get(i));
                }
            }
            if (!result.value.containsKey(b)){
                result.value.put(b, value.get(a));
                max_label = n;
            }
            else{
                if (result.value.get(b) < value.get(a)){
                    result.value.put(b, value.get(a));
                    max_label = n;
                }
            }
                
        }
        
        System.out.println("maximize cp:");
        print();
        System.out.println("index: " + index + " max_label: " + max_label);
        System.out.println("result");
        result.print();
        
        ans.cp = result;
        ans.labels = max_labels;
        ans.is_valid= true;
        
        return ans;
    }
    */
    
    public void print(){
        /*System.out.println("Printing probability table: ");
        System.out.println("Number of nodes: " + num_nodes);
        System.out.println("Number of rows: " + num_rows + " " + pot_values.toString());
        for (Integer j : node_values.keySet()){
                System.out.println(j + ": " + node_values.get(j).toString());
            }
        */
        
        String head = "Pr(";
        int count = 0;
        for (Integer i : node_values.keySet()){
            System.out.print("X_" + i + "\t");
            head += "X_" + i;
            if (count < node_values.keySet().size()-1) head += ",";
            count++;
        }
        head += ")";
        System.out.println(head);
        System.out.println();
        for (int i = 0; i < num_rows; i++){
            for (Integer j : node_values.keySet()){
                System.out.print(node_values.get(j).get(i) + "\t");
            }
            System.out.println(pot_values.get(i));
        }
        
    }
}
