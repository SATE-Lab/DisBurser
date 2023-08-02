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

package io.redit.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;


public class ZipUtil {
    public static void unTarGzip(String inputFile, String outputDir) throws IOException, InterruptedException{
        File unGzippedFile = unGzip(inputFile, outputDir);
        InputStream is = new FileInputStream(unGzippedFile);
        TarArchiveInputStream tarInputStream = null;

        try {
            tarInputStream = (TarArchiveInputStream)(new ArchiveStreamFactory()).createArchiveInputStream("tar", is);
        } catch (ArchiveException var8) {
        }

        TarArchiveEntry tarEntry = null;

        while((tarEntry = (TarArchiveEntry)tarInputStream.getNextEntry()) != null) {
            File outputFile = new File(outputDir, tarEntry.getName());
            if (tarEntry.isDirectory()) {
                if (!outputFile.exists() && !outputFile.mkdirs()) {
                    throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                }
            } else {
                OutputStream outputFileStream = new FileOutputStream(outputFile);
                IOUtils.copy(tarInputStream, outputFileStream);
                setPermissions(outputFile, tarEntry.getMode());
                outputFileStream.close();
            }
        }

        tarInputStream.close();
        unGzippedFile.delete();
    }

    private static void setPermissions(File inputFile, int mode) throws IOException {
        if (!Files.isSymbolicLink(inputFile.toPath()) && PosixUtil.isPosixFileStore(inputFile)) {
            Set<PosixFilePermission> permissions = PosixUtil.getPosixPermissionsAsSet(mode);
            if (!permissions.isEmpty()) {
                Files.setPosixFilePermissions(inputFile.toPath(), permissions);
            }
        }

    }
    public static void unzip(String src, String dest) throws IOException, InterruptedException, ZipException {
        if (OsUtil.getOS() == null || OsUtil.getOS() == OsUtil.OS.WINDOWS) {
            ZipFile zipFile = new ZipFile(src);
            zipFile.extractAll(dest);
        } else {
            Files.createDirectories(Paths.get(dest));
            Runtime.getRuntime().exec("unzip -uqq " + src + " -d " + dest).waitFor();
        }
    }

    private static File unGzip(String inputFileAddr, String outputDirAddr) throws IOException {
        File inputFile = new File(inputFileAddr);
        File outputDir = new File(outputDirAddr);
        String outputFileName = inputFile.getName().substring(0, inputFile.getName().length() - 3);
        if (!outputFileName.endsWith(".tar")) {
            outputFileName = outputFileName + ".tar";
        }

        File outputFile = new File(outputDir, outputFileName);
        outputDir.mkdirs();
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        FileOutputStream out = new FileOutputStream(outputFile);
        IOUtils.copy(in, out);
        in.close();
        out.close();
        return outputFile;
    }

}

class PosixUtil {
    private static final int O400 = 256;
    private static final int O200 = 128;
    private static final int O100 = 64;
    private static final int O040 = 32;
    private static final int O020 = 16;
    private static final int O010 = 8;
    private static final int O004 = 4;
    private static final int O002 = 2;
    private static final int O001 = 1;

    private PosixUtil() {
    }

    public static boolean isPosixFileStore(File file) throws IOException {
        return isPosixFileStore(file.toPath());
    }

    public static boolean isPosixFileStore(Path path) throws IOException {
        return Files.isSymbolicLink(path) || Files.getFileStore(path).supportsFileAttributeView("posix");
    }

    public static Set<PosixFilePermission> getPosixPermissionsAsSet(int mode) {
        Set<PosixFilePermission> permissionSet = new HashSet();
        if ((mode & 256) == 256) {
            permissionSet.add(PosixFilePermission.OWNER_READ);
        }

        if ((mode & 128) == 128) {
            permissionSet.add(PosixFilePermission.OWNER_WRITE);
        }

        if ((mode & 64) == 64) {
            permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        }

        if ((mode & 32) == 32) {
            permissionSet.add(PosixFilePermission.GROUP_READ);
        }

        if ((mode & 16) == 16) {
            permissionSet.add(PosixFilePermission.GROUP_WRITE);
        }

        if ((mode & 8) == 8) {
            permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        }

        if ((mode & 4) == 4) {
            permissionSet.add(PosixFilePermission.OTHERS_READ);
        }

        if ((mode & 2) == 2) {
            permissionSet.add(PosixFilePermission.OTHERS_WRITE);
        }

        if ((mode & 1) == 1) {
            permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);
        }

        return permissionSet;
    }
}