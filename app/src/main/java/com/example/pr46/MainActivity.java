package com.example.pr46;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextUserId;
    private TextView textViewOutput;
    private AppDatabase db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextFirstName = findViewById(R.id.editTextText);
        editTextLastName = findViewById(R.id.editTextText2);
        //editTextUserId = findViewById(R.id.editTextUserId); // Убедитесь, что этот EditText существует в вашем XML
        textViewOutput = findViewById(R.id.textView1);
        Button buttonInsert = findViewById(R.id.button1);
        Button buttonDelete = findViewById(R.id.button5);
        Button buttonFetch = findViewById(R.id.button5); // Убедитесь, что ID правильный
        Button buttonDeleteLast = findViewById(R.id.button5); // Новая кнопка для удаления последнего пользователя

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                User user = new User(firstName, lastName);

                executorService.execute(() -> db.userDao().insert(user));
                editTextFirstName.setText("");
                editTextLastName.setText("");
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIdString = editTextUserId.getText().toString();
                if (!userIdString.isEmpty()) {
                    int userId = Integer.parseInt(userIdString);
                    executorService.execute(() -> db.userDao().deleteById(userId));
                    editTextUserId.setText("");
                }
            }
        });

        // Логика для удаления последнего пользователя
        buttonDeleteLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executorService.execute(() -> {                     List<User> users = db.userDao().getAllUsers();
                    if (!users.isEmpty()) {
                        // Получаем ID последнего пользователя
                        int lastUserId = users.get(users.size() - 1).id;
                        db.userDao().deleteById(lastUserId);
                    }
                });
            }
        });

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executorService.execute(() -> {
                    List<User> users = db.userDao().getAllUsers();
                    StringBuilder output = new StringBuilder();
                    for (User user : users) {
                        output.append(user.id).append(": ").append(user.firstName).append(" ").append(user.lastName).append("\n");
                    }
                    runOnUiThread(() -> textViewOutput.setText(output.toString()));
                });
            }
        });
    }
}

