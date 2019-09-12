package com.lyh.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IpConfigUtil
{
    private static Logger logger = LoggerFactory.getLogger(IpConfigUtil.class); 
    
    public static Map<String, String> getLocalIpAddr()
    {
    	Map<String, String> ipmap = new HashMap<String, String>();
        try 
        {
        	Enumeration<NetworkInterface> interfaces=NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                NetworkInterface ni=(NetworkInterface)interfaces.nextElement();
                Enumeration<InetAddress> ipAddrEnum = ni.getInetAddresses();
                while(ipAddrEnum.hasMoreElements())
                {
                    InetAddress addr = (InetAddress)ipAddrEnum.nextElement();
                    if (addr.isLoopbackAddress() == true)
                    {
                        continue;
                    }
                    
                    String ip = addr.getHostAddress();
                    if (ip.indexOf(":") != -1)
                    {
                        //skip the IPv6 addr
                        continue;
                    }
                        
                    logger.debug("Interface: " + ni.getName()
                        + ", IP: " + ip);
                    ipmap.put(ni.getName(),ip);
                }
            }            
        }
        catch (Exception e) 
        {            
            e.printStackTrace();
            logger.error("Failed to get local ip list. " + e.getMessage());
            throw new RuntimeException("Failed to get local ip list");
        }
        
        return ipmap;
    }
    
    public static String getIpByNetName(String eth){
    	Map<String, String> addr = getLocalIpAddr();
    	return addr.get(eth);
    }
    
    public static void main(String args[])
    {
    	System.out.println(getIpByNetName("ppp0"));
    }
    
}
