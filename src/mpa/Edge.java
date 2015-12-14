/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mpa;

/**
 *
 * @author pararth
 */
public class Edge implements Comparable {
    public int a;
    public int b;
    
    public int w;
    
    Edge (int x, int y){
        a = x;
        b = y;
        w = 0;
    }
    
    Edge (int x, int y, int z){
        a = x;
        b = y;
        w = z;
    }

    @Override
    public int compareTo(Object o) {
        Edge e = (Edge) o;
        return ((Integer)w).compareTo((Integer)e.w);
    }
    
    @Override 
    public boolean equals(Object o){
        if (o == null) return false;
        Edge e = (Edge) o;
        if (a == e.a && b == e.b) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.a;
        hash = 11 * hash + this.b;
        return hash;
    }
}
