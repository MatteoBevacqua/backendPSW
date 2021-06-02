package edu.kit.ifv.mobitopp.publictransport.connectionscan;

import edu.kit.ifv.mobitopp.time.Time;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;

import java.util.function.BiConsumer;

interface ArrivalTimes {

	void initialise(BiConsumer<Stop, Time> consumer);

	void set(Stop stop, Time time);

	Time getConsideringMinimumChangeTime(Stop stop);

	Time get(Stop stop);

	Time startTime();

}