package rigor.io.irent.house;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

@RestController
@CrossOrigin
public class ImageController {

  private final String base = "/api/images"; // weird?
  private TokenService tokenService;

  public ImageController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @GetMapping("/print")
  public void print(@RequestParam String file) {
    System.out.println("here in print");
    System.out.println(file);
  }

  @GetMapping(base + "/{pic}")
  public void getImage(HttpServletResponse response,
                       @PathVariable String pic)
      throws IOException {

    String path = "upload-dir/" + pic;
    InputStream in = new FileInputStream(path);
//    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    IOUtils.copy(in, response.getOutputStream());
    in.close();
  }

  @PostMapping("/api/images")
  public ResponseEntity<?> upload(@RequestPart(name = "file") MultipartFile multipartFile,
                                  @RequestParam String token) throws IOException {

    if (!tokenService.isValid(token))
      return notAuthorized();

    InputStream in = multipartFile.getInputStream();
    String ofn = multipartFile.getOriginalFilename();

    String u = tokenService.fetchUser(token).getName();
    String name = ofn+u;

    String fileName = Base64.getEncoder().withoutPadding().encodeToString(name.getBytes()) + "." + ofn.split("\\.")[1];
    Path path = Paths.get("upload-dir/" + fileName);
    File file = new File(path.toUri());
    if (file.exists())
      file.delete();
    Files.copy(in, path);
    in.close();
    return new ResponseEntity<>(new HashMap<String, String>() {{
      put("status", "Success");
      put("message", "/" + path.getFileName());
    }}, HttpStatus.ACCEPTED);
  }

  private ResponseEntity<HashMap<String, Object>> notAuthorized() {
    return new ResponseEntity<>(createMap("error", "not authorized"), HttpStatus.OK);
  }

  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }

}
