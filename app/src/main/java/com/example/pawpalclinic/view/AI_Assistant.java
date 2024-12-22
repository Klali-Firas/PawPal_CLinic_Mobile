package com.example.pawpalclinic.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.service.AIService;

import io.noties.markwon.Markwon;

public class AI_Assistant extends AppCompatActivity {

    private AIService aiService;
    private LinearLayout messagesContainer;
    private EditText userPrompt;
    private Button sendButton;
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_asistant);

        aiService = new AIService(this);
        messagesContainer = findViewById(R.id.messages);
        userPrompt = findViewById(R.id.user_prompt);
        sendButton = findViewById(R.id.send_button);
        markwon = Markwon.create(this);

        sendButton.setOnClickListener(v -> sendPrompt());
    }

    private void sendPrompt() {
        String prompt = userPrompt.getText().toString().trim();
        if (prompt.isEmpty()) return;

        addMessage(prompt, true);
        userPrompt.setText("");

        aiService.generateAIResponseForProprietaire(prompt).thenAccept(response -> {
            try {
                String aiResponse = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                runOnUiThread(() -> addMarkdownMessage(aiResponse, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void addMessage(String text, boolean isUser) {
        TextView messageView = new TextView(this);
        messageView.setText(text);
        messageView.setBackgroundResource(isUser ? R.drawable.user_message_bg : R.drawable.ai_message_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 24); // Add bottom margin
        params.gravity = isUser ? Gravity.END : Gravity.START;
        messageView.setLayoutParams(params);
        messageView.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75));

        // Set padding directly on the TextView
        int padding = (int) (12 * getResources().getDisplayMetrics().density);
        messageView.setPadding(padding, padding, padding, padding);
        messageView.setTextColor(getResources().getColor(R.color.md_theme_onPrimaryContainer, getTheme()));

        messagesContainer.addView(messageView);

        ScrollView scrollView = findViewById(R.id.messages_container);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void addMarkdownMessage(String markdown, boolean isUser) {
        TextView messageView = new TextView(this);
        markwon.setMarkdown(messageView, markdown);
        messageView.setBackgroundResource(isUser ? R.drawable.user_message_bg : R.drawable.ai_message_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 24); // Add bottom margin
        params.gravity = isUser ? Gravity.END : Gravity.START;
        messageView.setLayoutParams(params);
        messageView.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75));
        messageView.setTextColor(getResources().getColor(R.color.md_theme_onSecondaryContainer, getTheme()));

        // Set padding directly on the TextView
        int padding = (int) (12 * getResources().getDisplayMetrics().density);
        messageView.setPadding(padding, padding, padding, padding);

        messagesContainer.addView(messageView);

        ScrollView scrollView = findViewById(R.id.messages_container);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}