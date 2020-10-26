package com.yoonje.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Paginator {

    private static final int DEFAULT_PAGE_NUM_PER_VIEW = 10;

    public static Pagination paging(int currentPage, int countPerPage, long totalCount) {
        int endPage = (int) (Math.ceil(currentPage / (double) DEFAULT_PAGE_NUM_PER_VIEW) * DEFAULT_PAGE_NUM_PER_VIEW);
        int startPage = (endPage - DEFAULT_PAGE_NUM_PER_VIEW) + 1;

        int totalPage = (int) (Math.ceil(totalCount / (double) countPerPage));
        if (currentPage > totalPage) {
            currentPage = totalPage;
        }
        if (endPage > totalPage) {
            endPage = totalPage;
        }
        if (startPage > totalPage) {
            startPage = Math.max(1, (endPage - DEFAULT_PAGE_NUM_PER_VIEW) + 1);
        }

        boolean hasPrev = startPage == 1 ? false : true;
        boolean hasNext = endPage * countPerPage >= totalCount ? false : true;

        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;
        pagination.countPerPage = countPerPage;
        pagination.totalCount = totalCount;
        pagination.startPage = startPage;
        pagination.endPage = endPage;
        pagination.hasPrev = hasPrev;
        pagination.hasNext = hasNext;
        return pagination;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pagination {
        private int currentPage;
        private int countPerPage;
        private long totalCount;

        private int startPage;
        private int endPage;
        private boolean hasPrev;
        private boolean hasNext;
    }

}
