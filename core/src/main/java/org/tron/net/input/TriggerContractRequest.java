package org.tron.net.input;


import org.tron.config.Parameter;

public class TriggerContractRequest {
    private String contractAddrStr;
    private String methodStr;
    private String argsStr;
    private boolean isHex;
    private long feeLimit;
    private long callValue;
    private long tokenCallValue;
    private String tokenId;

    public String getMethodABI() {
        return methodABI;
    }

    public void setMethodABI(String methodABI) {
        this.methodABI = methodABI;
    }

    private byte[] ower;

    private boolean abiPro;//true : use FunctionEncoder;false :AbiUtil

    private String methodABI;//abiPro:true : use methodABI


    public byte[] getOwer() {
        return ower;
    }

    public void setOwer(byte[] ower) {
        this.ower = ower;
    }

    public boolean isAbiPro() {
        return abiPro;
    }

    public void setAbiPro(boolean abiPro) {
        this.abiPro = abiPro;
    }

    public String getContractAddrStr() {
        return contractAddrStr;
    }

    public void setContractAddrStr(String contractAddrStr) {
        this.contractAddrStr = contractAddrStr;
    }

    public String getMethodStr() {
        return methodStr;
    }

    public void setMethodStr(String methodStr) {
        this.methodStr = methodStr;
    }

    public String getArgsStr() {
        return argsStr;
    }

    public void setArgsStr(String argsStr) {
        this.argsStr = argsStr;
    }

    public boolean isHex() {
        return isHex;
    }

    public void setHex(boolean hex) {
        isHex = hex;
    }

    public long getFeeLimit() {
        return feeLimit;
    }

    public void setFeeLimit(long feeLimit) {
        this.feeLimit = feeLimit;
    }

    public long getCallValue() {
        return callValue;
    }

    public void setCallValue(long callValue) {
        this.callValue = callValue;
    }

    public long getTokenCallValue() {
        return tokenCallValue;
    }

    public void setTokenCallValue(long tokenCallValue) {
        this.tokenCallValue = tokenCallValue;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public TriggerContractRequest() {
        this.contractAddrStr = null;
        this.methodStr = null;
        this.argsStr = null;
        this.isHex = false;
        this.feeLimit = Parameter.ResConstant.feeLimit;
        this.callValue = 0;
        this.tokenCallValue = 0;
        this.tokenId = null;
    }

}
