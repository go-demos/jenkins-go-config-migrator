package com.thoughtworks.go;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Project;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import hudson.tasks.Builder;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultDocument;
import org.dom4j.tree.DefaultElement;
import org.jdom2.input.SAXBuilder;
import org.kohsuke.stapler.StaplerRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Extension
public class GoGlobalConfiguration extends GlobalConfiguration {

    public boolean isChecked = true;

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        Boolean selected = (Boolean) ((JSONObject) json.get("com-tw-go")).get("selected");
        if (selected) {
            File file = new File("/tmp/jenkins_migrator");
            Document doc = new DefaultDocument(new DefaultElement("pipelines"));
            Element rootElement = doc.getRootElement();
            for (Project project : Jenkins.getInstance().getProjects()) {
                String name = project.getName();
                Element pipeline = rootElement.addElement("pipeline");
                pipeline.addAttribute("name", name);
                Element materials = pipeline.addElement("materials");
                Element stage = pipeline.addElement("stage");
                stage.addAttribute("name", "defaultStage");
                Element jobs = stage.addElement("jobs");
                Element job = jobs.addElement("job");
                job.addAttribute("name", "defaultJob");
                Element tasks = job.addElement("tasks");
                try {
                    String s = project.getConfigFile().asString();
                    Document projectDoc = new SAXReader().read(new ByteArrayInputStream(s.getBytes()));
                    Element scm = (Element) projectDoc.selectSingleNode(".//scm");
                    Attribute klazz = scm.attribute("class");
                    if (klazz.getText().equals("hudson.scm.SubversionSCM")) {
                        Element svn = materials.addElement("svn");
                        svn.addAttribute("url", scm.selectSingleNode(".//locations//remote").getText());
                    }
                    Element builders = (Element) projectDoc.selectSingleNode(".//builders");
                    Iterator<Element> iterator = builders.elementIterator();
                    while (iterator.hasNext()) {
                        Element element = iterator.next();
                        if (element.getName().equals("hudson.tasks.Ant")) {
                            Element ant = tasks.addElement("ant");
                            ant.addAttribute("buildFile", element.selectSingleNode(".//buildFile").getText());
                            ant.addAttribute("target", element.selectSingleNode(".//targets").getText());
                        }
                    }
                    FileUtils.writeStringToFile(file, doc.asXML());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
}