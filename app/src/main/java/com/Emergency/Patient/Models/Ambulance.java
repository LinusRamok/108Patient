package com.Emergency.Patient.Models;

/**
 * Created by kamlesh on 11-02-2018.
 */

public class Ambulance {

    private Locate locate;
    private ambStatus AmbStatus;


    public Ambulance() {
        super();
    }

    public Ambulance(Locate locate) {

        this.locate=locate;
    }

    public Locate getLocate() {
        return locate;
    }


    public void setAmbStatus(ambStatus ambStatus) {
        AmbStatus = ambStatus;
    }

    public void setLocate(Locate locate) {
        this.locate = locate;
    }


    public ambStatus getAmbStatus() {
        return AmbStatus;
    }

}
