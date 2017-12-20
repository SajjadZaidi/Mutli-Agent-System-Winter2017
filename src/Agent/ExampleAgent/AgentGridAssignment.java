package Agent.ExampleAgent;

import Ares.Common.AgentID;
import Ares.Common.Location;
import Ares.Common.World.Grid;
import Ares.Common.World.World;

import java.util.*;

import static Ares.Common.Direction.getDirection;

/**
 * Created by lion on 3/24/17.
 */
public class AgentGridAssignment {
    private static final int NUM_INIT_AGENTS = 7;
    private static final int AGENT_TEAM_SIZE = 2;
    private static final int PRIORITY_GRID_CHANCE_THRESHOLD = 40;

    private static Set<Location> assumedFinishedLocations = new HashSet<>();

    public static Map<AgentID, Location> run(Map<AgentID, Location> agentLocations, World world) {
        HashMap<AgentID, Location> agentGridAssignments = new HashMap<>();

        ArrayList<Grid> explorableGrids = getExplorableGrids(world);

        if(agentLocations.values().size() < NUM_INIT_AGENTS) { // determining routine assignments
            HashMap<AgentID, AgentID> agentPairs = findAgentPairs(agentLocations);
            makeOneAgentLocationFromPaired(agentLocations, agentPairs);

            HashMap<AgentID, Location> highPriorityAssignments = makeClosestGridAssignments(agentLocations, world, getHighPriorityGrids(explorableGrids));
            agentGridAssignments.putAll(highPriorityAssignments);

            filterOutAgentLocationsByValidAssignments(agentLocations, highPriorityAssignments);

            if(agentLocations.size() > 0) {
                HashMap<AgentID, Location> lowPriorityAssignments = makeClosestGridAssignments(agentLocations, world, getLowPriorityGrids(explorableGrids));
                agentGridAssignments.putAll(lowPriorityAssignments);
                filterOutAgentLocationsByValidAssignments(agentLocations, lowPriorityAssignments);
            }

            if(agentLocations.size() > 0) {
                HashMap<AgentID, Location> zeroPriorityAssignments = makeClosestGridAssignments(agentLocations, world, getZeroPriorityGrids(explorableGrids));
                agentGridAssignments.putAll(zeroPriorityAssignments);

                // The following code won't be used until we're confident that savage mode is more effective than master assignment
                //HashMap<AgentID, Location> nullAssignments = makeNullAssignments(agentLocations.keySet());
                //agentGridAssignments.putAll(nullAssignments);
            }

            makePairedAssignments(agentGridAssignments, agentPairs);
        } else { // determining initial assignments
            HashMap<AgentID, Location> assignments = new HashMap<>();

            ArrayList<Grid> explorableGridsDecsOrderByChance = descSortGridsByChance(explorableGrids);

            for(int i = 0; i < explorableGridsDecsOrderByChance.size(); i++) {
                for(int k = 0; k < AGENT_TEAM_SIZE; k++) {
                    Grid agentGrid = findClosestTargetGridToStartingGird(explorableGridsDecsOrderByChance.get(i), getGridsByLocations(agentLocations.values(), world), world);

                    if(agentGrid != null) {
                        AgentID agentId = getAgentIdByGrid(agentLocations, agentGrid);
                        assignments.put(agentId, explorableGridsDecsOrderByChance.get(i).getLocation());
                        agentLocations.remove(agentId);
                    } else {
                        nullifyLoneAssignments(assignments);
                    }
                }
                if(isThereEnoughPairAssignments(assignments)) break;
            }

            HashMap<AgentID, Location> nullAssignments = makeNullAssignments(agentLocations.keySet());

            agentGridAssignments.putAll(assignments);
            agentGridAssignments.putAll(nullAssignments);

            filterOutMaster(agentGridAssignments);
        }

        assumeAssignedLocationsFinished(agentGridAssignments);

        return agentGridAssignments;
    }

