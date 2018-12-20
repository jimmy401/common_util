package com.migu.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IpV6Format {
    private Map<String, String> hexToBinaryMap;
    private  Map<String, String> binaryToHexMap;
    private  String ipv6;

    public IpV6Format() {
        hexToBinaryMap = new HashMap<>();
        hexToBinaryMap.put("0", "0000");
        hexToBinaryMap.put("1", "0001");
        hexToBinaryMap.put("2", "0010");
        hexToBinaryMap.put("3", "0011");
        hexToBinaryMap.put("4", "0100");
        hexToBinaryMap.put("5", "0101");
        hexToBinaryMap.put("6", "0110");
        hexToBinaryMap.put("7", "0111");
        hexToBinaryMap.put("8", "1000");
        hexToBinaryMap.put("9", "1001");
        hexToBinaryMap.put("a", "1010");
        hexToBinaryMap.put("b", "1011");
        hexToBinaryMap.put("c", "1100");
        hexToBinaryMap.put("d", "1101");
        hexToBinaryMap.put("e", "1110");
        hexToBinaryMap.put("f", "1111");

        binaryToHexMap = new HashMap<>();
        binaryToHexMap.put("0000", "0");
        binaryToHexMap.put("0001", "1");
        binaryToHexMap.put("0010", "2");
        binaryToHexMap.put("0011", "3");
        binaryToHexMap.put("0100", "4");
        binaryToHexMap.put("0101", "5");
        binaryToHexMap.put("0110", "6");
        binaryToHexMap.put("0111", "7");
        binaryToHexMap.put("1000", "8");
        binaryToHexMap.put("1001", "9");
        binaryToHexMap.put("1010", "a");
        binaryToHexMap.put("1011", "b");
        binaryToHexMap.put("1100", "c");
        binaryToHexMap.put("1101", "d");
        binaryToHexMap.put("1110", "e");
        binaryToHexMap.put("1111", "f");
    }

    private String hexToBinary(String hexString) {
        hexString = hexString.toLowerCase();
        StringBuilder binarySB = new StringBuilder();
        if (hexString.length() == 0) {
            hexString = "0000";
        }
        if (hexString.length() == 1) {
            hexString = "000" + hexString;
        }
        if (hexString.length() == 2) {
            hexString = "00" + hexString;
        }
        if (hexString.length() == 3) {
            hexString = "0" + hexString;
        }
        for (char item : hexString.toCharArray()) {
            binarySB.append(hexToBinaryMap.get(String.valueOf(item)));
        }

        return binarySB.toString();
    }

    public String[] formatIpV6(String ipv6String) {
        String[] ret = new String[2];
        StringBuilder sb = new StringBuilder();
        ipv6="";
        if (ipv6String.contains("/")) {
            //如果带有掩码格式FE80::/64 , 1:123::ABCD:0:1/96
            String groups[] = ipv6String.split("/");
            ipv6 = groups[0];
            int yanMa = Integer.valueOf(groups[1]);
            String ipv6Items[] = ipv6.split(":");
            long toInsertCount = 8 - Arrays.stream(ipv6Items).filter(p -> !p.equals("")).count();
            fillIpv6(sb, ipv6Items, toInsertCount);

            String yanMaString = sb.toString().substring(0, yanMa);
            StringBuilder ipv6StartInBinary = new StringBuilder(yanMaString);
            StringBuilder ipv6EndInBinary = new StringBuilder(yanMaString);
            int fillCount = 128 - Integer.valueOf(yanMaString.length());
            while (fillCount-- > 0) {
                ipv6StartInBinary.append("0");
                ipv6EndInBinary.append("1");
            }
            ret[0] = binaryToHexInIP(ipv6StartInBinary.toString());
            ret[1] = binaryToHexInIP(ipv6EndInBinary.toString());
        }
        return ret;
    }

    private void fillIpv6(StringBuilder sb, String[] ipv6Items, long toInsertCount) {
        for (String item : ipv6Items) {
            if (!item.equals("")) {
                sb.append(hexToBinary(item));
            } else {
                while (toInsertCount-- > 0) {
                    sb.append(hexToBinary(item));
                }
            }
        }
        while (toInsertCount-- > 0) {
            sb.append(hexToBinary(""));
        }
    }

    private String binaryToHexInIP(String ipInBinaryString) {
        int ipLength = ipInBinaryString.length();
        int index = 0;
        int partCount = 0;
        StringBuilder ret = new StringBuilder();
        while (index < ipLength) {
            String part = ipInBinaryString.substring(index, index + 4);
            ret.append(binaryToHexMap.get(part));
            partCount++;
            if (partCount % 4 == 0 && partCount != 32) {
                ret.append(":");
            }
            index += 4;
        }
        return ret.toString();
    }

    public static void main(String[] args) {
        IpV6Format format = new IpV6Format();
//        String[] ret1 = format.formatIpV6("2001:250:380B::/48");
//        System.out.println("2001:250:380B::/48 range:" );
//        System.out.println("start ip :" + ret1[0]);
//        System.out.println("end ip :" + ret1[1]);
//
//        String[] ret2 = format.formatIpV6("2001:256:800::/37");
//        System.out.println("2001:256:800::/37 range:" );
//        System.out.println("start ip :" + ret2[0]);
//        System.out.println("end ip :" + ret2[1]);

        String[] ret3 = format.formatIpV6("2001:256::/40");
        System.out.println("2001:256::/40 range:");
        System.out.println("start ip " + ret3[0]);
        System.out.println("end ip " + ret3[1]);


//        String[] ret4 = format.formatIpV6("::FFFF:192.168.0.1");
//        System.out.println("::FFFF:192.168.0.1 range:" );
//        System.out.println("start ip :" + ret4[0]);
//        System.out.println("end ip :" + ret4[1]);


    }
}