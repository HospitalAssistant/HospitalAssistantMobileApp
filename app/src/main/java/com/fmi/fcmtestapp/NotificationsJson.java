package com.fmi.fcmtestapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.assistant.model.NotificationDto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationsJson {

    @JsonProperty("_embedded")
    public NotificationsEmbeddedList embedded;

    @JsonProperty("_links")
    public HateoasLinks links;

    public Page page;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NotificationsEmbeddedList {
        public List<NotificationDto> notificationDtoList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HateoasLinks {
        Map<String, String> self;
        Map<String, String> first;
        Map<String, String> last;
        Map<String, String> next;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Page {
        public int size;
        public int totalElements;
        public int totalPages;
        public int number;
    }
}
