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

package io.redit.workspace;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象构造类，节点工作空间需要调度路径、库文件路径、工作目录、根目录、日志目录、日志目录映射等
 */
public class NodeWorkspace {

    public static class PathMappingEntry {
        private final String source;
        private final String destination;
        private final Boolean readOnly;

        public PathMappingEntry(String source, String destination, Boolean readOnly) {
            this.source = source;
            this.destination = destination;
            this.readOnly = readOnly;
        }

        public String getSource() {
            return source;
        }

        public String getDestination() {
            return destination;
        }

        public Boolean isReadOnly() {
            return readOnly;
        }
    }

    private final Set<String> instrumentablePaths;
    private final Set<String> libraryPaths;
    private final String workingDirectory;
    private final String rootDirectory;
    private final String logDirectory;
    private final Map<String, String> logDirectoriesMap;
    private final Map<String, String> logFilesMap;
    private final Map<String, String> sharedDirectoriesMap;
    private final List<PathMappingEntry> pathMappingList;

    /**
     *
     * @param instrumentablePaths 调度路径
     * @param libraryPaths 库文件路径
     * @param workingDirectory 工作目录
     * @param rootDirectory 根目录
     * @param logDirectory 日志目录
     * @param logDirectoriesMap 日志目录映射
     * @param logFilesMap 日志文件映射
     * @param sharedDirectoriesMap 共享目录映射
     * @param pathMappingList 路径映射列表
     */
    public NodeWorkspace(Set<String> instrumentablePaths, Set<String> libraryPaths, String workingDirectory,
                         String rootDirectory, String logDirectory, Map<String, String> logDirectoriesMap,
                         Map<String, String> logFilesMap, Map<String, String> sharedDirectoriesMap,
                         List<PathMappingEntry> pathMappingList) {
        this.instrumentablePaths = instrumentablePaths;
        this.libraryPaths = libraryPaths;
        this.workingDirectory = workingDirectory;
        this.rootDirectory = rootDirectory;
        this.logDirectory = logDirectory;
        this.logDirectoriesMap = logDirectoriesMap;
        this.logFilesMap = logFilesMap;
        this.sharedDirectoriesMap = sharedDirectoriesMap;
        this.pathMappingList = pathMappingList;
    }

    public Set<String> getInstrumentablePaths() {
        return instrumentablePaths;
    }

    public Set<String> getLibraryPaths() {
        return libraryPaths;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public Map<String, String> getLogDirectoriesMap() {
        return logDirectoriesMap;
    }

    public Map<String, String> getLogFilesMap() {
        return logFilesMap;
    }

    public Map<String, String> getSharedDirectoriesMap() {
        return sharedDirectoriesMap;
    }

    public List<PathMappingEntry> getPathMappingList() {
        return pathMappingList;
    }
}
