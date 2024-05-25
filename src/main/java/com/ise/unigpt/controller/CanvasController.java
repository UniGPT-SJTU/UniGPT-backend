package com.ise.unigpt.controller;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import com.ise.unigpt.dto.CanvasEventDTO;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/canvas")
public class CanvasController {
    @GetMapping("/eventlist")
    public String getCanvasEventList() {
        try {

            String url = "https://oc.sjtu.edu.cn/feeds/calendars/user_5ANNdRErwaHFWaUwCJuLqUk2kyoSNRwMGFtN933O.ics";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String icsData = response.body();
            ICalendar ical = Biweekly.parse(icsData).first();

            List<CanvasEventDTO> eventList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            return ical.getEvents().stream()
                    .filter(event -> event.getDateStart() != null)
                    .filter(event -> {
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneOffset.UTC);
                        return endDate.isAfter(now);
                    })
                    .map(event -> {
                        LocalDateTime endDate = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneOffset.UTC);
                        endDate = endDate.plusHours(8);
                        if (endDate.getHour() == 0) {
                            endDate = endDate.plusDays(1);
                        }
                        Instant ddlTime = endDate.toInstant(ZoneOffset.UTC);
                        return new CanvasEventDTO(
                                event.getSummary().getValue(),
                                event.getDescription().getValue(),
                                ddlTime
                        );
                    })
                    .toList().toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
