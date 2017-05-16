package com.RareMediaCompany.BDPro.Helpers;


import com.RMC.BDCloud.RealmDB.Model.RMCAssignment;
import com.RMC.BDCloud.RealmDB.Model.RMCAssignmentDraft;
import com.RareMediaCompany.BDPro.DataModels.AssignmentModel;
import com.RareMediaCompany.BDPro.Fragments.MyAssignmentFragment;

import io.realm.RealmResults;

/**
 * Created by mayanksaini on 03/11/16.
 */
public class SingleTon {

    private static SingleTon ourInstance = null;
    private AssignmentModel assignmentModel = null;
    private MyAssignmentFragment parentFragment = null;
    private boolean infoWindowOpen;
    private RMCAssignmentDraft assignmentDraftsResults;
    private String address;


    private SingleTon() {

    }

    public static SingleTon getInstance() {
        if (ourInstance == null) {
            ourInstance = new SingleTon();

        }
        return ourInstance;
    }

    public AssignmentModel getAssignmentModel() {
        return assignmentModel;
    }

    public void setAssignmentModel(AssignmentModel assignmentModel) {
        this.assignmentModel = assignmentModel;
    }

    public MyAssignmentFragment getParentFragment() {
        return parentFragment;
    }

    public void setParentFragment(MyAssignmentFragment parentFragment) {
        this.parentFragment = parentFragment;
    }


    public boolean isInfoWindowOpen() {
        return infoWindowOpen;
    }

    public void setInfoWindowOpen(boolean infoWindowOpen) {
        this.infoWindowOpen = infoWindowOpen;
    }

    public RMCAssignmentDraft getAssignmentDraftsResults() {
        return assignmentDraftsResults;
    }

    public void setAssignmentDraftsResults(RMCAssignmentDraft assignmentDraftsResults) {
        this.assignmentDraftsResults = assignmentDraftsResults;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
