
package com.buildsmart.vendor.util;


import static com.buildsmart.common.util.IdGeneratorUtil.extractNumericSuffix;

public final class IdGeneratorUtil {

    private IdGeneratorUtil() {
    }

    public static String nextVendorId(String lastVendorId) {
        int next = extractNumericSuffix(lastVendorId, 3) + 1;
        return String.format("VENVN%03d", next);
    }
}