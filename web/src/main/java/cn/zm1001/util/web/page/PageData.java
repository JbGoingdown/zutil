package cn.zm1001.util.web.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Desc 返回给前端的分页数据
 * @Author Dongd_Zhou
 */
@Data
@NoArgsConstructor
public class PageData<T> implements Serializable {
    private static final long serialVersionUID = 7950943551093754654L;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;
}
