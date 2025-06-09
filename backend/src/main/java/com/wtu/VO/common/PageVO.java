package com.wtu.VO.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果VO
 * @param <T> 数据类型
 */
@Data
@Schema(description = "分页结果")
public class PageVO<T> {
    
    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private Long pages;
    
    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current;
    
    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Long size;
    
    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records = Collections.emptyList();
    
    /**
     * 创建分页结果
     * @param <T> 数据类型
     * @param total 总记录数
     * @param pages 总页数
     * @param current 当前页码
     * @param size 每页大小
     * @param records 数据列表
     * @return 分页结果
     */
    public static <T> PageVO<T> of(long total, long pages, long current, long size, List<T> records) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        pageVO.setPages(pages);
        pageVO.setCurrent(current);
        pageVO.setSize(size);
        pageVO.setRecords(records != null ? records : Collections.emptyList());
        
        // 计算是否有上一页和下一页
        pageVO.setHasPrevious(current > 1);
        pageVO.setHasNext(current < pages);
        
        return pageVO;
    }
} 