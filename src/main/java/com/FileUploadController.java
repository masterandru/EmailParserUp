package com;

import com.storage.StorageFileNotFoundException;
import com.storage.StorageService;
import com.utils.EmailExtractor;
import com.utils.EmailFormatter;
import com.utils.EmailSeparator;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    private EmailExtractor emailExtractor;
    @Autowired
    private EmailSeparator emailSeparator;
    @Autowired
    private EmailFormatter emailFormatter;

    private Logger logger = LogManager.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /*
        @Autowired
        public void wireEmailExtractor(EmailExtractor emailExtractor) {
            this.emailExtractor = emailExtractor;       }

        /*
         @Autowired
          public void wireEmailSeparator(EmailSeparator emailSeparator) {
              this.emailSeparator = emailSeparator;
          }
      */
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        logger.info("Call listUploadedFiles BEGIN");

        try {
            logger.info("---" + storageService.loadAll().collect(Collectors.toList()).toString());
            model.addAttribute("files", storageService.loadAll().map(
                    path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                            "serveFile", path.getFileName().toString()).build().toString())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            //logger.trace("Exception listUploadedFiles " + e);
            //logger.info("Exception listUploadedFiles " + e);
            logger.error("Exception listUploadedFiles " + e);

            // отправка почты
            //MailSender mailSender = new MailSender();
            //mailSender.sendMail("andruschenco@gmail.com",
            //        "andruschenco@gmail.com", "JavaMailSender", "Error: " + e);
        }

        logger.info("Call listUploadedFiles END");
        return "uploadForm";

    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        logger.info("Call serveFile ");
        Resource file = storageService.loadAsResource(filename);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/clear")
    public String handleClearFiles(Model model) {
        logger.info("Call handleClearFiles ");
        storageService.deleteAll();
        return "redirect:/";
    }


    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            logger.trace("Call handleFileUpload "); ///  -- загрузка файлов
            //logger.info(" - " + file.getClass().getName()); ///  -- загрузка файлов

            // Фильрация email адресов (извлекаем из файла)
            Set<String> emails = emailExtractor.getEmails(file.getInputStream());

            // Делим адреса на две группы  1-я группа адрес на gmail.com 2-я группа все остальные
            Map<Boolean, List<String>> gmailAndOtherEmails = emailSeparator.getDividedEmails(emails);

            String textEmailsList = emailFormatter.getFormattedEmails(gmailAndOtherEmails);
            logger.info("handleFileUpload " + textEmailsList);
/*
            StringBuilder emailsList = new StringBuilder();

            gmailAndOtherEmails.get(true)
                    .forEach((emailAddress) -> {                        // .forEach неявно  пеобразует в stream().forEach
                                logger.trace(emailAddress + ", ");
                                emailsList.append(emailAddress + ", ");
                            }
                    );
            logger.info("GMail EmailsList: Count:" + gmailAndOtherEmails.get(true).stream().count());

            gmailAndOtherEmails.get(false)
                    .forEach((emailAddress) -> {
                                logger.trace(emailAddress + ", ");
                                emailsList.append(emailAddress + ", ");
                            }
                    );
            logger.info("\nOther EmailsList: Count:" + gmailAndOtherEmails.get(false).stream().count());
            //gmailAndOtherEmails.get(true).stream().toArray();
            //gmailAndOtherEmails.get(false).stream().map(i -> i + ",").collect();
*/

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(textEmailsList.getBytes(Charset.forName("UTF-8")));
            logger.info("handleFileUpload " + byteArrayInputStream); ///  -- загрузка файлов

            //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(emails.toString().getBytes(Charset.forName("UTF-8")));
            //logger.info("handleFileUpload " + emails.toString().getBytes(Charset.forName("UTF-8"))); ///  -- загрузка файлов


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

            logger.error("IOException in handleFileUpload " + pe.getStackTrace()); ///  -- загрузка файлов
        }
           /*
        } catch (Exception e) {
            logger.error("ANY Exception in handleFileUpload " + e.getStackTrace()); ///  -- загрузка файлов
        }
*/


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
        redirectAttributes.addFlashAttribute("message", "Файл " + file.getOriginalFilename() + " загружен !");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
