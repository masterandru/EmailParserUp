package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    private Logger logger = LogManager.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        //logger.info("Call listUploadedFiles ");
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        //logger.info("Call serveFile ");
        Resource file = storageService.loadAsResource(filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Call handleFileUpload "); ///  -- загрузка файлов
            logger.info(" - " + file.getClass().getName()); ///  -- загрузка файлов

            try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
                Pattern pattern = Pattern.compile("\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b", Pattern.CASE_INSENSITIVE);
                Set<String> emails = new HashSet<>();
                br.lines().forEach(
                        line -> {
                            //logger.info("2 - " + line);
                            Matcher matcher = pattern.matcher(line);
                            while (matcher.find()) {
                                emails.add(matcher.group());
                            }
                        }
                );

                // Делим адреса на две части
                Map<Boolean, List<String>> gmailAndOtherEmails = emails.stream().sorted().collect(Collectors.partitioningBy((s) -> s.contains("@gmail.com")));

                StringBuilder emailsList = new StringBuilder();

                gmailAndOtherEmails.get(true).stream()
                        .forEach((x) -> {
                                    logger.info(x + ", ");
                            emailsList.append(x + ", ");
                                    //outGmail.println(x);
                                }
                        );
                logger.info("GMail EmailsList: Count:" + gmailAndOtherEmails.get(true).stream().count());

                gmailAndOtherEmails.get(false).stream()
                        .forEach((x) -> {
                                    logger.info(x + ", ");
                            emailsList.append(x + ", ");
                                    //outMail.println(x);
                                }
                        );
                logger.info("\nOther EmailsList: Count:" + gmailAndOtherEmails.get(false).stream().count());
                //gmailAndOtherEmails.get(true).stream().toArray();
                //gmailAndOtherEmails.get(false).stream().map(i -> i + ",").collect();

                ByteArrayInputStream byteArrayInputStream = new
                        ByteArrayInputStream(emails.toString().getBytes(Charset.forName("UTF-8")));
                logger.info("handleFileUpload " + emails.toString().getBytes(Charset.forName("UTF-8"))); ///  -- загрузка файлов

                //logger.info("handleFileUpload " + file.getInputStream().getClass().getName()); ///  -- загрузка файлов
                storageService.store(byteArrayInputStream, file.getOriginalFilename());


                /*
                // отправка почты
                logger.info("GMail EmailsList: " + gmailAndOtherEmails.get(false).toString());
                MailSender mailSender = new MailSender();
                String from = "andruschenco@gmail.com";
                String to = "andruschenco@gmail.com,andruschenco@mail.ru";
                //String to = "chereshka.org@gmail.com";
                String subject = "JavaMailSender";
                String body = "Ловите ссылку!  https://disk.yandex.ru/client/disk";
                //String body = gmailAndOtherEmails.get(false).toString();
                mailSender.sendMail(from, to, subject, body);
                */

            } catch (IOException pe) {
                pe.printStackTrace();
            }
        } catch (Exception e) {
            logger.info("ANY Exception in handleFileUpload " + e.getStackTrace()); ///  -- загрузка файлов
        }



/*
        StringBuilder nn = new StringBuilder();
        nn.toString().MultipartFile standardMultipartFile = new StandardMultipartFile();
        InputStream emailsStream = new ByteArrayInputStream();
*/

//        try {
        //java.io.FileInputStream
        //FileInputStream
        //ByteArrayInputStream
        //file.getInputStream();


//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream();
//            logger.info("handleFileUpload " + file.getInputStream().getClass().getName()); ///  -- загрузка файлов
//            storageService.store(byteArrayInputStream, file.getOriginalFilename());

//        } catch (IOException pe) {
//            pe.printStackTrace();
//        }

        //storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
