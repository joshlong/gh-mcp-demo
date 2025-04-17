package com.example.client;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
 /*

    @Bean
    McpSyncClient mcpSyncClient() {
        var mcp = McpClient
                .sync(new StdioClientTransport(ServerParameters
                        .builder("/Users/jlong/Desktop/github-mcp-server/cmd/github-mcp-server/github-mcp-server")
                        .args("stdio")
                        .addEnvVar("GITHUB_PERSONAL_ACCESS_TOKEN", System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN"))
                        .build()))
                .build();
        mcp.initialize();
        return mcp;
    }
*/
    @Bean
    ChatClient chatClient(ChatClient.Builder builder , List<McpSyncClient> syncClientList) {
        return builder
                .defaultTools(new SyncMcpToolCallbackProvider(syncClientList))
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(new InMemoryChatMemory()).build())
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> scheduler(ChatClient ai) {
        return route()
                .GET("/assistant", request -> {
                    var question = request.param("question")
                            .orElseThrow(() -> new IllegalArgumentException("question is required"));
                    var reply = ai
                            .prompt()
                            .user(question)
                            .call()
                            .content();
                    return ok().body(Map.of("reply", reply));
                })
                .build();
    }
}

