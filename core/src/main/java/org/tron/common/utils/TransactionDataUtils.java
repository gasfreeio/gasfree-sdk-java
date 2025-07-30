package org.tron.common.utils;

import com.google.protobuf.InvalidProtocolBufferException;

import org.tron.common.crypto.Hash;
import org.tron.protos.Protocol;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.protos.contract.SmartContractOuterClass.SmartContract.ABI;
import org.tron.protos.contract.SmartContractOuterClass.SmartContract.ABI.Entry.Param;
import org.tron.walletserver.AddressUtil;
import org.tron.walletserver.TriggerData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TransactionDataUtils {
    public static final String transferMethod = "transfer(address,uint256)";
    public static final String approveMethod = "approve(address,uint256)";
    public static final String increaseApprovalMethod = "increaseApproval(address,uint256)";
    public static final String transferFromMethod = "transferFrom(address,address,uint256)";
    public static final String ApproveSha3 = "095ea7b3";
    public static final String TransferSha3 = "a9059cbb";
    public static final String TransferFromSha3 = "23b872dd";
    public static final String IncreaseApprovalSha3 = "d73dd623";

    private TransactionDataUtils() {

    }

    private static class SingletonHolder {

        private final static TransactionDataUtils instance = new TransactionDataUtils();
    }

    public static TransactionDataUtils getInstance() {
        return SingletonHolder.instance;
    }


    public TriggerData parseData(SmartContractOuterClass.TriggerSmartContract triggerSmartContract, ABI abi) {
        ABI.Entry entry = getEntry(abi,
                getSelector(triggerSmartContract.getData().toByteArray()));
        TriggerData triggerData = new TriggerData();
        triggerData.setMethod(getMethod(entry));
        triggerData.setParameterMap(getParameter(triggerSmartContract.getData().toByteArray(), entry));
        return triggerData;

    }

    public TriggerData parseDataByFun(SmartContractOuterClass.TriggerSmartContract triggerSmartContract, String fun) {

        TriggerData triggerData = new TriggerData();
        triggerData.setMethod(fun);
        triggerData.setParameterMap(getParameterByFun(triggerSmartContract.getData().toByteArray(), fun));
        return triggerData;

    }



    public TriggerData parseConstantDataByFun(byte[] data, String fun) {

        TriggerData triggerData = new TriggerData();
        triggerData.setMethod(fun);
        triggerData.setParameterMap(getConstantParameterByFun(data, fun));
        return triggerData;

    }


    private Map<String, String> getConstantParameterByFun(byte[] data, String fun) {
        Map<String, String> paramsMap = TriggerLoad.parseTriggerDataByFun(data, fun);
        return paramsMap;
    }

    public  Map<String, String> getParameterByFun(byte[] data, String fun) {
        Map<String, String> paramsMap = new HashMap<>();
        if (AddressUtil.isEmpty(fun) || data.length < 4) {
            return paramsMap;
        }
        if (!checkFunValid(fun, data)) return paramsMap;

        byte[] paramData = Arrays.copyOfRange(data, 4, data.length);
        paramsMap = TriggerLoad.parseTriggerDataByFun(paramData, fun);
        return paramsMap;
    }

    private Map<String, String> getParameter(byte[] data, ABI.Entry entry) {
        Map<String, String> paramsMap = new HashMap<>();
        if (entry == null || data.length < 4) {
            return paramsMap;
        }
        byte[] paramData = Arrays.copyOfRange(data, 4, data.length);
        paramsMap = TriggerLoad.parseTriggerData(paramData, entry);
        return paramsMap;
    }


    private String getMethod(ABI.Entry entry) {
        if (entry == null) {
            return "()";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(entry.getName()).append("(");
        StringBuilder sbp = new StringBuilder();
        for (Param param : entry.getInputsList()) {
            if (sbp.length() > 0) {
                sbp.append(",");
            }
            sbp.append(param.getType());
        }
        sb.append(sbp.toString()).append(")");
        return sb.toString();
    }

    private ABI.Entry getEntry(ABI abi, byte[] selector) {
        if (abi == null || selector == null || selector.length != 4
                || abi.getEntrysList().size() == 0) {
            return null;
        }

        for (ABI.Entry entry : abi.getEntrysList()) {
            if (entry.getType() != ABI.Entry.EntryType.Function) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getName()).append("(");
            StringBuilder sbp = new StringBuilder();
            for (Param param : entry.getInputsList()) {
                if (sbp.length() > 0) {
                    sbp.append(",");
                }
                sbp.append(param.getType());
            }
            sb.append(sbp.toString()).append(")");
            byte[] funcSelector = new byte[4];
            System.arraycopy(Hash.sha3(sb.toString().getBytes()), 0, funcSelector, 0, 4);
            if (Arrays.equals(funcSelector, selector)) {
                return entry;
            }
        }
        return null;
    }

    public byte[] getSelector(byte[] data) {
        if (data == null || data.length < 4) {
            return null;
        }

        byte[] ret = new byte[4];
        System.arraycopy(data, 0, ret, 0, 4);
        return ret;
    }

    public TriggerData getTransferData(SmartContractOuterClass.TriggerSmartContract triggerSmartContract) throws JsonFormat.ParseException {
        return parseDataByFun(triggerSmartContract, "transfer(address,uint256)");
    }

    public TriggerData getTransferNFTData(SmartContractOuterClass.TriggerSmartContract triggerSmartContract) throws JsonFormat.ParseException {
        return parseDataByFun(triggerSmartContract, "transferFrom(address,address,uint256)");
    }

    public boolean checkFunValid(String fun, SmartContractOuterClass.TriggerSmartContract triggerSmartContract) {
        boolean valid = false;
        if (AddressUtil.isEmpty(fun) || triggerSmartContract == null) return valid;
        return checkFunValid(fun, triggerSmartContract.getData().toByteArray());
    }

    public boolean checkFunValid(String fun, byte[] data) {
        boolean valid = false;
        byte[] selector = getSelector(data);
        byte[] funcSelector = new byte[4];
        System.arraycopy(Hash.sha3(fun.getBytes()), 0, funcSelector, 0, 4);
        if (Arrays.equals(funcSelector, selector)) {
            valid = true;
        }
        return valid;
    }

    public boolean checkFunValid(String fun, Protocol.Transaction transaction) {
        if (transaction == null || transaction.toString().length() < 1 || transaction.getRawData() == null || transaction.getRawData().getContractCount() < 1)
            return false;

        Protocol.Transaction.Contract contract = transaction.getRawData().getContract(0);
        if (contract.getType() == Protocol.Transaction.Contract.ContractType.TriggerSmartContract) {

            try {
                return checkFunValid(fun, TransactionUtils.unpackContract(contract, SmartContractOuterClass.TriggerSmartContract.class));
            } catch (InvalidProtocolBufferException e) {
                LogUtils.e(e);
                return false;
            }
        } else {
            return true;
        }
    }

    public String getEntryName(ABI abi, byte[] data, int index) {
        try {
            ABI.Entry entry = getEntry(abi, data);
            Param outputs = entry.getInputs(index);
            return outputs.getName();
        } catch (Exception e) {
            LogUtils.e(e);
            //catch exception（empty abi）
            return null;
        }
    }
}
