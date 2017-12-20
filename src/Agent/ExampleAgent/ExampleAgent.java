package Agent.ExampleAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import Agent.BaseAgent;
import Agent.Brain;
import Agent.Cipher;
import Agent.LogLevels;
import Ares.Common.AgentID;
import Ares.Common.AgentIDList;
import Ares.Common.Direction;
import Ares.Common.LifeSignals;
import Ares.Common.Location;
import Ares.Common.Commands.AgentCommand;
import Ares.Common.Commands.AgentCommands.END_TURN;
import Ares.Common.Commands.AgentCommands.MOVE;
import Ares.Common.Commands.AgentCommands.OBSERVE;
import Ares.Common.Commands.AgentCommands.SAVE_SURV;
import Ares.Common.Commands.AgentCommands.SEND_MESSAGE;
import Ares.Common.Commands.AgentCommands.SLEEP;
import Ares.Common.Commands.AgentCommands.TEAM_DIG;
import Ares.Common.Commands.AresCommands.FWD_MESSAGE;
import Ares.Common.Commands.AresCommands.MOVE_RESULT;
import Ares.Common.Commands.AresCommands.OBSERVE_RESULT;
import Ares.Common.Commands.AresCommands.SAVE_SURV_RESULT;
import Ares.Common.Commands.AresCommands.TEAM_DIG_RESULT;
import Ares.Common.World.Grid;
import Ares.Common.World.Info.BottomLayerInfo;
import Ares.Common.World.Info.GridInfo;
import Ares.Common.World.Info.RubbleInfo;
import Ares.Common.World.Info.SurroundInfo;
import Ares.Common.World.Info.SurvivorGroupInfo;
import Ares.Common.World.Info.SurvivorInfo;
import Ares.Common.World.Objects.BottomLayer;
import Ares.Common.World.Objects.Rubble;
import Ares.Common.World.Objects.Survivor;
import Ares.Common.World.Objects.SurvivorGroup;
import Ares.Common.World.Objects.WorldObject;
import algorithms.a_star.AStar;

public class ExampleAgent extends Brain {

	// private Random random = new Random(12345);
	private int round = 1;
	private static BaseAgent agent = BaseAgent.getBaseAgent();
	private List<AgentCommand> list = new ArrayList<>();
	Map<Location, Grid> local_map_location = new HashMap<Location, Grid>();// not
	// used
	Map<String, Grid> local_map_string = new HashMap<String, Grid>();
	Map<AgentID, Location> IntitialAgentsLocation = new HashMap<AgentID, Location>();

	// Protocol
	boolean IAmMaster = false;
	Map<AgentID, Location> AgentsLocation = new HashMap<AgentID, Location>();
	AgentID mypair;
	AgentID masterid;

	enum state {
		digstate, sleep, RequestTask, MoveToDestination, SaveSurviviors, Master, Wait, Savage;
	}

	static state CurrentState = state.MoveToDestination;
	boolean digstate = false;
	boolean sleepstate = false;
	Location destination;
	boolean alertrnate = true;
	Map<AgentID, Location> AgentsTask = null;
	Map<String, Grid> local_map_new_stuff = new HashMap<String, Grid>();// Contains
																		// only
																		// info
																		// which
																		// was
																		// not
																		// sent
																		// or is
																		// required
	// String json2 =
	// get_seerialzed_message(1,local_map_string,null,null,null,null);
	// String json2 = get_serialzed_message(2,null,locations,null,null,null);
	// String json2 =
	// get_serialzed_message(4,local_map_string,null,Requestedtask,null,null);
	// String json2 =
	// get_serialzed_message(4,local_map_string,null,null,agentstasks,null);

	public String get_serialzed_message(int type, Map<String, Grid> local_map, Location grid, String request,
			List<ListAllAgentTask> tasks, AgentID agentid) {
		try {
			String json = "Unknown Type";
			switch (type) {
			case 1:// LOCALMAP
				json = seriliaze_local_map(local_map);
				break;
			case 2:// LOCATION
				json = seriliaze_Location(grid);
				break;
			case 3:// TASKREQUEST
				json = seriliaze_RequestTask(local_map, request);// request
																	// could be
																	// any
																	// string
																	// from
																	// slave to
																	// master in
																	// case we
																	// need it
				break;
			case 4:// TASKASSIGNMENT_ALL
				json = seriliaze_TaskAssignment_ALL(local_map, tasks);// sends
																		// Map
																		// and
																		// task
																		// to
																		// all
																		// agent
																		// or a
																		// single
																		// one.
				break;
			case 5:// Master announcement Savage mode TODO: This is savage mode
				json = seriliaze_savage(local_map, tasks);
				;
				break;
			}

			json = Compression.compressString(json);
			return Cipher.encrypt(json);
		} // json;}//
		catch (Exception e) {
			return null;
		}
	}

	public Object get_deserialzed_message(int type, String json) {// not being
																	// used
																	// write
																	// now.I
																	// have not
																	// tested
																	// this
		try {
			Object deserialized;
			switch (type) {
			case 1:// LOCALMAP
				deserialized = deseriliaze_local_map(json);
				break;
			case 2:// LOCATION
				deserialized = deseriliaze_Location(json);
				break;
			case 3:// TASKREQUEST
				deserialized = deseriliaze_RequestTask(json);
				break;
			case 4:// TASKASSIGNMENT_ALL or specific
				deserialized = deseriliaze_TaskAssignment_ALL(json);
				break;
			case 5:// Master Last Announcement
				deserialized = deseriliaze_savage(json);
				break;
			default:
				deserialized = "Garbage";
				break;

			}

			return deserialized;
		} catch (Exception e) {
			return null;
		}
	}

	public String seriliaze_local_map(Map<String, Grid> local_map) {
		RuntimeTypeAdapterFactory<WorldObject> adapter = RuntimeTypeAdapterFactory.of(WorldObject.class)
				.registerSubtype(WorldObject.class).registerSubtype(Rubble.class).registerSubtype(Survivor.class)
				.registerSubtype(SurvivorGroup.class).registerSubtype(BottomLayer.class);
		Gson gson2 = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

		String json2 = gson2.toJson(local_map);
		json2 = ("'" + "Local Map :" + "'").concat(json2);
		return json2;
	}

	public Map<String, Grid> deseriliaze_local_map(String json) {
		java.lang.reflect.Type listType = new TypeToken<Map<String, Grid>>() {
		}.getType();
		RuntimeTypeAdapterFactory<WorldObject> adapter = RuntimeTypeAdapterFactory.of(WorldObject.class)
				.registerSubtype(WorldObject.class).registerSubtype(Rubble.class).registerSubtype(Survivor.class)
				.registerSubtype(SurvivorGroup.class).registerSubtype(BottomLayer.class);
		Gson gson2 = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();
		// Update user map

		Map<String, Grid> local_map = gson2.fromJson(json, listType);
		return local_map;

	}

	public String seriliaze_Location(Location grid) {
		Gson gson2 = new Gson();
		String json2 = gson2.toJson(grid, Location.class);
		json2 = ("'" + "Location :" + "'").concat(json2);
		return json2;
	}

	public Location deseriliaze_Location(String json) {
		Gson gson2 = new Gson();
		Location json2 = gson2.fromJson(json, Location.class);
		return json2;
	}

