package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.service.KnowledgeService;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.NoSuchElementException;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


// pgvector
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
@Service
public class KnowledgeServiceImpl implements KnowledgeService{

    static int maxSegmentSizeInChar = 300, dimension = 384;
    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final PromptChatRepository promptChatRepository;
    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    public KnowledgeServiceImpl(BotRepository botRepository,
                          UserRepository userRepository,
                          HistoryRepository historyRepository,
                          PromptChatRepository promptChatRepository,
                          AuthService authService,
                          ChatHistoryService chatHistoryService) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
        this.promptChatRepository = promptChatRepository;
        this.authService = authService;
        this.historyRepository = historyRepository;
        this.chatHistoryService = chatHistoryService;
    }

    public String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (fileName != null && fileName.toLowerCase().endsWith(".txt")) {
            return extractTextFromTxt(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private String extractTextFromTxt(MultipartFile file) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(System.lineSeparator());
            }
        }
        return textBuilder.toString();
    }

    public ResponseDTO uploadFile(Integer id, String token, MultipartFile file) throws AuthenticationException {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) { throw new NoSuchElementException("User not found");}

        if(!bot.getCreator().equals(user) && !user.getAsAdmin()){
            throw new AuthenticationException("Unauthorized to upload file.");
        }

        try{
            Document document = new Document(extractText(file));
            DocumentSplitter splitter = DocumentSplitters.recursive(
                    maxSegmentSizeInChar,
                    0
            );
            List<TextSegment> textSegmentList = splitter.split(document);

            EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                    .host("localhost")
                    .port(5432)
                    .database("rag")
                    .user("bleaves")
                    .password("bleaves")
                    .table("bot" + id)
                    .dimension(dimension)
                    .build();

            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            for(TextSegment segment: textSegmentList)
                embeddingStore.add(
                        embeddingModel.embed(segment).content(),
                        segment
                );
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

        return new ResponseDTO(true, "Successfully upload " + file.getOriginalFilename());
    }
}