    private static void assumeAssignedLocationsFinished(Map<AgentID, Location> assignments) {
        for(Map.Entry<AgentID, Location> assignment : assignments.entrySet()){
            if (assignment.getValue() != null) {
                assumedFinishedLocations.add(assignment.getValue());
            }
        }
    }

    private static void filterOutAgentLocationsByValidAssignments(Map<AgentID, Location> agentLocations, Map<AgentID, Location> assignments) {
        for(Map.Entry<AgentID, Location> assignment : assignments.entrySet()){
            if (assignment.getValue() != null) {
                agentLocations.remove(assignment.getKey());
            }
        }
    }

    private static HashMap<AgentID, AgentID> findAgentPairs(Map<AgentID, Location> agentLocations) {
        HashMap<AgentID, AgentID> pairs = new HashMap<>();
        HashMap<Location, AgentID> locationsSeen = new HashMap<>();

        for(Map.Entry<AgentID, Location> agentLocation : agentLocations.entrySet()){
            if (locationsSeen.containsKey(agentLocation.getValue())) {
                pairs.put(agentLocation.getKey(), locationsSeen.get(agentLocation.getValue()));
            } else {
                locationsSeen.put(agentLocation.getValue(), agentLocation.getKey());
            }
        }

        return pairs;
    }

    private static void makeOneAgentLocationFromPaired(Map<AgentID, Location> agentLocations, HashMap<AgentID, AgentID> pairs) {
        for(Map.Entry<AgentID, AgentID> pair : pairs.entrySet()) {
            agentLocations.remove(pair.getValue());
        }
    }

    private static void makePairedAssignments(Map<AgentID, Location> agentLocations, HashMap<AgentID, AgentID> pairs) {
        for(Map.Entry<AgentID, AgentID> pair : pairs.entrySet()) {
            agentLocations.put(pair.getValue(), agentLocations.get(pair.getKey()));
        }
    }

    private static HashMap<AgentID, Location> makeClosestGridAssignments(Map<AgentID, Location> agentLocations, World world, ArrayList<Grid> grids) {
        HashMap<AgentID, Location> assignments = new HashMap<>();

        for(Map.Entry<AgentID,Location> agentLocation : agentLocations.entrySet()) {
            Grid closestGrid = findClosestTargetGridToStartingGird(world.getGridAt(agentLocation.getValue()), grids, world);
            if (closestGrid != null) {
                assignments.put(agentLocation.getKey(), closestGrid.getLocation());
                grids.remove(closestGrid);
            } else {
                assignments.put(agentLocation.getKey(), null);
            }
        }

        return assignments;
    }

    private static ArrayList<Grid> getHighPriorityGrids(ArrayList<Grid> grids) {
        ArrayList<Grid> priorityGrids = new ArrayList<>();

        for(Grid grid : grids) {
            if (grid.getPercentChange() > PRIORITY_GRID_CHANCE_THRESHOLD) {
                priorityGrids.add(grid);
            }
        }

        return priorityGrids;
    }

    private static ArrayList<Grid> getLowPriorityGrids(ArrayList<Grid> grids) {
        ArrayList<Grid> priorityGrids = new ArrayList<>();

        for(Grid grid : grids) {
            if (grid.getPercentChange() > 0 && grid.getPercentChange() <= PRIORITY_GRID_CHANCE_THRESHOLD) {
                priorityGrids.add(grid);
            }
        }

        return priorityGrids;
    }

    private static ArrayList<Grid> getZeroPriorityGrids(ArrayList<Grid> grids) {
        ArrayList<Grid> priorityGrids = new ArrayList<>();

        for(Grid grid : grids) {
            if (grid.getPercentChange() == 0) {
                priorityGrids.add(grid);
            }
        }

        return priorityGrids;
    }

    private static boolean isThereEnoughPairAssignments(HashMap<AgentID, Location> assignments) {
        int numLoneAssignments = Collections.frequency(assignments.values(), null);
        int numPairAssignments = assignments.size() - numLoneAssignments;

        if (numPairAssignments == nearestSmallEvenInt(NUM_INIT_AGENTS - numLoneAssignments)) return true;
        else return false;
    }

