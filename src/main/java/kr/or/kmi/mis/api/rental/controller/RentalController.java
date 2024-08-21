package kr.or.kmi.mis.api.rental.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rental")
@RequiredArgsConstructor
@Tag(name="RentalCRUD", description = "렌탈현황 관련 CRUD API")
public class RentalController {


}
