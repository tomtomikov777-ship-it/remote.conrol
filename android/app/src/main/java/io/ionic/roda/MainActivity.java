package io.ionic.roda;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.hardware.ConsumerIrManager;
import android.content.Context;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private ConsumerIrManager irManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем доступ к ИК-порту
        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);
        if (irManager == null || !irManager.hasIrEmitter()) {
            Toast.makeText(this, "ИК-порт не найден", Toast.LENGTH_LONG).show();
        }

        // Добавляем мост для JavaScript
        WebView webView = (WebView) findViewById(android.R.id.content);
        webView.addJavascriptInterface(new IrBridge(), "irBridge");
    }

    // Класс-мост
    private class IrBridge {
        @android.webkit.JavascriptInterface
        public void sendIR(int frequency, int[] pattern) {
            if (irManager != null && irManager.hasIrEmitter()) {
                irManager.transmit(frequency, pattern);
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "ИК-передача недоступна", Toast.LENGTH_SHORT).show());
            }
        }
    }
}
