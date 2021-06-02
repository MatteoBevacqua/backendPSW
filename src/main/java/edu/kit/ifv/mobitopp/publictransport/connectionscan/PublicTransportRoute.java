package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.Time;

import java.util.List;

public interface PublicTransportRoute {

	Stop start();
	
	Stop end();
	
	Time arrival();
	
	RelativeTime duration();

	List<Connection> connections();

}