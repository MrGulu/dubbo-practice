package cn.tang.enumbean;

import cn.tang.exception.IBussinessError;

/**
 * @author tangwenlong
 */
public enum RspCodeEnum implements IBussinessError {

    /*
     * 返回错误信息
     * */
    //系统公共异常
    ERR99999("系统异常", "99999"),
    ERR99998("必传参数为空,或参数不符合规范！", "99998"),
    ERR99997("操作数据库异常！", "99996"),
    ERR99996("接口发送失败！", "99995"),
    ERR99995("未查到数据！", "99994"),
    ERR99994("更新数据库失败,请重试！", "99993"),
    ERR99993("发送http请求失败！", "99992"),
    ERR99982("未查到有效数据！", "99984");


    private String message;
    private String code;

    private RspCodeEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    // 根据code获取message
    public static String rtnMessage(String code) {
        for (RspCodeEnum ce : RspCodeEnum.values()) {
            if (ce.getCode().equals(code)) {
                return ce.getMessage();
            }
        }
        return null;
    }

    @Override
    public String getMessage() {
        return this.message;
    }


    @Override
    public String getCode() {
        return this.code;
    }


    @Override
    public String getMessage(String... details) {
        String detailsMessage = this.message;

        try {
            detailsMessage = String.format(this.message, details);
        } catch (Exception e) {

        }
        return detailsMessage;
    }

}
