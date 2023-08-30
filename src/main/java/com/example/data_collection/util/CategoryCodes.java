package com.example.data_collection.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
public class CategoryCodes {

    private final Map<String, Integer> OASIS = new HashMap<>();
    private final Map<String, List<Integer>> OASIS_CODES_MAP = new HashMap<>();

    public CategoryCodes() {
        //간식
        OASIS.put("빵/샌드위치", 1101);
        OASIS.put("쿠키/케이크", 1102);
        OASIS.put("견과/병조림", 13);
        OASIS.put("치즈/요구르트/버터", 51);
        OASIS.put("과자/간식", 25);
        OASIS.put("떡/빵/한과/엿", 248);
        OASIS.put("선식류", 24);
        OASIS.put("사탕/젤리", 26);
        OASIS.put("시리얼", 249);
        OASIS.put("아침식사대용", 60);

        //수산가공
        OASIS.put("어묵/수산가공", 21);
        OASIS.put("닭/오리/유정란", 16);
        OASIS.put("육가공/족발", 17);

        //즉석섭취식품
        OASIS.put("채소/샐러드", 137);
        OASIS.put("파스타/면류", 64);
        OASIS.put("카레/스프", 242);
        OASIS.put("두부/어묵/햄/만두류", 56);
        OASIS.put("밑반찬/절임류", 34);
        OASIS.put("1인반찬", 243);
        OASIS.put("죽/스프/카레", 23);
        OASIS.put("전/떡/묵/부침", 45);
        OASIS.put("간편식사", 57);
        OASIS.put("베스트 야식", 87);
        OASIS.put("국/탕/찌개", 33);

        // 음료
        OASIS.put("생수/음료/커피", 28);
        OASIS.put("식혜/수정과류", 144);
        OASIS.put("곡물/티백/전통차", 145);
        OASIS.put("천연과즙", 48);
        OASIS.put("우유/두유/유제품", 27);

        OASIS_CODES_MAP.put("간식", Arrays.asList(1101, 1102, 13, 51, 25, 248, 24, 26, 249, 60));
        OASIS_CODES_MAP.put("육가공", Arrays.asList(21, 16, 17));
        OASIS_CODES_MAP.put("즉석섭취식품", Arrays.asList(137, 64, 242, 56, 34, 243, 23, 45, 57, 87, 33));
        OASIS_CODES_MAP.put("음료", Arrays.asList(28, 144, 145, 48, 27));
    }

}
