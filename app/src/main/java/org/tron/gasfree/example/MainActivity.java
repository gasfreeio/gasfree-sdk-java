package org.tron.gasfree.example;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.tron.gasfree.sdk.Constant;
import org.tron.gasfree.sdk.GasFreeGenerator;

public class MainActivity extends AppCompatActivity {

    private String userAddress = "TLFXfejEMgivFDR2x8qBpukMXd56spmFhz";
    private TextView tvGasFreeAddress;
    private EditText etUserAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvBeanconAddress = findViewById(R.id.tv_beancon_address);
        tvBeanconAddress.setText(Constant.beaconAddressRelease);
        TextView tvCreationCodestr = findViewById(R.id.tv_creation_codestr);
        tvCreationCodestr.setText(Constant.creationCodeStrRelease);
        TextView tvGasFreeContrallAddress = findViewById(R.id.tv_gas_free_contrall_address);
        tvGasFreeContrallAddress.setText(Constant.GasFreeControllerAddressRelease);
        Button btnGenerateGasFreeAddress = findViewById(R.id.btn_generate_gas_free_address);
        tvGasFreeAddress = findViewById(R.id.tv_gas_free_address);
        etUserAddress = findViewById(R.id.et_user_address);
        etUserAddress.setText(userAddress);
        etUserAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userAddress = s.toString();
            }
        });
        btnGenerateGasFreeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gasFreeAddress = GasFreeGenerator.generateGasFreeAddress(userAddress,
                        Constant.beaconAddressRelease, Constant.GasFreeControllerAddressRelease,
                        Constant.creationCodeStrRelease);
                if (gasFreeAddress.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a valid TRON address.", Toast.LENGTH_LONG).show();
                    return;
                }
                tvGasFreeAddress.setText(gasFreeAddress);
            }
        });

    }
}