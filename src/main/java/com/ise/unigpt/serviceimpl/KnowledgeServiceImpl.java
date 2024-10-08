package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.config.BasicConfig;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.service.AuthService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// pgvector
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    static int maxSegmentSizeInChar = 300, dimension = 384;
    private final BotRepository botRepository;
    private final AuthService authService;
    private final EmbeddingModel embeddingModel;

    private final BasicConfig basicConfig;

    public KnowledgeServiceImpl(BotRepository botRepository,
            AuthService authService,
            BasicConfig basicConfig) {
        this.botRepository = botRepository;
        this.authService = authService;
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.basicConfig = basicConfig;
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


    @Override
    public EmbeddingStore<TextSegment> createEmbeddingStore(Integer botId) {
        return PgVectorEmbeddingStore.builder()
                .host(basicConfig.POSTGRES_HOST)
                .port(basicConfig.POSTGRES_PORT)
                .database(basicConfig.POSTGRES_DB)
                .user(basicConfig.POSTGRES_USERNAME)
                .password(basicConfig.POSTGRES_PASSWORD)
                .table("bot" + botId)
                .dimension(dimension)
                .build();
    }

    public ResponseDTO uploadFile(Integer id, String token, MultipartFile file) throws AuthenticationException {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found");
        }

        if (!user.getAsAdmin() && !bot.getCreator().equals(user)) {
            throw new AuthenticationException("Unauthorized to upload file.");
        }

        try {
            Document document = new Document(extractText(file));
            DocumentSplitter splitter = DocumentSplitters.recursive(
                    maxSegmentSizeInChar,
                    0);
            List<TextSegment> textSegmentList = splitter.split(document);

            EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore(id);
            for (TextSegment segment : textSegmentList)
                embeddingStore.add(
                        embeddingModel.embed(segment).content(),
                        segment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }

        return new ResponseDTO(true, "Successfully upload " + file.getOriginalFilename());
    }

    // public List<String> queryKnowledge(Integer id, String queryText, Integer maxResults) {
    //     EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore(id);

    //     Embedding queryEmb = embeddingModel.embed(TextSegment.from(queryText)).content();
    //     List<EmbeddingMatch<TextSegment>> res = embeddingStore.findRelevant(queryEmb, maxResults);
    //     List<String> resString = new ArrayList<>();
    //     for (EmbeddingMatch<TextSegment> match : res) {
    //         resString.add(match.embedded().text());
    //     }
    //     return resString;
    // }

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
}
