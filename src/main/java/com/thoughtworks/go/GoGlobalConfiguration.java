package com.thoughtworks.go;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Extension
public class GoGlobalConfiguration extends GlobalConfiguration {

    public boolean isChecked = true;

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        Boolean selected = (Boolean) ((JSONObject) json.get("com-tw-go")).get("selected");
        if (selected) {
            File file = new File("/tmp/jenkins_migrator");
            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            List<Job> jobs = new ArrayList<Job>();
            for (Item item : Jenkins.getInstance().getAllItems()) {
                jobs.addAll(item.getAllJobs());
            }
            try {
                for (Job job : jobs) {
                    String name = job.getName() + "\n";
                    bufferedOutputStream.write(name.getBytes());
                    bufferedOutputStream.flush();
                }
                bufferedOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
