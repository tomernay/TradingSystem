/*
package Presentaion;

import DataBase.Files.FilesHandler;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.Subscriber.Messages.NormalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Welcome {

    @GetMapping("/")
    public String welcome() {
        return "redirect:/t.html";
    }

    @GetMapping("/yair")
    public String yair() {
        return "redirect:/yair.html";
    }
    @GetMapping("/paymentPage")
    public String paymentPage() {
        return "redirect:/payment.html";
    }


   */
/* @GetMapping("/messages")
    public String messagesDisplay() {
        List<Message> messages = new ArrayList<>();
        messages.add(new NormalMessage("YAr"));
        messages.add(new NormalMessage("YAt"));

        ObjectMapper objectMapper = new ObjectMapper();

            // Convert messages list to JSON string
        JsonNode node= null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            node= mapper.valueToTree(messages);


            // Convert JSON object to a JSON string
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);

            ResourceLoader resourceLoader = new ClassRelativeResourceLoader(Welcome.class);
            Resource resource = resourceLoader.getResource("classpath:static/jsons/y.json");

            // Read the file contents into a String
            Path filePath = Paths.get(resource.getURI());
            String jsonContent = Files.readString(filePath, StandardCharsets.UTF_8);
            System.out.println(jsonContent);
            Files.writeString(filePath, jsonString, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }

            // Redirect to messages.html
            return "redirect:/messages.html";


        // In case of an error, return an empty string or an error view

    }*//*




}




*/
