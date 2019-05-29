package cn.tang.base.redis.bean;

import cn.tang.base.bean.Appl;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: 业务鉴权缓存
 **/
public class ApplAuthCache implements Serializable {

    private static final long serialVersionUID = 5264748230486186034L;

    /**
     * 业务流水号
     */
    private String applSeq;

    /**
     * 放款店代码
     */
    private String cooprCde;

    /**
     * 金融专员代码
     */
    private String operatorCde;

    /**
     * 销售员手机号
     */
    private String salerMobile;

    /**
     * 外部状态
     */
    private String outSts;

    /**
     * 管理机构
     */
    private String bchCde;

    /**
     * 申请操作人cde
     */
    private String applOprCde;

    public String getApplOprCde() {
        return applOprCde;
    }

    public void setApplOprCde(String applOprCde) {
        this.applOprCde = applOprCde;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getCooprCde() {
        return cooprCde;
    }

    public void setCooprCde(String cooprCde) {
        this.cooprCde = cooprCde;
    }

    public String getOperatorCde() {
        return operatorCde;
    }

    public void setOperatorCde(String operatorCde) {
        this.operatorCde = operatorCde;
    }

    public String getSalerMobile() {
        return salerMobile;
    }

    public void setSalerMobile(String salerMobile) {
        this.salerMobile = salerMobile;
    }

    public String getOutSts() {
        return outSts;
    }

    public void setOutSts(String outSts) {
        this.outSts = outSts;
    }

    public String getBchCde() {
        return bchCde;
    }

    public void setBchCde(String bchCde) {
        this.bchCde = bchCde;
    }

    @Override
    public String toString() {
        return "ApplAuthCache{" +
                "applSeq='" + applSeq + '\'' +
                ", cooprCde='" + cooprCde + '\'' +
                ", operatorCde='" + operatorCde + '\'' +
                ", salerMobile='" + salerMobile + '\'' +
                ", outSts='" + outSts + '\'' +
                ", bchCde='" + bchCde + '\'' +
                ", applOprCde='" + applOprCde + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appl)) {
            return false;
        }
        Appl that = (Appl) o;
        return Objects.equals(getApplSeq(), that.getApplSeq().toString()) &&
                Objects.equals(getCooprCde(), that.getCooprCde()) &&
                Objects.equals(getOperatorCde(), that.getOperatorCde()) &&
                Objects.equals(getSalerMobile(), that.getSalerMobile()) &&
                Objects.equals(getOutSts(), that.getOutSts()) &&
                Objects.equals(getBchCde(), that.getBchCde());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getApplSeq(), getCooprCde(), getOperatorCde(), getSalerMobile(), getOutSts(), getBchCde(), getApplOprCde());
    }
}

