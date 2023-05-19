package Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wewallhere.R;


public class ToastHelper {

    public static void showLongToast(Context context, String message, int length) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_custom, null);
        TextView toastText = toastView.findViewById(R.id.toast_text);
        toastText.setText(message);
        toast.setView(toastView);
        toast.setDuration(length);
        toast.show();
    }

}
