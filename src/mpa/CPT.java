/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author pararth
 */
public class CPT {
    public int num_nodes;
    public ArrayList<Integer> node_indices;
    public HashMap<ArrayList<Integer>, Double> value;
    
    CPT(){
        node_indices = new ArrayList<Integer>();
        value = new HashMap<ArrayList<Integer>, Double>();
    }
    
    CPT(CPT cp){
        node_indices = new ArrayList<Integer>(cp.node_indices);
        value = new HashMap<ArrayList<Integer>, Double>(cp.value);
        num_nodes = cp.num_nodes;
    }
    
    public CPT marginalize(Integer index){
        Integer loc = node_indices.indexOf(index);
        if (loc == -1) return this;
        
        CPT result = new CPT();
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
            if (!result.value.containsKey(b))
                result.value.put(b, value.get(a));
            else
                result.value.put(b, result.value.get(b) + value.get(a));
        }
        
        return result;
    }
    
    /*
    public MAPResult maximize(Integer index){
        MAPResult ans = new MAPResult();
        Integer loc = node_indices.indexOf(index);
        if (loc == -1) {
            System.out.println("exiting: index=" + index);
            print();
            ans.cp = this;
            return ans;
        }
        
        Integer max_label = -1;
        Double max_prob = 0.0;
        CPT result = new CPT();
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
        ans.label = max_label;
        ans.is_valid= true;
        
        return ans;
    }
    */
    public void print(){
        //System.out.println("Printing probability table: ");
        //System.out.println("Number of nodes: " + num_nodes);
        String head = "Pr(";
        int count = 0;
        for (Integer i : node_indices){
            System.out.print("X_" + i + "\t");
            head += "X_" + i;
            if (count < node_indices.size()-1) head += ",";
            count++;
        }
        head += ")";
        System.out.println(head);
        System.out.println();
        for (ArrayList<Integer> a : value.keySet()){
            for (Integer i : a){
                System.out.print(i + "\t");
            }
            System.out.println(value.get(a));
        }
        System.out.println();
    }
}
