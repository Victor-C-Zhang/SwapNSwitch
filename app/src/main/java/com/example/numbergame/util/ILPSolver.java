package com.example.numbergame.util;

import java.util.ArrayList;

import projects.multipath.ILP.Main;
import projects.multipath.ILP.MultiagentGraphSolverGurobiTime;
import projects.multipath.advanced.Graph;
import projects.multipath.advanced.Problem;

/**
 * Utility class containing static calculation methods
 */
public class ILPSolver {
    private ILPSolver() {}

    /**
     * Solve a complete n^2 problem (without holes)
     * @param start
     * @param goal
     * @return the number of moves required to solve the problem
     */
    public static int squareOptimalMoves(int[] start, int[] goal){
        MultiagentGraphSolverGurobiTime.bDebugInfo = false;
        MultiagentGraphSolverGurobiTime.bPrintPaths = true;
        int n = (int) Math.sqrt(start.length);
        Graph g = new Graph();
        for (int i=1;i<=n*n;i++){
            int sides[] = {i-n,i-1,i+1,i+n};
            ArrayList<Integer> edges = new ArrayList<>();
            for (int neighbor : sides)
                if (1<=neighbor && neighbor<=n*n) edges.add(neighbor);
            g.addVertex(i,edges);
        }
        g.finishBuildingGraph();
        System.out.println("Adjacency list:");
        Problem p = new Problem();
        p.graph = g;
        p.sg = new int[][]{start,goal};
        long arr[] = Main.solveProblem(p,true,-1);
        if (arr[0]>Integer.MAX_VALUE) return -1;
        return (int) arr[0];
    }

}
