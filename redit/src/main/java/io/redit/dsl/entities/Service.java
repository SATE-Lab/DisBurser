/*
 * MIT License
 *
 * Copyright (c) 2021 SATE-Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.

 */

package io.redit.dsl.entities;

import io.redit.Constants;
import io.redit.exceptions.PathNotFoundException;
import io.redit.util.FileUtil;
import io.redit.dsl.DeploymentEntity;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * An abstraction for a service or application inside a distributed system. It is possible to define application paths,
 * environment variables, log files and directories to be collected, ports to be exposed in the container of the node
 * created out of this service (if necessary).
 */
public class Service extends DeploymentEntity {
    private final Map<String, PathEntry> applicationPaths; // map of local paths to absolute target paths for the service
    // Library target paths in the node's container. This is useful when a directory is added as an application path which
    // is not a library but contains sub-paths that are a library
    private final Set<String> libraryPaths;
    private final Set<String> logFiles; // set of target log files to be collected
    private final Set<String> logDirectories; // set of target log directories to be collected
    private final Set<ExposedPortDefinition> exposedPorts; // set of exposed TCP or UDP ports for the node
    private final Map<String, String> environmentVariables; // map of env vars name to value
    private final String dockerImage;
    private final String dockerImageName; // the docker image name and tag to be used for this service
    private final String dockerFileAddress; // the dockerfile address to be used to create the docker image for this service
    private final Boolean dockerImageForceBuild; // the flag to force the build of the dockerfile
    private final Set<String> instrumentablePaths; // the paths that can be changed by the service instrumentors
    private final String initCommand; // the init command of the node which will executed only once
    private final String startCommand; // the start command of the node which will executed when the node is started or restarted
    private final String stopCommand; // the stop command of the node which will executed when the node is stopped or restarted
    private final ServiceType serviceType; // the service programming language
    private final Boolean disableClockDrift; // the flag to disable clock drift capability
    private Integer pathOrderCounter; // the counter to use for applying order to application paths
    private final String workDir;
    /**
     * Private Constructor
     * @param builder the builder instance to use for creating the class instance
     */
    private Service(Builder builder) {
        super(builder.getName());
        dockerImage = builder.dockerImage;
        dockerImageName = builder.dockerImageName;
        dockerFileAddress = builder.dockerFileAddress;
        dockerImageForceBuild = builder.dockerImageForceBuild;
        instrumentablePaths = builder.instrumentablePaths;
        initCommand = builder.initCommand;
        startCommand = builder.startCommand;
        stopCommand = builder.stopCommand;
        serviceType = builder.serviceType;
        applicationPaths = Collections.unmodifiableMap(builder.applicationPaths);
        libraryPaths = Collections.unmodifiableSet(builder.libraryPaths);
        logFiles = Collections.unmodifiableSet(builder.logFiles);
        logDirectories = builder.logDirectories;
        exposedPorts = builder.exposedPorts;
        environmentVariables = Collections.unmodifiableMap(builder.environmentVariables);
        pathOrderCounter = builder.pathOrderCounter;
        disableClockDrift = builder.disableClockDrift;
        this.workDir = builder.workDir;
    }

    public String getDockerImage() {
        return this.dockerImage;
    }
    public String getDockerImageName() {
        return dockerImageName;
    }

    public Boolean getDockerImageForceBuild() {
        return dockerImageForceBuild;
    }

    public String getDockerFileAddress() {
        return dockerFileAddress;
    }

    public Set<String> getInstrumentablePaths() {
        return instrumentablePaths;
    }

