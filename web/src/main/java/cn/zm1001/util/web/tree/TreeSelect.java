package cn.zm1001.util.web.tree;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Desc TreeSelect树结构实体类
 * @Author Dongd_Zhou
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TreeSelect implements Serializable {
    private static final long serialVersionUID = -1306858564634524211L;

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;
}
