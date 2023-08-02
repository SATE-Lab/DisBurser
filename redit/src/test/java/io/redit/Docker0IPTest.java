package io.redit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Docker0IPTest {
    public static void main(String[] args) {
        String pattern =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"; try {
            Enumeration<InetAddress> InetAddressList= NetworkInterface.getByName("docker0").getInetAddresses();
            while(InetAddressList.hasMoreElements()) {
                String str= InetAddressList.nextElement().getHostAddress();
                if(str.matches(pattern)){
                    System.out.println(str);
                }
            }
                    }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