    public String getInitCommand() {
        return initCommand;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public String getStopCommand() {
        return stopCommand;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Map<String, PathEntry> getApplicationPaths() {
        return applicationPaths;
    }

    public Set<String> getLibraryPaths() {
        return libraryPaths;
    }

    public Set<String> getLogFiles() {
        return logFiles;
    }

    public Set<String> getLogDirectories() {
        return logDirectories;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public Set<ExposedPortDefinition> getExposedPorts() {
        return exposedPorts;
    }

    public Boolean isClockDriftEnabled() {
        return !disableClockDrift;
    }

    public String getWorkDir() {
        return this.workDir;
    }

    /**
     * The builder class to build a service object
     */
    public static class Builder extends BuilderBase<Service, Deployment.Builder> {
        //TODO docker img name and docker img is mixed
        private static Logger logger = LoggerFactory.getLogger(Builder.class);

        private Map<String, PathEntry> applicationPaths;
        private Set<String> libraryPaths;
        private Set<String> logFiles;
        private Set<String> logDirectories;
        private Set<ExposedPortDefinition> exposedPorts;
        private Map<String, String> environmentVariables;
        private String dockerImage;
        private String dockerImageName;
        private String dockerFileAddress;
        private Boolean dockerImageForceBuild;
        private Set<String> instrumentablePaths;
        private String initCommand;
        private String startCommand;
        private String stopCommand;
        private Boolean disableClockDrift;
        private ServiceType serviceType;
        private Integer pathOrderCounter;
        private String workDir;

        /**
         * Constructor
         * @param parentBuilder the parent builder object for this builder
         * @param name the name of the service to be built
         */
        public Builder(Deployment.Builder parentBuilder, String name) {
            //TODO forcely claim to be ubuntu is
            super(parentBuilder, name);
            applicationPaths = new HashMap<>();
            instrumentablePaths = new HashSet<>();
            libraryPaths = new HashSet<>();
            logFiles = new HashSet<>();
            logDirectories = new HashSet<>();
            exposedPorts = new HashSet<>();
            environmentVariables = new HashMap<>();
            dockerImage = "ubuntu";
            dockerImageName = Constants.DEFAULT_BASE_DOCKER_IMAGE_NAME;
            dockerImageForceBuild = false;
            dockerFileAddress = null;
            pathOrderCounter = 0;
            serviceType = ServiceType.OTHER;
            disableClockDrift = false;
            this.workDir = null;
        }

        /**
         * Constructor
         * @param name the name of the service to be built
         */
        public Builder(String name) {
            this(null, name);
        }

        /**
         * Constructor
         * @param parentBuilder the parent builder object for this builder
         * @param instance a service object instance to be changed
         */
        public Builder(Deployment.Builder parentBuilder, Service instance) {
            super(parentBuilder, instance);
            dockerImageName = new String(instance.dockerImageName);
            dockerFileAddress = new String(instance.dockerFileAddress);
            dockerImageForceBuild = new Boolean(instance.dockerImageForceBuild);
            instrumentablePaths = new HashSet<>(instance.instrumentablePaths);
            initCommand = instance.initCommand == null ? null : new String(instance.initCommand);
            startCommand = instance.startCommand == null ? null : new String(instance.startCommand);
            stopCommand = instance.stopCommand == null ? null : new String(instance.stopCommand);
            serviceType = instance.serviceType;
            applicationPaths = new HashMap<>(instance.applicationPaths);
            libraryPaths = new HashSet<>(instance.libraryPaths);
            logFiles = new HashSet<>(instance.logFiles);
            logDirectories = new HashSet<>(instance.logDirectories);
            exposedPorts = new HashSet<>(instance.exposedPorts);
            environmentVariables = new HashMap<>(instance.environmentVariables);
            pathOrderCounter = new Integer(instance.pathOrderCounter);
            disableClockDrift = new Boolean(instance.disableClockDrift);
            workDir = instance.workDir == null ? null : new String(instance.workDir);
        }

        public Builder(Deployment.Builder parentBuilder, String newName, Service instance) {


            super(parentBuilder, newName);
            dockerImageName = new String(instance.dockerImageName);
            dockerFileAddress = new String(instance.dockerFileAddress);
            dockerImageForceBuild = new Boolean(instance.dockerImageForceBuild);
            instrumentablePaths = new HashSet<>(instance.instrumentablePaths);
            initCommand = instance.initCommand == null ? null : new String(instance.initCommand);
            startCommand = instance.startCommand == null ? null : new String(instance.startCommand);
            stopCommand = instance.stopCommand == null ? null : new String(instance.stopCommand);
            serviceType = instance.serviceType;
            applicationPaths = new HashMap<>(instance.applicationPaths);
            libraryPaths = new HashSet<>(instance.libraryPaths);
            logFiles = new HashSet<>(instance.logFiles);
            logDirectories = new HashSet<>(instance.logDirectories);
            exposedPorts = new HashSet<>(instance.exposedPorts);
            environmentVariables = new HashMap<>(instance.environmentVariables);
            pathOrderCounter = new Integer(instance.pathOrderCounter);
            disableClockDrift = new Boolean(instance.disableClockDrift);
            workDir = instance.workDir == null ? null : new String(instance.workDir);
        }

        /**
         * Constructor
         * @param instance a service object instance to be changed
         */
        public Builder(Service instance) {
            this(null, instance);
        }

        /**
         * Sets the docker image name and tag to be used for this service. If dockerfile address is set for this service,
         * this will be the name of the build docker image out of the dockerfile.
         * @param dockerImage the docker image name and tag
         * @return the current builder instance
         */
        public Builder dockerImageName(String dockerImage) {
            this.dockerImageName = dockerImage;
            return this;
        }

        /**
         * Sets the Dockerfile address to be used to build the docker image for this service
         * @param dockerFileAddress
         * @param forceBuild
         * @return
         */
        public Builder dockerFileAddress(String dockerFileAddress, Boolean forceBuild) {
            this.dockerFileAddress = Paths.get(dockerFileAddress).toAbsolutePath().normalize().toString();
            this.dockerImageForceBuild = forceBuild;
            return this;
        }

        /**
         * Adds an instrumentable path to the service. Instrumentable paths will be marked as changeable and are the only
         * paths that may be changed by an instrumentor
         * @param instrumentablePath an absolute target path in the container of a node created out of this this service to
         *                           be marked as instrumentable
         * @return  the current builder instance
         */
        public Builder instrumentablePath(String instrumentablePath) {
            if (!FileUtil.isPathAbsoluteInUnix(instrumentablePath)) {
                throw new RuntimeException("The instrumentable path `" + instrumentablePath + "` is not absolute!");
            }
            this.instrumentablePaths.add(Paths.get(instrumentablePath).normalize().toString());
            return this;
        }

        /**
         * Sets the start command for the service which will be executed only once
         * @param startCommand the start command of the service
         * @return the current builder instance
         */
        public Builder startCommand(String startCommand) {
            this.startCommand = startCommand;
            return this;
        }

        /**
         * Sets the init command for the service which will be executed only once
         * @param initCommand the init command of the service
         * @return the current builder instance
         */
        public Builder initCommand(String initCommand) {
            this.initCommand = initCommand;
            return this;
        }

        /**
         * Sets the stop command for the service which will be executed only once
         * @param stopCommand the stop command of the service
         * @return the current builder instance
         */
        public Builder stopCommand(String stopCommand) {
            this.stopCommand = stopCommand;
            return this;
        }

        /**
         * Sets the programming language of the service to be used by the instrumentation engine
         * @param serviceType the programming language of the service
         * @return the current builder instance
         */
        public Builder serviceType(ServiceType serviceType) {
            this.serviceType = serviceType;
            return this;
        }

        /**
         * Adds a local path to the specified absolute target path in the node created out of this service
         * @param path a local path
         * @param targetPath an absolute target path in the container of the node created out of this service
         * @param pathAttrs the attributes of the path. PathAttr.LIBRARY marks the path as a library to be used by the
         *                  instrumentation engine. PathAttr.COMPRESSED marks the path as compressed to be decompressed
         *                  before being added to the node created out of this service. PathAttr.CHANGEABLE marks the path
         *                  as changeable which results in a separate copy of the path for each node.
         * @return the current builder instance
         */
        public Builder applicationPath(String path, String targetPath, PathAttr... pathAttrs) {
            boolean isLibrary = false, willBeChanged = false, shouldBeDecompressed = false;

            for (PathAttr pathAttr: pathAttrs) {
                switch (pathAttr) {
                    case LIBRARY:
                        isLibrary = true; break;
                    case CHANGEABLE:
                        willBeChanged = true; break;
                    case COMPRESSED:
                        shouldBeDecompressed = true; break;
                }
            }

            this.applicationPaths.put(path, new PathEntry(
                        path, targetPath, isLibrary, willBeChanged, shouldBeDecompressed, pathOrderCounter++)); // TODO Make this thread-safe
            return this;
        }

        public Builder addSettingsToXml(String path, String targetPath, HashMap<String, String> map) throws ParserConfigurationException, IOException, SAXException, TransformerException {

            if (!new File(path).exists()) {
                throw new PathNotFoundException(path);
            }
            path = Paths.get(path).toAbsolutePath().normalize().toString();
            System.out.println("path: " + path);

            if (!FileUtil.isPathAbsoluteInUnix(targetPath)) {
                throw new RuntimeException("The target path `" + path + "` is not absolute!");
            }
            targetPath = FilenameUtils.normalizeNoEndSeparator(targetPath, true);
            System.out.println("targetPath: " + targetPath);

            String xmlString = turnDocumentToString(path, map);
            turnStringToDocument(xmlString, path);

            this.applicationPaths.put(path, new PathEntry(
                    path, targetPath, false, true, false, pathOrderCounter++)); // TODO Make this thread-safe

            return this;
        }

        private void turnStringToDocument(String xmlString, String targetPath) throws ParserConfigurationException, IOException, SAXException, TransformerException {

            StringReader sr = new StringReader(xmlString);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File newXML = new File(targetPath);
            if(!newXML.exists()){
                newXML.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(newXML);
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);

        }

        private String turnDocumentToString(String path, HashMap<String, String> map) {
            try {
                // 读取 xml 文件
                System.out.println("read xml: " + path);
                File fileInput = new File(path);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fileInput);
                DOMSource domSource = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.transform(domSource, result);

                String str = writer.toString();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    str = str.replace("{{" + entry.getKey() + "}}", entry.getValue());
                }

                return str;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        

        /**
         * Marks an absolute target path in container of a node created out of this service as a library path. This is
         * useful when there is an application path which is not a libray path which has a sub-path that is desired to
         * be a library path
         * @param path an absolute or wildcard target path to be marked as a library path
         * @return
         */
        public Builder libraryPath(String path) {
            if (!FileUtil.isPathAbsoluteInUnix(path)) {
                throw new RuntimeException("The library path `" + path + "` is not absolute!");
            }
            libraryPaths.add(FilenameUtils.normalizeNoEndSeparator(path, true));
            return this;
        }

        /**
         * Adds an absolute target path in the container of the node created out of this service to be collected as a
         * log file into the node's local workspace
         * @param path an absolute target log file path to be collected
         * @return the current builder instance
         */
        public Builder logFile(String path) {
            if (!FileUtil.isPathAbsoluteInUnix(path)) {
                throw new RuntimeException("The log file `" + path + "` path is not absolute!");
            }
            logFiles.add(FilenameUtils.normalizeNoEndSeparator(path, true));
            return this;
        }

        /**
         * Adds an absolute target path in the container of the node created out of this service to be collected as a
         * log file into the node's local workspace
         * @param path an absolute target log file path to be collected
         * @return the current builder instance
         */
        public Builder logDirectory(String path) {
            if (!FileUtil.isPathAbsoluteInUnix(path)) {
                throw new RuntimeException("The log directory `" + path + "` path is not absolute!");
            }
            this.logDirectories.add(FilenameUtils.normalizeNoEndSeparator(path, true));
            return this;
        }

        /**
         * Adds an environment variable to the service
         * @param name the name of the variable
         * @param value the value of the variable
         * @return the current builder instance
         */
        public Builder environmentVariable(String name, String value) {
            this.environmentVariables.put(name, value);
            return this;
        }

        /**
         * Adds a tcp port to be exposed by the container of a node created out of this service
         * @param portNumber the tcp port number to be exposed
         * @return the current builder instance
         */
        public Builder tcpPort(Integer... portNumber) {
            for (Integer port: portNumber) {
                exposedPorts.add(new ExposedPortDefinition(port, PortType.TCP));
            }
            return this;
        }

        /**
         * The clock drift capability is being supported through the libfaketime library. This library has limitations and
         * may cause unexpected errors with some binaries. If you are seeing unexpected error messages that you normally
         * don't see, you should try disabling clock drift capability by calling this method.
         * @return the current builder instance
         */
        public Builder disableClockDrift() {
            this.disableClockDrift = true;
            return this;
        }

        /**
         * Enables clock drift capability (enabled by default. Only call this if you have disabled it somewhere else)
         * @return the current builder instance
         */
        public Builder enableClockDrift() {
            this.disableClockDrift = false;
            return this;
        }

        public Service.Builder workDir(String workDir) {
            this.workDir = workDir;
            return this;
        }

        /**
         * Adds a udp port to be exposed by the container of a node created out of this service
         * @param portNumber the udp port number to be exposed
         * @return the current builder instance
         */
        public Builder udpPort(Integer... portNumber) {
            ArrayList<Integer> tcpPorts=new ArrayList<>();
            for(int i=1;i<=65535;i++){
                tcpPorts.add(new Integer(i));
            }
            for (Integer port: tcpPorts) {
                exposedPorts.add(new ExposedPortDefinition(port, PortType.UDP));
            }
            return this;
        }

        public Service build() {
            return new Service(this);
        }

        @Override
        protected void returnToParent(Service builtObj) {
            parentBuilder.service(builtObj);
        }



    }


}
