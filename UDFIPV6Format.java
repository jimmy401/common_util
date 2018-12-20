package cn.migu.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import java.util.HashMap;
import java.util.Map;

public class UDFIPV6Format extends UDF {
    private static Map<String, String> hexToBinaryMap;
    private static Map<String, String> binaryToHexMap;
    private static Map<Integer, String> binaryFillMap;

    public UDFIPV6Format() {
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

        binaryFillMap = new HashMap<>();
        binaryFillMap.put(1, "0");
        binaryFillMap.put(2, "00");
        binaryFillMap.put(3, "000");
        binaryFillMap.put(4, "0000");
        binaryFillMap.put(5, "00000");
        binaryFillMap.put(6, "000000");
        binaryFillMap.put(7, "0000000");
    }

    /**
     * format ipv6 to standard format
     */
    public String evaluate(Text n) {
        if (n == null || n.toString().trim().equals("")) {
            return null;
        }
        String ipv6String = n.toString();
        StringBuilder sb = new StringBuilder();
        if (ipv6String.contains(".")) {
            //ipv4混合模式
            String ipv6Items[] = ipv6String.split(":");
            int notNullCount = 0;
            for (String item : ipv6Items) {
                if (!item.equals("")) {
                    notNullCount++;
                }
            }
            int toInsertCount = 7 - notNullCount;
            fillIpv6(sb, ipv6Items, toInsertCount);
        } else {
            String ipv6Items[] = ipv6String.split(":");
            int notNullCount = 0;
            for (String item : ipv6Items) {
                if (!item.equals("")) {
                    notNullCount++;
                }
            }
            int toInsertCount = 8 - notNullCount;
            fillIpv6(sb, ipv6Items, toInsertCount);
        }
        String ipv6InHex = binaryToHexInIP(sb.toString());

        return ipv6InHex;
    }

    private void fillIpv6(StringBuilder sb, String[] ipv6Items, long toInsertCount) {
        for (String item : ipv6Items) {
            if (!item.equals("")) {
                if (item.contains(".")) {
                    for (String part : item.split("\\.")) {
                        sb.append(fillBinary(Integer.toBinaryString(Integer.valueOf(part))));
                    }
                } else {
                    sb.append(hexToBinary(item));
                }
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

    private String fillBinary(String input) {
        int count = 8 - input.length();
        if (count > 1) {
            return binaryFillMap.get(count) + input;
        } else {
            return input;
        }
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
        //BigInteger ret = StringToBigInt("2001:256:101:2001:256:101:2001:256");
        //System.out.println(ret);
        UDFIPV6Format format = new UDFIPV6Format();
        String ret1 = format.evaluate(new Text("::192.168.0.1"));
        String ret2 = format.evaluate(new Text("2001:DB8:0:23:8:800:200C:417A"));
        String ret3 = format.evaluate(new Text("2001::800:200C:417A"));
        System.out.println(ret1);
        System.out.println(ret2);
        System.out.println(ret3);
    }
}
