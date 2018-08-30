package singa.bio.exchange.model;

import bio.singa.simulation.model.simulation.Simulation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author cl
 */
public class Metadata {

    @JsonProperty("creation-time-stamp")
    private String creationTimeStamp;

    @JsonProperty("simulation-tool")
    private String simulationTool;

    @JsonProperty("simulation-tool-version")
    private String simulationToolVersion;

    @JsonProperty
    private String comment;

    public Metadata() {

    }

    public static Metadata forSinga() {
        Metadata metadata = new Metadata();
        Date date = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss'Z'").format(date);
        metadata.setCreationTimeStamp(timeStamp);
        metadata.setSimulationTool("SiNGA");
        metadata.setSimulationToolVersion(Simulation.class.getPackage().getImplementationVersion());
        metadata.setComment("automatically created by singa-exchange");
        return metadata;
    }

    public String getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(String creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public String getSimulationTool() {
        return simulationTool;
    }

    public void setSimulationTool(String simulationTool) {
        this.simulationTool = simulationTool;
    }

    public String getSimulationToolVersion() {
        return simulationToolVersion;
    }

    public void setSimulationToolVersion(String simulationToolVersion) {
        this.simulationToolVersion = simulationToolVersion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
