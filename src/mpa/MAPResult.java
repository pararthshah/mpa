/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

import java.util.TreeMap;

/**
 *
 * @author pararth
 */
public class MAPResult {
    public CPT2 cp;
    public TreeMap<Integer,Integer> labels;
    public double prob;
    public boolean is_valid;
    
    MAPResult(){
        cp = new CPT2();
        labels = new TreeMap<Integer, Integer>();
        prob = -1;
        is_valid = false;
    }
}
