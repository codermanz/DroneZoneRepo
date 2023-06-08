package utils.jsonObjectModels;

import java.util.List;

public class TimeStep {

    private int iteration;
    private String description;
    List<Observation> observations;

    public TimeStep() {

    }

    public TimeStep(int iteration, String description, List<Observation> observations) {
        this.iteration = iteration;
        this.description = description;
        this.observations = observations;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    @Override
    public String toString() {
        return ("Iteration: " + this.iteration + "\n\tDescription: " + this.description +
                "\n\tObservations: " + this.observations.toString());
    }
}