	public String seriliaze_savage(Map<String, Grid> local_map, List<ListAllAgentTask> tasks) {
		RuntimeTypeAdapterFactory<WorldObject> adapter = RuntimeTypeAdapterFactory.of(WorldObject.class)
				.registerSubtype(WorldObject.class).registerSubtype(Rubble.class).registerSubtype(Survivor.class)
				.registerSubtype(SurvivorGroup.class).registerSubtype(BottomLayer.class);
		Gson gson2 = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

		String json2 = gson2.toJson(local_map);
		json2 = ("'" + "Local Map RandomExploration:" + "'").concat(json2);

		json2 = ("'" + "RandomExploration:" + "'").concat(json2);
		for (ListAllAgentTask task : tasks) {
			String agenttask = ("'" + "Agent ID:" + task.agentid.getID() + "'").concat(task.RandomExploration);
			json2 = json2.concat(agenttask);
		}
		return json2;

	}

	public deserilizedAsignmentTaskObject deseriliaze_savage(String json) {
		String AgentId = "'" + "Agent ID:" + agent.getAgentID().getID() + "'";
		if (!(json.contains(("'" + "Local Map RandomExploration:" + "'")) && json.contains((AgentId)))) {
			return null;
		}
		deserilizedAsignmentTaskObject dato = new deserilizedAsignmentTaskObject();
		int size_LocalMap = json.indexOf(("'" + "Local Map RandomExploration:" + "'"))
				+ ("'" + "Local Map RandomExploration:" + "'").length();
		int size_Task = json.indexOf(("'" + "Agent ID:")) + ("'" + "Agent ID:").length();
		// TODO: Why is this not used?
		int index_Task = json.indexOf(("'" + "Agent ID:"));
		String localmap = json.substring(size_LocalMap, index_Task);
		Map<String, Grid> map = deseriliaze_local_map(localmap);
		int size = json.indexOf(AgentId);
		int sizelength = AgentId.length();
		int index = size + sizelength;
		// String location = json.substring(index, index + 9);//hardcoded
		dato.local_map = map;

		return dato;
	}

	public String seriliaze_RequestTask(Map<String, Grid> local_map, String request) {
		RuntimeTypeAdapterFactory<WorldObject> adapter = RuntimeTypeAdapterFactory.of(WorldObject.class)
				.registerSubtype(WorldObject.class).registerSubtype(Rubble.class).registerSubtype(Survivor.class)
				.registerSubtype(SurvivorGroup.class).registerSubtype(BottomLayer.class);
		Gson gson2 = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

		String json2 = gson2.toJson(local_map);
		json2 = ("'" + "Local Map request task:" + "'").concat(json2);

		json2 = ("'" + "REQUEST TASK :" + "'").concat(json2);
		json2 = json2.concat("'" + "{TASK} :" + "'" + request);
		return json2;
	}

	class RequestTaskObject {
		public Map<String, Grid> local_map = new HashMap<String, Grid>();
		public String Task = new String();

	}

	public RequestTaskObject deseriliaze_RequestTask(String json) {
		RequestTaskObject rto = new RequestTaskObject();
		if (!(json.contains(("'" + "Local Map request task:" + "'")) && json.contains(("'" + "{TASK} :" + "'")))) {
			return null;
		}

		int size_LocalMap = json.indexOf(("'" + "Local Map request task:" + "'"))
				+ ("'" + "Local Map request task:" + "'").length();
		int size_Task = json.indexOf(("'" + "{TASK} :" + "'")) + ("'" + "{TASK} :" + "'").length();
		int index_Task = json.indexOf(("'" + "{TASK} :" + "'"));
		String localmap = json.substring(size_LocalMap, index_Task);
		Map<String, Grid> map = deseriliaze_local_map(localmap);
		String Task = json.substring(size_Task);
		rto.local_map = map;
		rto.Task = Task;
		return rto;

	}

	class ListAllAgentTask {
		public Location location;
		public AgentID agentid;
		public String RandomExploration = "Go Savage";

		ListAllAgentTask() {

		}

		ListAllAgentTask(Location locations, AgentID agentids) {
			location = locations;
			agentid = agentids;
		}
	}

	public String seriliaze_TaskAssignment_ALL(Map<String, Grid> local_map, List<ListAllAgentTask> tasks) {
		RuntimeTypeAdapterFactory<WorldObject> adapter = RuntimeTypeAdapterFactory.of(WorldObject.class)
				.registerSubtype(WorldObject.class).registerSubtype(Rubble.class).registerSubtype(Survivor.class)
				.registerSubtype(SurvivorGroup.class).registerSubtype(BottomLayer.class);
		Gson gson2 = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

		String json2 = gson2.toJson(local_map);
		json2 = ("'" + "Local Map TaskAssignment:" + "'").concat(json2);

		json2 = ("'" + "Task Assignment :" + "'").concat(json2);
		for (ListAllAgentTask task : tasks) {
			String agenttask;
			if (task.location != null)
				agenttask = ("'" + "Agent ID:" + task.agentid.getID() + "'").concat((task.location.procString()));
			else// if null
				agenttask = ("'" + "Agent ID:" + task.agentid.getID() + "'").concat("(-1,-1)");

			json2 = json2.concat(agenttask);
		}

		return json2;

	}

	class deserilizedAsignmentTaskObject {
		public Map<String, Grid> local_map;
		public Location Task;
		public boolean gosavage = false;
		public String RandomExploration;

	}

	public deserilizedAsignmentTaskObject deseriliaze_TaskAssignment_ALL(String json) {

		String AgentId = "'" + "Agent ID:" + agent.getAgentID().getID() + "'";
		if (!(json.contains(("'" + "Local Map TaskAssignment:" + "'")) && json.contains((AgentId)))) {
			return null;
		}
		deserilizedAsignmentTaskObject dato = new deserilizedAsignmentTaskObject();
		int size_LocalMap = json.indexOf(("'" + "Local Map TaskAssignment:" + "'"))
				+ ("'" + "Local Map TaskAssignment:" + "'").length();
		int size_Task = json.indexOf(("'" + "Agent ID:")) + ("'" + "Agent ID:").length();
		// TODO: Why is this not used?
		int index_Task = json.indexOf(("'" + "Agent ID:"));
		String localmap = json.substring(size_LocalMap, index_Task);
		Map<String, Grid> map = deseriliaze_local_map(localmap);
		int size = json.indexOf(AgentId);
		int sizelength = AgentId.length();
		int index = size + sizelength;
		int loc = json.indexOf(")", index);
		// Detect wether location or savage here change savage variable here
		// aswell
		String location = json.substring(index, loc + 1);
		String row = location.substring(1, location.indexOf(','));
		String col = location.substring(location.indexOf(',') + 1, location.indexOf(')'));
		if (!row.equals("-1")) {
			dato.local_map = map;
			dato.Task = new Location(Integer.parseInt(row), Integer.parseInt(col));
		} else {
			dato.local_map = map;
			dato.Task = null;
			CurrentState = state.Savage;// change to savage
		}

		return dato;
	}

