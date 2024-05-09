package com.ise.unigpt.utils;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页逻辑，更好地复用代码
 */
public class PaginationUtils {
    /**
     * 分页逻辑
     * @param <T> 泛型
     * @param data 数据
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页后的子列表
     */
    public static <T> List<T> paginate(List<T> data, int page, int pageSize) {
        int start = page * pageSize;
        int end = Math.min(start + pageSize, data.size());
        return start < end ? data.subList(start, end) : new ArrayList<>();
    }
}