package com.yoonje.service;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PaginatorTest {

    @Test
    public void 페이지가_한개만_있는경우() {
        // given
        int currentPage = 1;
        int countPerPage = 10;
        int totalCount = 10;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getStartPage(), is(1));
        assertThat(pagination.getEndPage(), is(1));
        assertThat(pagination.getHasPrev(), is(false));
        assertThat(pagination.getHasNext(), is(false));
    }

    @Test
    public void 페이지당_문서수보다_전체카운트가_적을경우() {
        // given
        int currentPage = 1;
        int countPerPage = 10;
        int totalCount = 3;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getStartPage(), is(1));
        assertThat(pagination.getEndPage(), is(1));
        assertThat(pagination.getHasPrev(), is(false));
        assertThat(pagination.getHasNext(), is(false));
    }

    @Test
    public void prev_next가_다있는경우() {
        // given
        int currentPage = 4;
        int countPerPage = 10;
        int totalCount = 120;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getStartPage(), is(1));
        assertThat(pagination.getEndPage(), is(10));
        assertThat(pagination.getHasPrev(), is(false));
        assertThat(pagination.getHasNext(), is(true));
    }

    @Test
    public void 마지막페이지() {
        int currentPage = 12;
        int countPerPage = 10;
        int totalCount = 120;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getStartPage(), is(11));
        assertThat(pagination.getEndPage(), is(12));
        assertThat(pagination.getHasPrev(), is(true));
        assertThat(pagination.getHasNext(), is(false));
    }

    @Test
    public void 현재페이지가_유효하지_않은경우() {
        int currentPage = 13;
        int countPerPage = 10;
        int totalCount = 120;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getCurrentPage(), is(12));
        assertThat(pagination.getStartPage(), is(11));
        assertThat(pagination.getEndPage(), is(12));
        assertThat(pagination.getHasPrev(), is(true));
        assertThat(pagination.getHasNext(), is(false));
    }

    @Test
    public void 시작페이지기_전체페이지수보다_큰경우() {
        int currentPage = 110;
        int countPerPage = 5;
        int totalCount = 40;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getCurrentPage(), is(8));
        assertThat(pagination.getStartPage(), is(1));
        assertThat(pagination.getEndPage(), is(8));
        assertThat(pagination.getHasPrev(), is(false));
        assertThat(pagination.getHasNext(), is(false));
    }

    @Test
    public void 시작페이지기_전체페이지수보다_큰경우_이전페이지존재() {
        int currentPage = 110;
        int countPerPage = 5;
        int totalCount = 60;

        // when
        Paginator.Pagination pagination = Paginator.paging(currentPage, countPerPage, totalCount);

        // then
        assertThat(pagination.getCurrentPage(), is(12));
        assertThat(pagination.getStartPage(), is(3));
        assertThat(pagination.getEndPage(), is(12));
        assertThat(pagination.getHasPrev(), is(true));
        assertThat(pagination.getHasNext(), is(false));
    }

}
