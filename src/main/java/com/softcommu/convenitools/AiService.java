package com.softcommu.convenitools;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import dev.langchain4j.model.audio.AudioTranscriptionResponse;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiAudioTranscriptionModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Service
public class AiService {

    // 重要：通常、セキュリティの関係で APIキーはコードの中には記載しません。サーバーの環境変数にセットしておくのが正しい方法です。
    private static final String API_KEY = System.getenv("API_KEY_OPEN_AI_CONVENITOOLS");
    private static final String CHAT_MODEL = "gpt-5-nano"; // チャットモデル
    private static final String IMAGE_MODEL = "gpt-image-1-mini"; // 画像生成用モデル
    private static final String AUDIO_TRANSCRIPTION_MODEL = "whisper-1"; // 文字起こし用モデル
    // private static final String TRANSCRPIPT_MODEL = "gpt-audio-mini"; // 文字起こし用モデル

    /**
     * シンプルなチャット
     */
    public String getChatAnswer(String question){
        
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(CHAT_MODEL)
                .build();

        String answer = model.chat(question);

        return answer;
    }

    /**
     * シンプルなチャットで結果を JSON形式で受け取るようにしたもの。
     * 
     * 呼び出し側サンプルコード 
     * String question = "空が青いのはなぜですか？";
     * String jsonText = getChatAnswerByJson(question);		
     * ObjectMapper objectMapper = new ObjectMapper();
     * Answer answer = objectMapper.readValue(jsonText, Answer.class);
     */
    public String getChatAnswerByJson(String question){

        System.out.println("API_KEY = " + API_KEY);

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(CHAT_MODEL)
                .responseFormat("json_schema")
                .strictJsonSchema(true)
                .build();

        String json = model.chat(question);

        return json;
    }

    /**
     * シンプルなチャットで結果を音声データBase64形式で受け取るようにしたもの。
     */
    public String getChatAnswerByAudioBase64(String question) {

        try {

            HttpClient client = HttpClient.newHttpClient();

            String requestBody = """
            {
            "model": "gpt-audio-mini",
            "modalities": ["text", "audio"],
            "audio": {
                "voice": "alloy",
                "format": "wav"
            },
            "messages": [
                {
                "role": "user",
                "content": "%s"
                }
            ]
            }
            """.formatted(question.replace("\"", "\\\""));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());            
            String base64 = root.get("choices").get(0).get("message").get("audio").get("data").toString();
            base64 = base64.substring(1, base64.length()-2); // 先頭と最後尾にダブルクォーテーションがあるので取り除く
            return base64;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * OpenAI の REST APIを使って音声合成
     */
    public byte[] getAudioBytes(String message) {

        try {
            // message（AIの返答）に " や改行が含まれても壊れないよう、JSONはJacksonで安全に組み立てる
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(java.util.Map.of(
                    "model", "tts-1",
                    "voice", "nova",
                    "input", message));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            // ステータスを確認し、失敗時はエラー本文を音声として返さない
            if (response.statusCode() != 200) {
                System.err.println("TTS失敗 status=" + response.statusCode()
                        + " body=" + new String(response.body(), StandardCharsets.UTF_8));
                return null;
            }

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * OpenAI Whisper を使って音声データ（byte[]）を文字起こしする。
     * Vaadin の AudioRecorder アドオンから渡される録音バイト列をそのまま渡せる。
     * とりあえず、録音の MIME タイプは "audio/webm"専用。
     */
    public String getTranscription(byte[] audio) {

        if(audio==null || audio.length<=0){
            return null;
        }

        OpenAiAudioTranscriptionModel model = OpenAiAudioTranscriptionModel.builder()
                .apiKey(API_KEY)
                .modelName(AUDIO_TRANSCRIPTION_MODEL)
                .build();

        Audio audioData = Audio.builder().binaryData(audio).mimeType("audio/webm").build();

        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder()
                .audio(audioData)
                .build();

        AudioTranscriptionResponse response = model.transcribe(request);

        return response.text();
    }

    /**
     * チャットの返答をストリーミングで受け取る
     */
    public void streamingChat(String question, Consumer<String> partialResponseListener, Consumer<String> completeListener){

        var model = OpenAiStreamingChatModel.builder()
            .apiKey(API_KEY)
            .modelName("gpt-4o-mini") 
            .build();

        var handler = new StreamingChatResponseHandler() {
                    
            @Override
            public void onPartialResponse(String partialResponse) {
                partialResponseListener.accept(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                completeListener.accept(completeResponse.aiMessage().text());
            }

            @Override
            public void onError(Throwable error) {
                System.out.println("188:LLMエラー:" + error.getMessage());
            }
        };

        model.chat(question, handler);
    }

    /**
     * 会話履歴を記録しながらAIと会話するための ConversationalChain を生成する。
     * 使用方法のサンプル
     * 
     *  AiService ai = new AiService()
     *  ConversationalChain conversationalChain = ai.getConversationalChain("あなたはとってもポジティブなアシスタントです。いっぱい褒めてください。");
     *
     * String answer1 = conversationalChain.execute("私の名前は Eiichi と言います。");
     * String answer2 = conversationalChain.execute("新人研修に参加中です。");
     * String answer3 = conversationalChain.execute("Javaを勉強しました。");
     * String answer4 = conversationalChain.execute("LLMも使いこなせます。");
     */
    public ConversationalChain getConversationalChain(String systemMessage){

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(CHAT_MODEL)
                // 廃止メソッド .temperature(0.0)
                // 廃止メソッド .maxTokens(1000)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryStore(new InMemoryChatMemoryStore()) // チャット履歴をメモリ上に保管しておく場所。
                .maxMessages(5) // Context Window は5つの会話のみ。この数にはシステムメッセージを含む。
                .build();

        chatMemory.add(new SystemMessage(systemMessage));

        ConversationalChain conversationalChain = ConversationalChain.builder()
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();

        return conversationalChain;
    }

    /**
     * 画像生成
     */
    public String getImage(String description){

        OpenAiImageModel model = OpenAiImageModel.builder()
                .apiKey(API_KEY)
                .modelName(IMAGE_MODEL)
                // .size("1024x1024") // 解像度指定
                .size("1536x1024") // 解像度指定
                .quality("low") // API使用料の節約のため、低品質に設定。
                .build();

        Response<Image> response = model.generate(description);

        if(response.content()!=null)
        {
            if(response.content().url()!=null) 
            {
                String url = response.content().url().toString();
                System.out.println("画像のURL：" + response.content().url());
                return url;
            }

            if(response.content().base64Data()!=null)
            {
                String base64Data = response.content().base64Data().toString();                 
                System.out.println("画像のBASE64：" + base64Data);
                return base64Data;
            }
        
        }

        return null;
    }
}
