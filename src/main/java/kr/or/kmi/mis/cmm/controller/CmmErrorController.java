package kr.or.kmi.mis.cmm.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * packageName    : mng.mrk.cmm.controller
 * fileName       : CmmErrorController
 * author         : clsung
 * date           : 2024-04-11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-04-11        clsung       the first create
 */
@Controller
public class CmmErrorController implements ErrorController {
    private final String ERROR_PATH = "/error";

    /**
     * Redirect root string.
     *
     * @return the string
     */
    @GetMapping(ERROR_PATH)
    public String redirectRoot(){
        return "index.html";
    }

    /**
     * Get error path string.
     *
     * @return the string
     */
    public String getErrorPath(){
        return null;
    }
}
