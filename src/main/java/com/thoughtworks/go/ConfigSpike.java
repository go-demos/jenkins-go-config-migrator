package com.thoughtworks.go;

import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Job;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.List;

public class ConfigSpike {

    public static void main(String[] args) {
        Jenkins instance = Jenkins.getInstance();
        List<Job> jobs = new ArrayList<Job>();
        for (Item item : instance.getAllItems()) {
            jobs.addAll(item.getAllJobs());
        }
        for (Job job : jobs) {
            List<Action> actions = job.getActions();
            for (Action action : actions) {

            }
        }
    }
}
