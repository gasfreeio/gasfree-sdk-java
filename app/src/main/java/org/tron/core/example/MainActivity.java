package org.tron.core.example;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;

import org.bouncycastle.util.encoders.Hex;
import org.tron.core.example.R;
import org.tron.api.GrpcAPI;
import org.tron.api.WalletGrpc;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.ByteUtil;
import org.tron.common.utils.LogUtils;
import org.tron.common.utils.TransactionUtils;
import org.tron.common.utils.abi.AbiUtil;
import org.tron.config.Parameter;
import org.tron.net.input.TriggerContractRequest;
import org.tron.protos.Protocol;
import org.tron.protos.contract.AssetIssueContractOuterClass;
import org.tron.protos.contract.BalanceContract;
import org.tron.protos.contract.SmartContractOuterClass;
import org.tron.walletserver.AddressUtil;
import org.tron.walletserver.ConnectErrorException;
import org.tron.walletserver.I_TYPE;
import org.tron.walletserver.Wallet;
import org.tron.walletserver.WalletPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}