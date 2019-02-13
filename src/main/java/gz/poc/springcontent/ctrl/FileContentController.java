/*
 * Copyright © 2018 Capco. All rights reserved.
 * Capco BlockWorkx
 *
 * You may obtain a copy of the License at
 *
 * capco-blockworkx-license.txt file under the top-level jar file
 *
 */
package gz.poc.springcontent.ctrl;

import gz.poc.springcontent.model.File;
import gz.poc.springcontent.repo.FileContentStore;
import gz.poc.springcontent.repo.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
public class FileContentController {
    @Autowired
    private FileRepository filesRepo;
    @Autowired
    private FileContentStore contentStore;

    @PutMapping(path="/files/{fileId}")
    public ResponseEntity<?> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Optional<File> f = filesRepo.findById(id);
        if (f.isPresent()) {
            f.get().setMimeType(file.getContentType());
            contentStore.setContent(f.get(), file.getInputStream());
            filesRepo.save(f.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.debug("File #{} doesn't exist yet, returning without save", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path="/files/{fileId}")
    public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) throws IOException {
        Optional<File> f = filesRepo.findById(id);
        if (f.isPresent()) {
            InputStreamResource inputStreamResource = new InputStreamResource(contentStore.getContent(f.get()));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(f.get().getContentLength());
            headers.set("Content-Type", f.get().getMimeType());
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        } else {
            log.debug("File #{} doesn't exist yet, returning without save", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
