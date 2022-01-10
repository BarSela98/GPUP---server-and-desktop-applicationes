package component.chat;


import component.chat.model.ChatLinesWithVersion;
import error.errorMain;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.GSON_INSTANCE;


public class ChatAreaRefresher extends TimerTask {

    private final Consumer<ChatLinesWithVersion> chatlinesConsumer;
    private final IntegerProperty chatVersion;
    private final BooleanProperty shouldUpdate;
    private int requestNumber;

    public ChatAreaRefresher(IntegerProperty chatVersion, BooleanProperty shouldUpdate, Consumer<ChatLinesWithVersion> chatlinesConsumer) {

        this.chatlinesConsumer = chatlinesConsumer;
        this.chatVersion = chatVersion;
        this.shouldUpdate = shouldUpdate;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.CHAT_LINES_LIST)
                .newBuilder()
                .addQueryParameter("chatversion", String.valueOf(chatVersion.get()))
                .build()
                .toString();



        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> new errorMain(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> new errorMain(new Exception("Chat - Response code: "+response.code()+"\nResponse body: "+responseBody)));
                }
                else{
                    String rawBody = response.body().string();

                    ChatLinesWithVersion chatLinesWithVersion = GSON_INSTANCE.fromJson(rawBody, ChatLinesWithVersion.class);
                    chatlinesConsumer.accept(chatLinesWithVersion);
                }
            }
        });

    }
}
