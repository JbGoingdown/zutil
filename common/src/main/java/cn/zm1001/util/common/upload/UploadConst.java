package cn.zm1001.util.common.upload;

/**
 * @author Dongd_Zhou
 * @desc 上传常量
 */
public interface UploadConst {
    /**
     * 接口中指定的图片分类类型，对应不同的处理方式
     */
    enum HandlerImgType {
        /**
         * 图片通用类型，原样上传
         */
        COMMON,
        /**
         * 图片上传后会生成大中小三张图
         */
        IMG3,
        /**
         * 用户头像
         */
        USER_HEAD;
    }

    /**
     * 接口中指定的媒体文件分类类型，对应不同的处理方式
     */
    enum HandlerMediaType {
        /**
         * 音频文件按
         */
        AUDIO;
    }
}
