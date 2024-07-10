package kr.or.kmi.mis.api.order.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.or.kmi.mis.api.order.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/bsc/order")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    @PostMapping("/excel")
    public void downloadExcel(HttpServletResponse response, @RequestBody List<Long> draftIds) throws IOException {
        excelService.downloadExcel(response, draftIds);
    }
}
