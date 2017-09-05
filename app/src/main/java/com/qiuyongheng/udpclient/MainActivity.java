package com.qiuyongheng.udpclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.et_send)
    EditText etSend;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.tv_receive)
    TextView tvReceive;

    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initListener();

    }

    private void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = etIp.getText().toString().trim();
                String port = etPort.getText().toString().trim();
                String sendData = etSend.getText().toString().trim();

                if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port) || TextUtils.isEmpty(sendData)) {
                    Toast.makeText(MainActivity.this, "傻逼, 你还有数据没填写", Toast.LENGTH_SHORT).show();
                    return;
                }

                UDPReceive(ip, port);
                UDPSend(ip, port, sendData);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuffer = new StringBuffer();
            }
        });
    }

    private void UDPReceive(String ip, final String port) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 创建socket对象
                    DatagramSocket receive = new DatagramSocket(Integer.valueOf(port));

                    // 创建一个数据包, 接受数据
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

                    while (true) {
                        // 接受数据
                        receive.receive(packet);

                        byte[] data = packet.getData();
                        int length = packet.getLength();
                        String hostAddress = packet.getAddress().getHostAddress();
                        String s = String.format("%s--->%s", hostAddress, new String(data, 0, length));
                        stringBuffer.append(s + "\n");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvReceive.setText(stringBuffer.toString());
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送
     *
     * @param ip
     * @param port
     * @param sendData
     */
    private void UDPSend(final String ip, final String port, final String sendData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建socket对象
                    DatagramSocket send = new DatagramSocket();

                    // 把发射的数据封装成数据包
                    byte[] buf = sendData.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), Integer.valueOf(port));

                    // 发送数据包
                    send.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
