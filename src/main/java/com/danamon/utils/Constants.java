package com.danamon.utils;

public class Constants {

    private static final String UTILITY_CLASS_EX = "Utility class";

    private Constants() {
        throw new IllegalStateException(UTILITY_CLASS_EX);
    }

    public static final class PageParameter {
        private PageParameter() {
            throw new IllegalStateException(UTILITY_CLASS_EX);
        }

        public static final String LIST_DATA = "listData";
        public static final String TOTAL_PAGES = "totalPages";
        public static final String TOTAL_ELEMENTS = "totalElements";
    }


}