	void print_map(Map<String, Grid> local_map) {
		for (Map.Entry<String, Grid> entry : local_map.entrySet()) {
			String key = entry.getKey();
			Grid value = entry.getValue();
			System.out.print("Location:" + key);
			System.out.println(" Grid info " + value.getGridInfo() + " " + value.round + "----"
					+ value.getStoredLifeSignals() + "------" + value.getStoredLifeSignals().round);
		}

	}

	public Map<String, Grid> CopyMap(Map<String, Grid> existingMap, Map<String, Grid> NewMap) {// Error
																								// in
																								// some
																								// casses
		// --object reference of grid
		try {
			Map<String, Grid> combinedmap = new HashMap<String, Grid>();
			combinedmap = existingMap;
			for (Map.Entry<String, Grid> entry : NewMap.entrySet()) {
				String key = entry.getKey();
				Grid value = entry.getValue();
				if (existingMap.containsKey(key)) {
					Grid exisitingvalue = existingMap.get(key);
					if (exisitingvalue.round <= value.round) {
						Grid onegrid = existingMap.get(key);
						onegrid.setAgentList(value.getAgentList());
						onegrid.setMoveCost(value.getMoveCost());
						onegrid.setTopLayer(value.getTopLayer());
						if (value.getStoredLifeSignals().round >= exisitingvalue.getStoredLifeSignals().round)// value.getStoredLifeSignals().getSignals().length
																												// !=
																												// 0&&//
																												// problem
							onegrid.setStoredLifeSignals(value.getStoredLifeSignals());

						combinedmap.put(key, onegrid);
					} else if (exisitingvalue.round > value.round) {
						Grid onegrid = existingMap.get(key);
						if (value.getStoredLifeSignals().round >= exisitingvalue.getStoredLifeSignals().round)// value.getStoredLifeSignals().getSignals().length
																												// !=
																												// 0&&//
																												// problem
							onegrid.setStoredLifeSignals(value.getStoredLifeSignals());

						combinedmap.put(key, onegrid);
					}

				} else {
					String location = key;
					String row = location.substring(1, location.indexOf(','));
					String col = location.substring(location.indexOf(',') + 1, location.indexOf(')'));
					Grid onegrid = getWorld().getGrid()[Integer.parseInt(row)][Integer.parseInt(col)];

					onegrid.setAgentList(value.getAgentList());
					onegrid.setMoveCost(value.getMoveCost());
					onegrid.setTopLayer(value.getTopLayer());

					onegrid.setStoredLifeSignals(value.getStoredLifeSignals());
					combinedmap.put(key, onegrid);

				}

			}
			return combinedmap;
		} catch (Exception e) {
			e.printStackTrace();
			return existingMap;
		}
	}

	public ExampleAgent() {
		list.add(new SLEEP());
		list.add(new OBSERVE(new Location(2, 2)));
		list.add(new SAVE_SURV());
		list.add(new TEAM_DIG());
		list.add(new MOVE(Direction.SOUTH_WEST));
		AgentIDList agent_id_list = new AgentIDList();
		list.add(new SEND_MESSAGE(agent_id_list, "This is a test"));
	}

	@Override
	public void handleDisconnect() {
		BaseAgent.log(LogLevels.Always, "DISCONNECT");
	}

	@Override
	public void handleDead() {
		BaseAgent.log(LogLevels.Always, "DEAD");
	}

	@Override
	public void handleFwdMessage(FWD_MESSAGE fwd_message) {
		// TODO: Add a round check and drop message if bigger/smaller then a
		// difference of 2-3
		try {
			fwd_message = Cipher.decrypt(fwd_message);
			fwd_message = Compression.uncompressString(fwd_message);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Error:--------\n" + fwd_message.getMessage());
		}

		// Skip non-ours GID
		try {
			if (fwd_message.getFromAgentID().getGID() != agent.getAgentID().getGID()) {
				return;
			}
		} catch (Exception e) {
			System.out.println("Example Agent Exception: " + e.getMessage());
			e.printStackTrace();
		}

		// use this map some_how
		try {
			if (fwd_message.getMessage().contains(("'" + "Local Map :" + "'"))) {// LOCALMAP
				int size = fwd_message.getMessage().indexOf(("'" + "Local Map :" + "'"))
						+ ("'" + "Local Map :" + "'").length();
				String localmap = fwd_message.getMessage().substring(size);
				Map<String, Grid> map = deseriliaze_local_map(localmap);
				local_map_string = CopyMap(local_map_string, map);
				print_map(local_map_string);

				// Do soemthing with the local map
			} else if (fwd_message.getMessage().contains(("'" + "Location :" + "'"))) {// LOCATION

				int size = fwd_message.getMessage().indexOf(("'" + "Location :" + "'"))
						+ ("'" + "Location :" + "'").length();
				String location = fwd_message.getMessage().substring(size);
				Location loc = deseriliaze_Location(location);
				if (fwd_message.getFromAgentID().getID() != agent.getAgentID().getID() && round == 2) {// First
																										// Broadcast
					IntitialAgentsLocation.put(fwd_message.getFromAgentID(), loc);
				}
				// Use Location Somehow

			} else if (fwd_message.getMessage().contains(("'" + "REQUEST TASK :" + "'"))) {// Request
				// Task
				int size = fwd_message.getMessage().indexOf(("'" + "REQUEST TASK :" + "'"))
						+ ("'" + "REQUEST TASK :" + "'").length();
				String requesttask = fwd_message.getMessage().substring(size);

				RequestTaskObject rto = deseriliaze_RequestTask(requesttask);

				if (rto != null) {
					if (rto.local_map != null)
						local_map_string = CopyMap(local_map_string, rto.local_map);
					else {
						System.out.println("Null");
					}
				}
				if (AgentsTask == null)
					AgentsTask = new HashMap<AgentID, Location>();
				AgentsTask.put(fwd_message.getFromAgentID(), null);

			} else if (fwd_message.getMessage().contains(("'" + "Task Assignment :" + "'"))) {// Task
				// Assignement

				int size = fwd_message.getMessage().indexOf(("'" + "Task Assignment :" + "'"))
						+ ("'" + "Task Assignment :" + "'").length();
				String requesttask = fwd_message.getMessage().substring(size);
				deserilizedAsignmentTaskObject dato = deseriliaze_TaskAssignment_ALL(requesttask);
				if (dato != null) {

					if (dato.local_map != null)
						local_map_string = CopyMap(local_map_string, dato.local_map);
					// Master has a new Request
					if (CurrentState == state.sleep) {// error
						if (dato.Task != null) {
							destination = dato.Task;
							CurrentState = state.MoveToDestination;
						}
					}
				}

				else {
					System.out.println("No message for me");
				}

			} else if (fwd_message.getMessage().contains(("'" + "RandomExploration:" + "'"))) {// Random
																								// Exploration
																								// here

				int size = fwd_message.getMessage().indexOf(("'" + "RandomExploration:" + "'"))
						+ ("'" + "RandomExploration:" + "'").length();
				String masterid = fwd_message.getMessage().substring(size);
				deserilizedAsignmentTaskObject dato = deseriliaze_savage(masterid);
				local_map_string = CopyMap(local_map_string, dato.local_map);

				// TODO: Change to savage mode
				CurrentState = state.Savage;
				// Change to Random exploration state here
			} else {
				System.out.println("Unknown Command take: " + fwd_message.getMessage());
			}
		} catch (Exception e) {
			System.out.println("Unknown Command exception: " + fwd_message.getMessage());

		}
	}

