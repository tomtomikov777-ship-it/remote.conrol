package io.ionic.roda;

import android.os.Bundle;
import android.webkit.WebView;
import android.hardware.ConsumerIrManager;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private ConsumerIrManager irManager;
    private static final String TAG = "RODA_IR";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
            if (irManager == null || !irManager.hasIrEmitter()) {
                Toast.makeText(this, "ИК-порт не найден", Toast.LENGTH_LONG).show();
                Log.w(TAG, "IR emitter not available");
            } else {
                Log.i(TAG, "IR emitter found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking IR: " + e.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart(); // обязательно вызываем родительский метод
        try {
            WebView webView = getBridge().getWebView();
            if (webView != null) {
                webView.addJavascriptInterface(new IrBridge(), "irBridge");
                Log.i(TAG, "IR bridge registered successfully");
            } else {
                Log.e(TAG, "WebView is null");
                Toast.makeText(this, "Ошибка: WebView не инициализирован", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error registering bridge: " + e.getMessage());
            Toast.makeText(this, "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class IrBridge {
        @android.webkit.JavascriptInterface
        public void sendIR(int frequency, int[] pattern) {
            if (irManager != null && irManager.hasIrEmitter()) {
                try {
                    irManager.transmit(frequency, pattern);
                    Log.i(TAG, "IR signal sent");
                } catch (Exception e) {
                    Log.e(TAG, "IR transmit error: " + e.getMessage());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "ИК-передача недоступна", Toast.LENGTH_SHORT).show());
            }
        }
    }
}
