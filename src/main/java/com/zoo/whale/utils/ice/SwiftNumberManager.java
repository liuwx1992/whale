package com.zoo.whale.utils.ice;

import org.apache.commons.lang.StringUtils;


public class SwiftNumberManager {
    private static long sequenceId = System.currentTimeMillis();
    private final static Object syncLock = new Object();
    /**
     * 默认API_CODE
     */
    public static final String API_CODE = "1234567";

    private SwiftNumberManager() {
    }

    ;

    private static volatile SwiftNumberManager swiftNumberManager = null;

    public static SwiftNumberManager getSwiftNumberManager() {
        if (swiftNumberManager == null) {
            synchronized (syncLock) {
                if (swiftNumberManager == null) {
                    swiftNumberManager = new SwiftNumberManager();
                }
            }
        }
        return swiftNumberManager;
    }

    public String getSwiftNumber() {
        String date = DateUtil.getNowyyyyMMddHHmmss();
        String nextTime = getNextId() + "";
        date = date + "_" + nextTime.substring(nextTime.length() - 4);
        return date;
    }

    public String getSwiftNumber(String apiCode) {
        if (StringUtils.isBlank(apiCode)) {
            apiCode = API_CODE;            //默认api_code
        }
        StringBuilder builder = new StringBuilder(apiCode);
        String secDate = DateUtil.getNowyyyyMMddHHmmss();
        String nextSeq = String.valueOf(getNextId());
        String seqNum = nextSeq.substring(nextSeq.length() - 4);
        builder.append("_").append(secDate).append("_").append(seqNum);
        return builder.toString();
    }

    private synchronized long getNextId() {
        return sequenceId++;
    }

//	public static void main(String[] args) {
//
//		System.out.println(SwiftNumberManager.getSwiftNumberManager().getSwiftNumber());
//		System.out.println(SwiftNumberManager.getSwiftNumberManager().getSwiftNumber());
//	}

}
