package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookPayloadDTO;
import com.example.demo.entity.BookViewDTO;
import com.example.demo.service.BookService;
import com.example.demo.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// R -> S -> C
@RestController
@RequestMapping("/api")
public class BookRestController {

    @Value("${upload.path}") //업로드 하려면 업로드 폴더가 필요하다 이값을 어플리케이션 yml에 넣어주면 파일경로를 읽어갈수 있다  path: src/main/resources/static/upload/
    private String uploadPath;

    @Autowired
    private BookService service;

    @Autowired
    private FileUploadService fileUploadService;

    // GET : http://localhost:8081/api/books
    @GetMapping("/books")
    public ResponseEntity<?> getAllList(){
        return new ResponseEntity<>(service.getAllList(), HttpStatus.OK);
    }


    // POST :  http://localhost:8081/api/books
    @PostMapping("/books")
    public ResponseEntity<?> register(@RequestBody BookPayloadDTO payloadDTO){ //임시로 받고테스트하려고 페이로드 DTO를 만들어 구현
        Book book = new Book();
        book.setTitle(payloadDTO.getTitle());
        book.setPrice(payloadDTO.getPrice());
        book.setAuthor(payloadDTO.getAuthor());
        book.setPage(payloadDTO.getPage());
        book = service.register(book);
        //응답에 필요한 데이터를 저장하는 DTO 가 있으면 더좋다
        BookViewDTO bookViewDTO = new BookViewDTO();
        bookViewDTO.setTitle(book.getTitle());
        bookViewDTO.setAuthor(book.getAuthor());
          return new ResponseEntity<>(bookViewDTO, HttpStatus.OK);
    }

    // GET :  http://localhost:8081/api/books/{id}
    @GetMapping("/books/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try {
            return new ResponseEntity<>(service.getById(id), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Book not found with id:" + id, HttpStatus.NOT_FOUND);
        }
    }

    // PUT :  http://localhost:8081/api/books/{id}
    @PutMapping("/books/{id}")                               // {  "title" : "자바의 신신", "price" : 20000  }
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookPayloadDTO payloadDTO){ //수정할 데이터를 페이로드로 받음
        try {
            Book book = service.getById(id); //3번 책이 존재 한다.   >> 책 id값으로 책정보를 가져왔다 새로 등록하려고 가지고 온게 아니라 수정하려고 가지고온것이다
            book.setTitle(payloadDTO.getTitle());//페이로드DTO 요청받은데이터를 가지고와 book 테이블과 연동되는 곳에 set으로 한다
            book.setAuthor(payloadDTO.getAuthor());
            book=service.register(book); //수정으로 간다 이미 있는 id갑이라서  save()와 연결  그래서 그 해당데이터만 수정한다
            BookViewDTO bookViewDTO = new BookViewDTO();
            bookViewDTO.setTitle(book.getTitle());
            bookViewDTO.setAuthor(book.getAuthor());
            return new ResponseEntity<>(bookViewDTO, HttpStatus.OK); //book 테이블 수정된 값을 가져와 이정보만 보려고 이렇게 커스텀 마이징 한것이다
        }catch (Exception e){
            return new ResponseEntity<>("Not Updated", HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE : http://localhost:8081/api/books/{id}
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
          try {
              service.deleteById(id);
              return new ResponseEntity<>("Deleted book with id:"+id , HttpStatus.OK);
          }catch(Exception e){
              return new ResponseEntity<>("Failed : " + id,HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }

    //파일업로드 추가
    @PostMapping("/books/save")
    public ResponseEntity<?> saveBook(@RequestParam("title") String title,
                                      @RequestParam("price") int price,
                                      @RequestParam("author") String author,
                                      @RequestParam("page") int page,
                                      @RequestParam("image_path") MultipartFile file) { //파일은 멀티파트로 받아야 한다
        try {
            String uniqueFilename = fileUploadService.saveFile(file);
            Book book = new Book();
            book.setTitle(title);
            book.setPrice(price);
            book.setAuthor(author);
            book.setPage(page);
            book.setOriginalFileName(uniqueFilename);
            book = service.register(book);

            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 이미를 클라이언트로 내려보내기
    @GetMapping("/image/{filename:.+}")//filename:.+  이뜻은 파일이름일수도 있지만 뒤에 확장자 도 올수가 있다 확장자 들이 포함된 파일이름이 올수도 있기떄문에 .+를 해주면 그 아래에 있는 경로를 다 읽을수 있다는것이다 dog.jpg, my_photo.png 등
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get(uploadPath, filename);  //yml파일에 경로저장 한 것을 불러와 finame랑 결합
        byte[] imageBytes = Files.readAllBytes(imagePath); //imagePath 이미지 경로를 readAllBytes 로 읽음 바이트 타입으로 imageBytes 에 저장 == 해당 경로의 파일을 바이트 배열로 전부 읽어옴
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Set appropriate content type based on image format    == 클라이언트에 응답할 때는 Content-Type을 꼭 지정해야 브라우저가 이미지로 인식함

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

}
