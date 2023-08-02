package io.redit;

import io.redit.util.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TarGzDecompressTest {
    public static void main(String[] args) {
        try{
            ZipUtil.unTarGzip("redit/src/test/resource/hellotargz.tar.gz","redit/src/test/resource");
            File f= new File("redit/src/test/resource/targz/hellotargz");
            if(!f.exists()){
                System.out.println("Decompress failed! ");
            }
            else{
                BufferedReader in = new BufferedReader(new FileReader("redit/src/test/resource/targz/hellotargz"));
                String str=in.readLine();
                if(!str.equals("hellotargz")){
                    System.out.println("Data error!");
                }
                else{
                    System.out.println("Successfully decompress tar.gz file!");
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            File f= new File("redit/src/test/resource/targz/");
            TarGzDecompressTest.deleteDir(f);
        }
    }
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {

            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
