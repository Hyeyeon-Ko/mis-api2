package kr.or.kmi.mis.api.order.service.Impl;

import kr.or.kmi.mis.api.bcd.model.entity.BcdDetail;
import kr.or.kmi.mis.api.bcd.model.entity.BcdMaster;
import kr.or.kmi.mis.api.bcd.repository.BcdDetailRepository;
import kr.or.kmi.mis.api.bcd.repository.BcdMasterRepository;
import kr.or.kmi.mis.api.exception.EntityNotFoundException;
import kr.or.kmi.mis.api.noti.service.NotificationSendService;
import kr.or.kmi.mis.api.order.model.request.OrderRequestDTO;
import kr.or.kmi.mis.api.order.model.response.EmailSettingsResponseDTO;
import kr.or.kmi.mis.api.order.model.response.OrderListResponseDTO;
import kr.or.kmi.mis.api.order.service.ExcelService;
import kr.or.kmi.mis.api.order.service.OrderService;
import kr.or.kmi.mis.api.std.model.entity.StdDetail;
import kr.or.kmi.mis.api.std.model.entity.StdGroup;
import kr.or.kmi.mis.api.std.repository.StdDetailRepository;
import kr.or.kmi.mis.api.std.repository.StdGroupRepository;
import kr.or.kmi.mis.api.std.service.StdBcdService;
import kr.or.kmi.mis.api.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BcdMasterRepository bcdMasterRepository;
    private final BcdDetailRepository bcdDetailRepository;
    private final StdGroupRepository stdGroupRepository;
    private final StdDetailRepository stdDetailRepository;
    private final ExcelService excelService;
    private final StdBcdService stdBcdService;
    private final NotificationSendService notificationSendService;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDTO> getOrderList(String instCd) {

        List<BcdMaster> bcdMasterList = bcdMasterRepository.findAllByStatusAndOrderDateIsNull("B")
                .orElseThrow(() -> new EntityNotFoundException("BcdMaster"));

        return bcdMasterList.stream()
                .map(bcdMaster -> {
                    BcdDetail bcdDetail = bcdDetailRepository.findById(bcdMaster.getDraftId())
                            .orElseThrow(() -> new EntityNotFoundException("BcdDetail"));

                    if (bcdDetail.getInstCd().equals(instCd)) {
                        Integer quantity = bcdDetail.getQuantity();

                        return OrderListResponseDTO.builder()
                                .draftId(bcdDetail.getDraftId())
                                .instNm(stdBcdService.getInstNm(bcdDetail.getInstCd()))
                                .title(bcdMaster.getTitle())
                                .draftDate(bcdMaster.getDraftDate())
                                .respondDate(bcdMaster.getRespondDate())
                                .drafter(bcdMaster.getDrafter())
                                .quantity(quantity)
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void orderRequest(OrderRequestDTO orderRequest, MultipartFile previewFile, List<MultipartFile> files) throws IOException, GeneralSecurityException {
        byte[] previewFileData;

        if (previewFile != null && !previewFile.isEmpty()) {
            String extension = FilenameUtils.getExtension(previewFile.getOriginalFilename());
            if ("xlsx".equalsIgnoreCase(extension)) {
                previewFileData = excelService.getEncryptedExcelBytes(previewFile.getBytes(), "06960");
            } else {
                throw new IllegalArgumentException("미리보기 파일은 엑셀(.xlsx) 형식만 허용됩니다.");
            }
        } else {
            previewFileData = excelService.getEncryptedExcelBytes(excelService.generateExcel(orderRequest.getDraftIds()), "06960");
        }

        emailService.sendEmailWithDynamicCredentials(
                "smtp.sirteam.net",
                465,
                orderRequest.getFromEmail(),
                orderRequest.getPassword(),
                orderRequest.getFromEmail(),
                orderRequest.getToEmail(),
                orderRequest.getEmailSubject(),
                orderRequest.getEmailBody(),
                previewFileData,
                files,
                orderRequest.getFileName()
        );

        orderRequest.getDraftIds().forEach(draftId -> {
            BcdMaster bcdMaster = bcdMasterRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("Draft not found with id " + draftId));
            bcdMaster.updateOrder(LocalDateTime.now());
            bcdMasterRepository.save(bcdMaster);

            BcdDetail bcdDetail = bcdDetailRepository.findById(draftId)
                    .orElseThrow(() -> new EntityNotFoundException("BcdDetail not found with id " + draftId));
            notificationSendService.sendBcdOrder(bcdMaster.getDraftDate(), bcdDetail.getUserId());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] previewOrderFile(List<String> draftIds) throws IOException {
        return excelService.generateExcel(draftIds);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailSettingsResponseDTO getEmailSettings() {
        StdGroup stdGroup = stdGroupRepository.findByGroupCd("B003")
                .orElseThrow(() -> new EntityNotFoundException("B003"));
        StdDetail stdDetail = stdDetailRepository.findByGroupCdAndDetailCd(stdGroup, "001")
                .orElseThrow(() -> new EntityNotFoundException("001"));

        return new EmailSettingsResponseDTO(stdDetail.getEtcItem2());
    }
}
