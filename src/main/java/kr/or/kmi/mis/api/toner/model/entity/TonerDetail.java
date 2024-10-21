package kr.or.kmi.mis.api.toner.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "hrmtptnd")
@IdClass(TonerDetailId.class)
public class TonerDetail {

    @Id
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Id
    @ManyToOne
    @JoinColumn(name = "draft_id", nullable = false)
    private TonerMaster draftId;

    @Column(name = "mng_num", length = 10)
    private String mngNum;

    @Column(name = "team_nm", length = 20)
    private String teamNm;

    @Column(name = "location", length = 50)
    private String location;

    @Column(name = "print_nm", length = 100)
    private String printNm;

    @Column(name = "toner_nm", length = 100)
    private String tonerNm;

    @Column(name = "color", length = 1)
    private String color;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price", length = 20)
    private String price;






}