	public boolean hasmyagent(AgentIDList agentlists) {
		for (AgentID aid : agentlists) {
			if (aid.getGID() == agent.getAgentID().getGID() && aid.getID() != agent.getAgentID().getID()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleMoveResult(MOVE_RESULT move_result) {
		GridInfo curr = move_result.getSurrroundInfo().getCurrentInfo();
		agent.setEnergyLevel(move_result.getEnergyLevel());
		agent.setLocation(curr.getLocation());
		Grid[][] world_grid = getWorld().getGrid();
		SurroundInfo surr_info = move_result.getSurrroundInfo();

		GridInfo dir = null;
		for (int i = 0; i < 9; i++) {
			switch (i) {
			case 0:
				dir = surr_info.getSurroundInfo(Direction.STAY_PUT);
				break;
			case 1:
				dir = surr_info.getSurroundInfo(Direction.NORTH_WEST);
				break;
			case 2:
				dir = surr_info.getSurroundInfo(Direction.EAST);
				break;
			case 3:
				dir = surr_info.getSurroundInfo(Direction.NORTH);
				break;
			case 4:
				dir = surr_info.getSurroundInfo(Direction.NORTH_EAST);
				break;
			case 5:
				dir = surr_info.getSurroundInfo(Direction.SOUTH);
				break;
			case 6:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_EAST);
				break;
			case 7:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_WEST);
				break;
			case 8:
				dir = surr_info.getSurroundInfo(Direction.WEST);
				break;

			}
			if (dir != null && !dir.isNoGrid()) {// Updating other move info
				// directly
				Grid onegrid = world_grid[dir.getLocation().getRow()][dir.getLocation().getCol()];
				onegrid.round = round;
				onegrid.setAgentList(dir.getAgentIDList());

				// for
				// Stayput

				onegrid.setAgentList(dir.getAgentIDList());
				onegrid.setMoveCost(dir.getMoveCost());
				if (dir.getTopLayerInfo() instanceof BottomLayerInfo) {
					// BottomLayerInfo LayerInfo = (BottomLayerInfo)
					// dir.getTopLayerInfo();
					BottomLayer LayerObject = new BottomLayer();
					onegrid.setTopLayer(LayerObject);
				} else if (dir.getTopLayerInfo() instanceof RubbleInfo) {
					RubbleInfo LayerInfo = (RubbleInfo) dir.getTopLayerInfo();
					Rubble LayerObject = new Rubble(LayerInfo.getRemoveEnergy(), LayerInfo.getRemoveAgents());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorInfo) {
					SurvivorInfo LayerInfo = (SurvivorInfo) dir.getTopLayerInfo();
					Survivor LayerObject = new Survivor(LayerInfo.getEnergyLevel(), LayerInfo.getDamageFactor(),
							LayerInfo.getBodyMass(), LayerInfo.getMentalState());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorGroupInfo) {
					SurvivorGroupInfo LayerInfo = (SurvivorGroupInfo) dir.getTopLayerInfo();
					SurvivorGroup LayerObject = new SurvivorGroup(LayerInfo.getEnergyLevel(),
							LayerInfo.getNumberOfSurvivors());
					onegrid.setTopLayer(LayerObject);

				}
				if (i == 0) {
					LifeSignals ls = surr_info.getLifeSignals();
					ls.round = round;
					onegrid.setStoredLifeSignals(ls);// only
					// nolifesignal
					// agent.getLocation()!=destination&&(onegrid.getTopLayer()
					// instanceof Survivor ||onegrid.getTopLayer() instanceof
					// SurvivorGroup)
					if (CurrentState != state.Savage) {
						if (onegrid.getTopLayer() instanceof Survivor
								|| onegrid.getTopLayer() instanceof SurvivorGroup) {
							CurrentState = state.SaveSurviviors;
						} else if (agent.getLocation().equals(destination) && hasmyagent(onegrid.getAgentList())) {
							CurrentState = state.digstate;// here
						} else if (agent.getLocation().equals(destination) && !hasmyagent(onegrid.getAgentList())) {
							CurrentState = state.Wait;// here
						}
					}
				}
				if (local_map_string.containsKey(onegrid.getLocation().procString())) {
					// If the key Already exist.
					local_map_string.put(onegrid.getLocation().procString(), onegrid);
					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				} else {
					local_map_string.put(onegrid.getLocation().procString(), onegrid);
					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				}
			}
		}

	}

	@Override
	public void handleObserveResult(OBSERVE_RESULT observe_result) {
		agent.setEnergyLevel(observe_result.getEnergyLevel());
		Grid[][] world_grid = getWorld().getGrid();
		GridInfo dir = observe_result.getTopLayerInfo();
		Grid onegrid = world_grid[dir.getLocation().getRow()][dir.getLocation().getCol()];
		onegrid.round = round;
		LifeSignals ls = observe_result.getLifeSignals();
		ls.round = round;
		onegrid.setStoredLifeSignals(ls);// only

		onegrid.setAgentList(dir.getAgentIDList());
		onegrid.setMoveCost(dir.getMoveCost());
		if (dir.getTopLayerInfo() instanceof BottomLayerInfo) {
			BottomLayer LayerObject = new BottomLayer();
			onegrid.setTopLayer(LayerObject);
		} else if (dir.getTopLayerInfo() instanceof RubbleInfo) {
			RubbleInfo LayerInfo = (RubbleInfo) dir.getTopLayerInfo();
			Rubble LayerObject = new Rubble(LayerInfo.getRemoveEnergy(), LayerInfo.getRemoveAgents());
			onegrid.setTopLayer(LayerObject);

		} else if (dir.getTopLayerInfo() instanceof SurvivorInfo) {
			SurvivorInfo LayerInfo = (SurvivorInfo) dir.getTopLayerInfo();
			Survivor LayerObject = new Survivor(LayerInfo.getEnergyLevel(), LayerInfo.getDamageFactor(),
					LayerInfo.getBodyMass(), LayerInfo.getMentalState());
			onegrid.setTopLayer(LayerObject);

		} else if (dir.getTopLayerInfo() instanceof SurvivorGroupInfo) {
			SurvivorGroupInfo LayerInfo = (SurvivorGroupInfo) dir.getTopLayerInfo();
			SurvivorGroup LayerObject = new SurvivorGroup(LayerInfo.getEnergyLevel(), LayerInfo.getNumberOfSurvivors());
			onegrid.setTopLayer(LayerObject);

		}

		if (local_map_string.containsKey(onegrid.getLocation().procString())) {
			// If the key already exist
			local_map_string.put(onegrid.getLocation().procString(), onegrid);

			local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
		} else {
			local_map_string.put(onegrid.getLocation().procString(), onegrid);

			local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
		}
	}

	@Override
	public void handleSaveSurvResult(SAVE_SURV_RESULT save_surv_result) {
		// GridInfo curr = save_surv_result.getSurrroundInfo().getCurrentInfo();
		agent.setEnergyLevel(save_surv_result.getEnergyLevel());
		Grid[][] world_grid = getWorld().getGrid();
		SurroundInfo surr_info = save_surv_result.getSurrroundInfo();

		GridInfo dir = null;
		for (int i = 0; i < 9; i++) {
			switch (i) {
			case 0:
				dir = surr_info.getSurroundInfo(Direction.STAY_PUT);
				break;
			case 1:
				dir = surr_info.getSurroundInfo(Direction.NORTH_WEST);
				break;
			case 2:
				dir = surr_info.getSurroundInfo(Direction.EAST);
				break;
			case 3:
				dir = surr_info.getSurroundInfo(Direction.NORTH);
				break;
			case 4:
				dir = surr_info.getSurroundInfo(Direction.NORTH_EAST);
				break;
			case 5:
				dir = surr_info.getSurroundInfo(Direction.SOUTH);
				break;
			case 6:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_EAST);
				break;
			case 7:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_WEST);
				break;
			case 8:
				dir = surr_info.getSurroundInfo(Direction.WEST);
				break;

			}
			if (dir != null && !dir.isNoGrid()) {// Updating other move info
				// directly
				Grid onegrid = world_grid[dir.getLocation().getRow()][dir.getLocation().getCol()];
				onegrid.round = round;
				onegrid.setAgentList(dir.getAgentIDList());

				// for
				// Stayput

				onegrid.setAgentList(dir.getAgentIDList());
				onegrid.setMoveCost(dir.getMoveCost());
				if (dir.getTopLayerInfo() instanceof BottomLayerInfo) {
					// BottomLayerInfo LayerInfo = (BottomLayerInfo)
					// dir.getTopLayerInfo();
					BottomLayer LayerObject = new BottomLayer();
					onegrid.setTopLayer(LayerObject);
				} else if (dir.getTopLayerInfo() instanceof RubbleInfo) {
					RubbleInfo LayerInfo = (RubbleInfo) dir.getTopLayerInfo();
					Rubble LayerObject = new Rubble(LayerInfo.getRemoveEnergy(), LayerInfo.getRemoveAgents());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorInfo) {
					SurvivorInfo LayerInfo = (SurvivorInfo) dir.getTopLayerInfo();
					Survivor LayerObject = new Survivor(LayerInfo.getEnergyLevel(), LayerInfo.getDamageFactor(),
							LayerInfo.getBodyMass(), LayerInfo.getMentalState());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorGroupInfo) {
					SurvivorGroupInfo LayerInfo = (SurvivorGroupInfo) dir.getTopLayerInfo();
					SurvivorGroup LayerObject = new SurvivorGroup(LayerInfo.getEnergyLevel(),
							LayerInfo.getNumberOfSurvivors());
					onegrid.setTopLayer(LayerObject);

				}
				if (i == 0) {
					WorldObject wo = (getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()])
							.getTopLayer();
					Grid grid = getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()];
					LifeSignals ls = surr_info.getLifeSignals();
					ls.round = round;
					// Setting state according to info
					onegrid.setStoredLifeSignals(ls);// only
					if (CurrentState != state.Savage) {
						if (onegrid.getTopLayer() instanceof Survivor
								|| onegrid.getTopLayer() instanceof SurvivorGroup) {
							CurrentState = state.SaveSurviviors;
						} else if (agent.getLocation().equals(destination)
								&& nolifesignal(grid.getStoredLifeSignals())) {// for
																				// lifesignals
																				// zero
							// System.out.println(grid.getStoredLifeSignals());
							CurrentState = state.RequestTask;// here
						} else if (agent.getLocation().equals(destination) && wo instanceof BottomLayer) {
							CurrentState = state.RequestTask;// here
						}
						else if (agent.getLocation().equals(destination)
								&& nolifesignal(grid.getStoredLifeSignals())) {// for
																				// lifesignals
																				// zero
																				// but
																				// are
																				// we
																				// sure
																				// about
																				// this?

							CurrentState = state.RequestTask;// here
						}
						else if (agent.getLocation().equals(destination) && hasmyagent(onegrid.getAgentList())) {
							CurrentState = state.digstate;// here
						} else if (agent.getLocation().equals(destination) && !hasmyagent(onegrid.getAgentList())) {
							CurrentState = state.Wait;// here
							// System.out.println("here waiting");
						}
						else if(!agent.getLocation().equals(destination)){
							CurrentState = state.MoveToDestination;
						}
					}
				}
				if (local_map_string.containsKey(onegrid.getLocation().procString())) {
					// If the key Already exist.
					local_map_string.put(onegrid.getLocation().procString(), onegrid);

					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				} else {
					local_map_string.put(onegrid.getLocation().procString(), onegrid);

					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				}
			}
		}

	}

	@Override
	public void handleTeamDigResult(TEAM_DIG_RESULT team_dig_result) {
		// same as move
		// GridInfo curr = team_dig_result.getSurrroundInfo().getCurrentInfo();
		agent.setEnergyLevel(team_dig_result.getEnergyLevel());
		Grid[][] world_grid = getWorld().getGrid();
		SurroundInfo surr_info = team_dig_result.getSurrroundInfo();

		GridInfo dir = null;
		for (int i = 0; i < 9; i++) {
			switch (i) {
			case 0:
				dir = surr_info.getSurroundInfo(Direction.STAY_PUT);
				break;
			case 1:
				dir = surr_info.getSurroundInfo(Direction.NORTH_WEST);
				break;
			case 2:
				dir = surr_info.getSurroundInfo(Direction.EAST);
				break;
			case 3:
				dir = surr_info.getSurroundInfo(Direction.NORTH);
				break;
			case 4:
				dir = surr_info.getSurroundInfo(Direction.NORTH_EAST);
				break;
			case 5:
				dir = surr_info.getSurroundInfo(Direction.SOUTH);
				break;
			case 6:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_EAST);
				break;
			case 7:
				dir = surr_info.getSurroundInfo(Direction.SOUTH_WEST);
				break;
			case 8:
				dir = surr_info.getSurroundInfo(Direction.WEST);
				break;

			}
			if (dir != null && !dir.isNoGrid()) {// Updating other move info
				// directly
				Grid onegrid = world_grid[dir.getLocation().getRow()][dir.getLocation().getCol()];
				onegrid.round = round;
				onegrid.setAgentList(dir.getAgentIDList());

				onegrid.setAgentList(dir.getAgentIDList());
				onegrid.setMoveCost(dir.getMoveCost());
				if (dir.getTopLayerInfo() instanceof BottomLayerInfo) {
					// BottomLayerInfo LayerInfo = (BottomLayerInfo)
					// dir.getTopLayerInfo();
					BottomLayer LayerObject = new BottomLayer();
					onegrid.setTopLayer(LayerObject);
				} else if (dir.getTopLayerInfo() instanceof RubbleInfo) {
					RubbleInfo LayerInfo = (RubbleInfo) dir.getTopLayerInfo();
					Rubble LayerObject = new Rubble(LayerInfo.getRemoveEnergy(), LayerInfo.getRemoveAgents());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorInfo) {
					SurvivorInfo LayerInfo = (SurvivorInfo) dir.getTopLayerInfo();
					Survivor LayerObject = new Survivor(LayerInfo.getEnergyLevel(), LayerInfo.getDamageFactor(),
							LayerInfo.getBodyMass(), LayerInfo.getMentalState());
					onegrid.setTopLayer(LayerObject);

				} else if (dir.getTopLayerInfo() instanceof SurvivorGroupInfo) {
					SurvivorGroupInfo LayerInfo = (SurvivorGroupInfo) dir.getTopLayerInfo();
					SurvivorGroup LayerObject = new SurvivorGroup(LayerInfo.getEnergyLevel(),
							LayerInfo.getNumberOfSurvivors());
					onegrid.setTopLayer(LayerObject);

				}
				if (i == 0) {
					WorldObject wo = (getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()])
							.getTopLayer();
					Grid grid = getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()];
					// Setting state according to info
					LifeSignals ls = surr_info.getLifeSignals();
					ls.round = round;
					onegrid.setStoredLifeSignals(ls);// only
					if (CurrentState != state.Savage) {
						if (onegrid.getTopLayer() instanceof Survivor
								|| onegrid.getTopLayer() instanceof SurvivorGroup) {
							CurrentState = state.SaveSurviviors;
						} else if (agent.getLocation().equals(destination) && wo instanceof BottomLayer) {
							CurrentState = state.RequestTask;// here
						} else if (agent.getLocation().equals(destination)
								&& nolifesignal(grid.getStoredLifeSignals())) {// for
																				// lifesignals
																				// zero
																				// but
																				// are
																				// we
																				// sure
																				// about
																				// this?

							CurrentState = state.RequestTask;// here
						} else if (agent.getLocation().equals(destination) && hasmyagent(onegrid.getAgentList())) {

							CurrentState = state.digstate;// here
						} else if (agent.getLocation().equals(destination) && !hasmyagent(onegrid.getAgentList())) {
							CurrentState = state.Wait;// here
						}
					}

				} // for
				// Stayput

				if (local_map_string.containsKey(onegrid.getLocation().procString())) {
					// If the key Already exist.
					local_map_string.put(onegrid.getLocation().procString(), onegrid);

					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				} else {
					local_map_string.put(onegrid.getLocation().procString(), onegrid);

					local_map_new_stuff.put(onegrid.getLocation().procString(), onegrid);
				}
			}
		}

	}

	public boolean toprubbleisremovable(WorldObject rubble) {
		if (rubble instanceof Rubble) {
			Rubble rub = (Rubble) rubble;
			if (rub.getRemoveAgents() < 2) {
				return true;
			}
		}
		return false;
	}

	public boolean nolifesignal(LifeSignals ls) {
		// System.out.println("LifeSignals"+ls.getSignals().length);
		if (ls.getSignals().length == 0)
			return true;

		// System.out.println("LifeSignals"+ls.getSignals());
		for (int i = 0; i < ls.getSignals().length; i++) {
			if (ls.get(i) > 0)
				return false;
		}
		return true;
	}

	boolean null_list(Map<AgentID, Location> Tasks) {
		if (Tasks != null && !Tasks.isEmpty()) {
			for (Entry<AgentID, Location> ag : Tasks.entrySet()) {
				if (ag.getValue() != null)
					return false;
			}
		}
		return true;
	}

	@Override
	public void think() {
		BaseAgent.log(LogLevels.Always, "Thinking");
		try {
			// Needed for savage mode - track positions you visited and add
			// "neigbours" to the "frontier"
			do_work_for_savage_mode();

			// This is broadcast location and such
			AgentCommand command = null;
			if (round == 1) {
				String json2 = get_serialzed_message(2, null, agent.getLocation(), null, null, null);
				command = list.get(5);
				SEND_MESSAGE send_message = (SEND_MESSAGE) command;
				send_message.getAgentIDList().add(new AgentID(0, agent.getAgentID().getGID()));
				send_message = new SEND_MESSAGE(send_message.getAgentIDList(), json2);
				command = send_message;
				agent.send(command);
			}

			else if (round == 2) {
				IntitialAgentsLocation.put(agent.getAgentID(), agent.getLocation());
				command = new MOVE(getDirection(0));
				// We have Location of ever agent do something here
				AgentsLocation = AgentGridAssignment.run(IntitialAgentsLocation, getWorld());

				if (null_list(AgentsLocation)) {
					CurrentState = state.Savage;
				} else if (AgentsLocation.containsKey(agent.getAgentID())) {
					destination = AgentsLocation.get(agent.getAgentID());

					for (int i = 1; i < 8; i++) {
						if (!AgentsLocation.containsKey(new AgentID(i, agent.getAgentID().getGID()))) {
							masterid = new AgentID(i, agent.getAgentID().getGID());
							break;
						}
					}

					if (destination == null) {
						// TODO: Go to savage mode
						CurrentState = state.Savage;
					}

				} else {

					IAmMaster = true;
					CurrentState = state.Master;
				}
				agent.send(command);
			} else {// After round 2
				if (!IAmMaster) {
				//	System.out.println(agent.getLocation()+"---State"+CurrentState);																					// assign
					// grid

					if (!(agent.getLocation().equals(destination)) && CurrentState == state.MoveToDestination) {// when
																												// moving
																												// to
		
						Location loc = AStar.test(this.getWorld(), agent.getLocation().getRow(),
								agent.getLocation().getCol(), destination.getRow(), destination.getCol());
						// No Possile path
//System.out.println("Location to move:"+loc+"Destination:"+destination);
						if (loc != null) {
							Grid wo = (getWorld().getGrid()[loc.getRow()][loc.getCol()]);
							if (agent.getEnergyLevel() <= wo.getMoveCost() + 1)
								command = new SLEEP();
							else {
								Direction tomove = getDirection(agent.getLocation().getRow(),
										agent.getLocation().getCol(), loc.getRow(), loc.getCol());
								command = new MOVE(tomove);
							}
						} // Check energy and cost here .
					} // agent.getLocation().equals(destination)&&
					else if (CurrentState == state.SaveSurviviors) {// Save
																	// surviors
																	// in
																	// between
																	// or at the
																	// destination
						if (agent.getEnergyLevel() <= 1)// Energy Check
						{
							command = new SLEEP();
						} else {
							command = new SAVE_SURV();
						}
					} else if (agent.getLocation().equals(destination) && CurrentState == state.digstate) {// Team
																											// dig
																											// with
																											// my
																											// buddy
						WorldObject wo = (getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation()
								.getCol()]).getTopLayer();
						if (wo instanceof Rubble) {// If the top is rubble go
							Rubble rub = (Rubble) wo;

							if (agent.getEnergyLevel() <= rub.getRemoveEnergy() + 1)// Energy
																					// Check
								command = new SLEEP();
							else
								command = new TEAM_DIG();
						} else if (wo instanceof Survivor || wo instanceof SurvivorGroup) {// Save
																							// a
																							// person

							command = new SAVE_SURV();
						} else // bottom Layer
						{
							// Ask Master for new Task
							CurrentState = state.RequestTask;
						}
					} else if (agent.getLocation().equals(destination) && CurrentState == state.Wait) {// At
																										// the
																										// spot
																										// but
																										// my
																										// pair
																										// is
																										// not
																										// here

						// Waiting too long for a guy who dies tackle this later
						// on.

						WorldObject wo = (getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation()
								.getCol()]).getTopLayer();
						if (toprubbleisremovable(wo))// Remove rubble if can be
														// done alone
						{
							Rubble rub = (Rubble) wo;

							if (agent.getEnergyLevel() <= rub.getRemoveEnergy() + 1)// Energy
																					// Check
								command = new SLEEP();
							else
								command = new TEAM_DIG(); // dig alone
						} else if (alertrnate) {// Alternate between sleep and
												// team Dig till help arrive
							alertrnate = !alertrnate;
							command = new SLEEP();
						} else {
							alertrnate = !alertrnate;
							if (wo instanceof Rubble) {// This is kind of
														// useless he would be
														// stuck only at teamDig
														// I guess
								Rubble rub = (Rubble) wo;
								if (agent.getEnergyLevel() <= rub.getRemoveEnergy() + 1)// Energy
																						// check
									command = new SLEEP();
								else
									command = new TEAM_DIG();
							} else
								command = new SLEEP();
						}
					} else if (CurrentState == state.sleep) {// Let me Sleep
						command = new SLEEP();
					} else if (CurrentState == state.Savage) {
						// TODO: Include the savage code here. ERIC DOES.
						SavageMode();

					}

					WorldObject wo = (getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()])
							.getTopLayer();

					Grid grid = getWorld().getGrid()[agent.getLocation().getRow()][agent.getLocation().getCol()];
					if (agent.getLocation().equals(destination)
							&& (wo instanceof BottomLayer || nolifesignal(grid.getStoredLifeSignals()))
							&& CurrentState == state.RequestTask) {// No
																	// Lifesignal
						// System.out.println("----REquest Task----");

						// All map
						// String json2 =
						// get_serialzed_message(3,local_map_string,null,"Wahts
						// up i am done",null,null);

						// only new map
						String json2 = get_serialzed_message(3, local_map_new_stuff, null, "Wahts up i am done", null,
								null);
						local_map_new_stuff = new HashMap<String, Grid>();

						// Local Map sent here
						AgentIDList agent_id_list = new AgentIDList();
						SEND_MESSAGE send_message = null;
						agent_id_list.add(new AgentID(masterid.getID(), agent.getAgentID().getGID()));
						// Length exception some time
						send_message = new SEND_MESSAGE(agent_id_list, json2);
						command = send_message;
						CurrentState = state.sleep;
					}

					if (command == null && CurrentState != state.Savage) {// In
																			// none
																			// of
																			// the
																			// above
																			// state
																			// worked
																			// form
																			// master
						// System.out.println("----REquest Task----");
						String json2 = get_serialzed_message(3, local_map_string, null,
								"Wahts up i dont hava a command", null, null);

						// Local Map sent here
						AgentIDList agent_id_list = new AgentIDList();
						SEND_MESSAGE send_message = null;
						agent_id_list.add(new AgentID(masterid.getID(), agent.getAgentID().getGID()));
						// Length exception some time
						send_message = new SEND_MESSAGE(agent_id_list, json2);
						command = send_message;
						CurrentState = state.sleep;
					}
					if (command != null)
						agent.send(command);
				} else {
					// I am the Master

					// Make master do some shit.
					if (AgentsTask != null) {
						System.out.println("Assignment----Master");
						/*for (Entry<AgentID, Location> ag : AgentsLocation.entrySet()) {
							// Error
							if (ag.getValue() != null)
								getWorld().getGrid()[ag.getValue().getRow()][ag.getValue().getCol()]
										.setPercentChance(0);
						}*/
						Map<AgentID, Location> TaskAssignment = null;
						Map<AgentID, Location> TaskRelatedAgentLocation = new HashMap<AgentID, Location>();
						for (Entry<AgentID, Location> ag : AgentsTask.entrySet()) {
							TaskRelatedAgentLocation.put(ag.getKey(), AgentsLocation.get(ag.getKey()));
							//System.out.println(ag.getKey() + "---" + ag.getValue());
						}
						TaskAssignment = AgentGridAssignment.run(TaskRelatedAgentLocation, getWorld());
						// If agentsLocation is Null GoSavage Send Agent savage
						// mode message.
						// If assignment for only two agents is null send them
						// the savage method.
						//System.out.println("Tasks");
						//for (Entry<AgentID, Location> ag : TaskAssignment.entrySet())
						
						if (null_list(TaskAssignment)) {// TaskAssignment.isEmpty()){
							System.out.println("----Empty---");
							// TODO: Random exploration
							// No new Task found for agents Random Exploration
							List<ListAllAgentTask> tasks = new ArrayList<ListAllAgentTask>();

							AgentIDList agent_id_list = new AgentIDList();

							for (Entry<AgentID, Location> aga : AgentsTask.entrySet()) {
								tasks.add(new ListAllAgentTask(null, aga.getKey()));
								agent_id_list.add(new AgentID(aga.getKey().getID(), agent.getAgentID().getGID()));
							}
							String json = get_serialzed_message(5, local_map_string, null, null, tasks, null);
							// command = new SEND_MESSAGE(,"");
							SEND_MESSAGE send_message;// = (SEND_MESSAGE)
														// command;

							send_message = new SEND_MESSAGE(agent_id_list, json);
							command = send_message;
							// CurrentState=state.Savage;//No more assignements
							// available
							// Terminate here or change the code the rest of the
							// code should not be run.
							// No Task for any one after this
						} else {
							//System.out.println("-----Task Assignment------");

							for (Entry<AgentID, Location> ag : TaskAssignment.entrySet()) {
								// System.out.println(ag.getValue().getRow()+"
								// "+ag.getValue().getCol());

								AgentsLocation.put(ag.getKey(), ag.getValue());

							}
						}
						if (!null_list(TaskAssignment)) {// TaskAssignment.isEmpty()){
							List<ListAllAgentTask> tasks = new ArrayList<ListAllAgentTask>();
							for (Entry<AgentID, Location> ag : AgentsTask.entrySet()) {// Get
																						// task
																						// agents
																						// and
																						// assing
																						// them
																						// their
																						// shit
								// System.out.println(ag.getKey()+"
								// "+ag.getValue());
								if (TaskAssignment.containsKey(ag.getKey())) {
									AgentsTask.put(ag.getKey(), AgentsLocation.get(ag.getKey()));
									if (AgentsLocation.get(ag.getKey()) != null)// No
																				// assignment
																				// for
																				// LION
																				// due
																				// to
																				// zero
																				// percentage
										tasks.add(new ListAllAgentTask(AgentsLocation.get(ag.getKey()), ag.getKey()));
									else {// If task assignement was null Go to
											// random Exploration mode.
										System.out.println("I dont have an assignment for You I am null");
										tasks.add(new ListAllAgentTask(null, ag.getKey()));

									}
								} else {
									System.out.println("I dont have an assignment for You");
									tasks.add(new ListAllAgentTask(null, ag.getKey()));

									// Look into this
									// Go Savage TODO: s
									// Send savage message
									// --No new task was found for one of the
									// two pairs.

								}
							}
							// Local Map sent here
							String json2 = null;

							// TODO: Only do this shit if not in savage mode
							json2 = get_serialzed_message(4, local_map_string, null, null, tasks, null);
							AgentIDList agent_id_list = new AgentIDList();
							SEND_MESSAGE send_message = null;

							for (Entry<AgentID, Location> ag : AgentsTask.entrySet()) {
								agent_id_list.add(new AgentID(ag.getKey().getID(), agent.getAgentID().getGID()));
							}
							// Instead of Location send Random Exploration
							// message change in message
							send_message = new SEND_MESSAGE(agent_id_list, json2);
							command = send_message;
						}
						agent.send(command);
						AgentsTask = null;
					} else {
						// Master does his shit.
						if (CurrentState == state.Savage) {
							// TODO: Include the savage code here. ERIC DOES.
							SavageMode();

						}
					}
				}

			}
			round++;
			BaseAgent.getBaseAgent().send(new END_TURN());
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
	}

	// This list grows forever for the agent, as it discovers more and more
	// locations to which it COULD travel but didn't.
	List<Grid> frontier = new ArrayList<Grid>();

	private void do_work_for_savage_mode() {
		Grid[][] grid = getWorld().getGrid();
		Location loc = agent.getLocation();

		grid[loc.getRow()][loc.getCol()].was_visited = true;

		for (int row = -1; row <= 1; row++) {
			for (int col = -1; col <= 1; col++) {
				int cur_row = loc.getRow() + row;
				int cur_col = loc.getCol() + col;

				// Skip out of bounds indecies
				if (cur_row < 0 || cur_row >= grid.length) {
					continue;
				}
				if (cur_col < 0 || cur_col >= grid[0].length) {
					continue;
				}

				// Add neighbour to frontier if possible to move to it
				Grid neighbour = grid[cur_row][cur_col];

				if (!neighbour.isKiller())
					frontier.add(neighbour);
			}
		}
	}

	boolean traveling_to_target = false;
	Grid target;

	public void SavageMode() {
		System.out.println("I am in savage mode");
		Grid[][] grid = getWorld().getGrid();
		Location loc = agent.getLocation();
		Grid cur_grid = grid[loc.getRow()][loc.getCol()];
		AgentCommand command = null;

		// Check if we arrived to our target
		if (target != null) {
			if (target.getLocation().getCol() == loc.getCol() && target.getLocation().getRow() == loc.getRow()) {
				// Check grid for life signals && check to see if I can save
				// person
				if (cur_grid.getTopLayer().getObjectInfo() instanceof SurvivorInfo
						|| cur_grid.getTopLayer().getObjectInfo() instanceof SurvivorGroupInfo) {
					// Have a survivor on top

					if (agent.getEnergyLevel() <= 1) {
						// Sleep command
						command = new SLEEP();
					} else {
						// Save_surv command
						command = new SAVE_SURV();
					}
				} else if (!nolifesignal(cur_grid.getStoredLifeSignals())) {
					// Means there's a life signal
					if (cur_grid.getTopLayer().getObjectInfo() instanceof RubbleInfo) {
						// If rubble limit is 1, dig, otherwise continue onwards
						RubbleInfo rubble_info = (RubbleInfo) cur_grid.getTopLayer().getObjectInfo();

						if (rubble_info.getRemoveAgents() == 1) {
							// Check if there's enough energy to remove it or
							// sleep
							if (agent.getEnergyLevel() <= rubble_info.getRemoveEnergy() + 1) {
								// Sleep command
								command = new SLEEP();
							} else {
								// Dig command
								command = new TEAM_DIG();
							}
						} else {
							// Done with this target
							target = null;
						}
					} else {
						// Shouldn't really happen, but if another some kind of
						// top layer, just re-run the target and ignore this
						// location
						target = null;
					}
				}
			}
		}

		if (target == null) {
			// Aquire a new target
			List<Grid> to_remove = new ArrayList<Grid>();
			for (Grid possible_target : frontier) {
				if (possible_target.was_visited)
					to_remove.add(possible_target);
			}

			// Remove all the frontiers that we might've visited already
			frontier.removeAll(to_remove);

			// Find the shortest frontier spot based on distance to travel
			int shortest_dist = Integer.MAX_VALUE;

			if (frontier.isEmpty()) {
				// Sleep - we're done exploring everything!
			} else {
				for (Grid possible_target : frontier) {
					// Test if this possible target is the shortest - will
					// return the future movement location
					// And store the length of the path in the
					// AStar.last_path_length
					Location move_location = AStar.test(getWorld(), loc.getRow(), loc.getCol(),
							possible_target.getLocation().getRow(), possible_target.getLocation().getRow());

					// If move was possible to that possible target
					if (move_location != null) {
						// If the target path length was shortest in terms of
						// cost
						if (AStar.last_path_cost < shortest_dist) {
							target = grid[move_location.getRow()][move_location.getCol()];
						}
					}
				}

				// Send command to travel to target or sleep
				if (agent.getEnergyLevel() <= target.getMoveCost() + 1) {
					target = null; // Will recalculate the same target next turn
									// - lazy hack

					// Send Sleep command
					command = new SLEEP();

				} else {
					// Move to target
					Direction target_dir = getDirection(loc.getRow(), loc.getCol(), target.getLocation().getRow(),
							target.getLocation().getCol());
					command = new MOVE(target_dir);
				}
			}
		}

		if (command != null) {
			agent.send(command);
		}
	}

	public static Direction getDirection(int currentrow, int currentcol, int desrow, int destcol) {
		if (desrow + 1 == currentrow && destcol + 1 == currentcol)// NORTH_WEST(0,
																	// 0, 0, -1,
																	// -1,
																	// "NORTH_WEST",
																	// "North
																	// West"),
			return Direction.NORTH_WEST;
		else if (desrow + 1 == currentrow && destcol == currentcol)
			return Direction.NORTH;
		// NORTH(1, 0, 1, -1, 0, "NORTH", "North"),
		else if (desrow + 1 == currentrow && destcol - 1 == currentcol)
			return Direction.NORTH_EAST;
		// NORTH_EAST(2, 0, 2, -1, 1, "NORTH_EAST", "North East"),
		else if (desrow == currentrow && destcol - 1 == currentcol)
			return Direction.EAST;
		// EAST(3, 1, 2, 0, 1, "EAST", "East"),
		else if (desrow - 1 == currentrow && destcol - 1 == currentcol)
			return Direction.SOUTH_EAST;
		// SOUTH_EAST(4, 2, 2, 1, 1, "SOUTH_EAST", "South East"),
		else if (desrow - 1 == currentrow && destcol == currentcol)
			return Direction.SOUTH;
		// SOUTH(5, 2, 1, 1, 0, "SOUTH", "South"),
		else if (desrow - 1 == currentrow && destcol + 1 == currentcol)
			return Direction.SOUTH_WEST;
		// SOUTH_WEST(6, 2, 0, 1, -1, "SOUTH_WEST", "South West"),
		else if (desrow == currentrow && destcol + 1 == currentcol)
			return Direction.WEST;
		return Direction.UNKNOWN;

	}

	public static Direction getDirection(int index) {
		if (index == 0) {
			return Direction.STAY_PUT;
		} else if (index == 1) {
			return Direction.NORTH;
		} else if (index == 2) {
			return Direction.NORTH_EAST;
		} else if (index == 3) {
			return Direction.EAST;
		} else if (index == 4) {
			return Direction.SOUTH_EAST;
		} else if (index == 5) {
			return Direction.SOUTH;
		} else if (index == 6) {
			return Direction.SOUTH_WEST;
		} else if (index == 7) {
			return Direction.WEST;
		} else if (index == 8) {
			return Direction.NORTH_WEST;
		} else {
			return Direction.UNKNOWN;
		}
	}
}
