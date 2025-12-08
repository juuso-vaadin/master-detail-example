package com.example.application.service;

import com.example.application.data.Message;
import com.example.application.data.Person;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class DataService {

    public List<Person> getAllPeople() {
        return Arrays.asList(
                new Person("Henry", "Henry", 3331296L, "Henry"),
                new Person("Liam", "Liam", 1012997L, "Liam"),
                new Person("Justin", "Justin", 3079296L, "Justin"),
                new Person("Jordan", "Jordan", 3514346L, "Jordan"),
                new Person("Jacob", "Jacob", -542669L, "Jacob"),
                new Person("Robert", "Robert", -1322600L, "Robert"),
                new Person("Maya", "Maya", 3448602L, "Maya"),
                new Person("Andrew", "Andrew", -1700250L, "Andrew"),
                new Person("Samantha", "Samantha", 1429048L, "Samantha"),
                new Person("Angel", "Angel", -472648L, "Angel"),
                new Person("Henry", "Henry", 228200L, "Henry"),
                new Person("Liam", "Liam", 140900L, "Liam"),
                new Person("Justin", "Justin", -1170950L, "Justin"),
                new Person("Jordan", "Jordan", -2262550L, "Jordan"),
                new Person("Jacob", "Jacob", 528950L, "Jacob"),
                new Person("Robert", "Robert", 1535950L, "Robert"),
                new Person("Maya", "Maya", 1913750L, "Maya"),
                new Person("Andrew", "Andrew", 290600L, "Andrew"),
                new Person("Samantha", "Samantha", 4933400L, "Samantha"),
                new Person("Angel", "Angel", 2266200L, "Angel"),
                new Person("Henry", "Henry", 1563450L, "Henry"),
                new Person("Liam", "Liam", 1906850L, "Liam"),
                new Person("Justin", "Justin", 3331296L, "Justin"),
                new Person("Jordan", "Jordan", -1012997L, "Jordan"),
                new Person("Jacob", "Jacob", 3079296L, "Jacob"),
                new Person("Robert", "Robert", -3514346L, "Robert"),
                new Person("Maya", "Maya", -542669L, "Maya"),
                new Person("Andrew", "Andrew", -1322600L, "Andrew"),
                new Person("Samantha", "Samantha", 3448604L, "Samantha")
        );
    }

    public List<Message> getAllMessages() {
        LocalDateTime baseTime = LocalDateTime.now().minusHours(2);

        return Arrays.asList(
                new Message("Aurora Velasco",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        baseTime.minusMinutes(30),
                        "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150", 1),
                new Message("Alicia Thomas",
                        "Nullam sapien justo, aliquam sit amet est non, finibus dapibus odio. Duis fringilla turpis eget viverra tristique. Mauris non ornare enim.",
                        baseTime.minusMinutes(20),
                        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", 2),
                new Message("Anthony Robinson",
                        "Nullam ut purus eros.",
                        baseTime.minusMinutes(10),
                        "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150", 3)
        );
    }
}
