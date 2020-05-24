package il.whitomtit.edudiary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    Stack<String> easterEgg;
    ImageView icon;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        icon = findViewById(R.id.aboud_icon);
        text = findViewById(R.id.aboud_text);

        easterEgg = new Stack<>();
        easterEgg.push("\u004f\u006b\u0061\u0079\u002c \u0079\u006f\u0075 \u0066\u006f\u0075\u006e\u0064 \u0074\u0068\u0065 \u0074\u0072\u0075\u0074\u0068\u002e \u0041\u0072\u0065 \u0079\u006f\u0075 \u0073\u0061\u0074\u0069\u0073\u0066\u0069\u0065\u0064\u003f");
        easterEgg.push("\u0050\u006c\u0065\u0065\u0065\u0065\u0061\u0073\u0065 \u0054\u005f\u0054");
        easterEgg.push("\u0050\u006c\u0065\u0061\u0073\u0065\u002c \u0073\u0074\u006f\u0070\u0021");
        easterEgg.push("\u0059\u006f\u0075 \u0064\u0065\u0066\u0069\u006e\u0069\u0074\u0065\u006c\u0079 \u0073\u0068\u006f\u0075\u006c\u0064 \u0066\u006f\u0075\u006e\u0064 \u0073\u006f\u006d\u0065\u0074\u0068\u0069\u006e\u0067 \u0065\u006c\u0073\u0065 \u0074\u006f \u0064\u006f");
        easterEgg.push("\u004c\u006f\u006f\u006b \u0061\u0074 \u006d\u0065\u002c \u0049\u0027\u006d \u0074\u0061\u0070\u0069\u006e\u0067 \u006f\u006e \u0074\u0068\u0065 \u0069\u0063\u006f\u006e\u0021");
        easterEgg.push("\u0059\u006f\u0075 \u0061\u0072\u0065 \u006a\u0075\u0073\u0074 \u0077\u0061\u0073\u0074\u0069\u006e\u0067 \u0079\u006f\u0075\u0072 \u0074\u0069\u006d\u0065\u002e\u002e\u002e");
        easterEgg.push("\u0054\u0068\u0065\u0072\u0065 \u0069\u0073 \u006e\u006f\u0074\u0068\u0069\u006e\u0067 \u0069\u006e\u0074\u0065\u0072\u0065\u0073\u0074\u0069\u006e\u0067 \u0068\u0065\u0072\u0065");
        easterEgg.push("\u0057\u0068\u0079 \u0064\u0069\u0064 \u0079\u006f\u0075 \u0063\u006f\u006d\u0065 \u0068\u0065\u0072\u0065\u003f");

        icon.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == icon) {
            if (!easterEgg.isEmpty()) {
                Toast.makeText(this, easterEgg.pop(), Toast.LENGTH_SHORT).show();
                if (easterEgg.isEmpty())
                    text.append("\u0026\u006d\u0061\u0072\u006b\u0073\u0074\u0061\u006c\u006b\u0065\u0072");
            }
        }
    }
}