    private static int nearestSmallEvenInt(int to) {
        return (to % 2 == 0) ? to : (to - 1);
    }

    private static HashMap<AgentID, Location> makeNullAssignments(Set<AgentID> agentIds) {
        HashMap<AgentID, Location> assignments = new HashMap<>();

        for(AgentID id : agentIds) {
            assignments.put(id, null);
        }

        return assignments;
    }

    private static void filterOutMaster(HashMap<AgentID, Location> assignments) {
        for(Map.Entry<AgentID, Location> entry: assignments.entrySet()) {
            if (entry.getValue() == null) {
               assignments.remove(entry.getKey());
               return;
            }
        }
    }

    private static void nullifyLoneAssignments(Map<AgentID, Location> assignments) {
        for(Map.Entry<AgentID, Location> entry: assignments.entrySet()) {
            if (Collections.frequency(assignments.values(), entry.getValue()) == 1) {
                assignments.put(entry.getKey(), null);
            }
        }
    }

    private static Grid findClosestTargetGridToStartingGird(Grid startingGrid, ArrayList<Grid> targetGrids, World world) {
        int[][] visitedGrids = new int[world.getRows()][world.getCols()];
        visitedGrids = markGridAsVisited(visitedGrids, startingGrid);

        Queue<Grid> workingQ = new LinkedList<>();
        workingQ.add(startingGrid);

        while(!workingQ.isEmpty()) {
            Grid grid = workingQ.remove();

            if(targetGrids.contains(grid)) return grid;

            for(int i = 0; i < 8; i++) {
                Grid neighborGrid = world.getGridNeighbours(getDirection(i), grid.getLocation());

                if (isGridReachable(neighborGrid) && !isGridVisited(visitedGrids, neighborGrid)) {
                    visitedGrids = markGridAsVisited(visitedGrids, neighborGrid);
                    workingQ.add(neighborGrid);
                }
            }
        }

        return null;
    }

    private static AgentID getAgentIdByGrid(Map<AgentID, Location> agentLocations, Grid grid) {
        Iterator agentLocationsIter = agentLocations.keySet().iterator();

        while (agentLocationsIter.hasNext()) {
            AgentID agentId = (AgentID)agentLocationsIter.next();
            Location agentLocation = agentLocations.get(agentId);
            if (agentLocation.equals(grid.getLocation())) {
                return agentId;
            }
        }

        return null;
    }

    private static ArrayList<Grid> getGridsByLocations(Collection<Location> locations, World world) {
        ArrayList<Grid> grids = new ArrayList<>();

        for (Iterator iterator = locations.iterator(); iterator.hasNext();) {
            grids.add(world.getGridAt((Location)iterator.next()));
        }

        return grids;
    }

    private static boolean isGridReachable(Grid grid) {
        if (grid == null || grid.isKiller()) return false;
        return true;
    }

    private static int[][] markGridAsVisited(int[][] visitedGrids, Grid grid) {
        visitedGrids[grid.getLocation().getRow()][grid.getLocation().getCol()] = 1;
        return visitedGrids;
    }

    private static boolean isGridVisited(int[][] visitedGrids, Grid grid) {
        return visitedGrids[grid.getLocation().getRow()][grid.getLocation().getCol()] == 1;
    }


    private static ArrayList<Grid> descSortGridsByChance(ArrayList<Grid> grids) {
        Collections.sort(grids, new Comparator<Grid>() {
            @Override
            public int compare(Grid a, Grid b) {
                return b.getPercentChange() - a.getPercentChange();
            }
        });

        return grids;
    }

    private static ArrayList<Grid> getExplorableGrids(World world) {
        ArrayList<Grid> grids = new ArrayList<>();

        for(int i = 0; i < world.getRows(); i++) {
            for (int j = 0; j < world.getCols(); j++) {
                if(!world.getGrid()[i][j].isKiller() && !assumedFinishedLocations.contains(world.getGrid()[i][j].getLocation()) ) {
                    grids.add(world.getGrid()[i][j]);
                }
            }
        }

        return grids;
    }

}