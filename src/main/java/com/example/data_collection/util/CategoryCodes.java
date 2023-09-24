package com.example.data_collection.util;

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

    private final Map<String, String> SSG = new HashMap<>();
    private final Map<String, List<String>> SSG_CODES_MAP = new HashMap<>();

    public CategoryCodes() {

        //간식
//        SSG.put("과자/쿠키/파이", "6000093747");
//        SSG.put("떡/한과/전통과자", "6000093748");
        SSG.put("누룽지/원물간식", "6000093822");
        SSG.put("김스낵/부각/튀각", "6000093825");
        SSG.put("기타스낵", "6000093827");
        SSG.put("사탕/캬라멜/껌", "6000093750");
//        SSG.put("젤리/푸딩", "6000093751");
        SSG.put("초콜릿/초코바", "6000092902");
//        SSG.put("시리얼", "6000093753");
        SSG.put("빙과/아이스크림", "6000093754");
//        SSG.put("베이커리/잼", "6000092903");
//        SSG.put("과자/스낵/시리얼", "6000095148");
//        SSG.put("과일/스낵치즈", "6000093630");

        //육가공
        SSG.put("돈가스/커틀릿", "6000093656");
//        SSG.put("치킨너겟/텐더/닭꼬치", "6000093657");
        SSG.put("훈제오리/족발/곱창/볶음/찜", "6000093609");
        SSG.put("떡갈비/함박스테이크", "6000093660");
        SSG.put("동그랑땡/완자", "6000093661");

        //즉석섭취식품
        SSG.put("만두/전병","6000093654");
        SSG.put("감자튀김", "6000093658");
//        SSG.put("치즈스틱/기타튀김", "6000093659");
        SSG.put("전/기타구이", "6000093662");
        SSG.put("유부/묵/떡", "6000093608");
//        SSG.put("피자/핫도그/파스타/떡볶이", "6000093606");
        SSG.put("중식/일식/세계요리", "6000093607");
        SSG.put("즉석밥/컵밥", "6000217750");
//        SSG.put("카레/짜장/즉석요리", "6000217754");
        SSG.put("라면", "6000093615");
//        SSG.put("즉석면요리", "6000093616");
        SSG.put("파스타면/생면/건면", "6000093617");
        SSG.put("헬스보충제", "6000094877");
        SSG.put("냉장/냉동/간편식", "6000095144");
        SSG.put("라면/즉석식품", "6000095259");

        //음료
        SSG.put("탄산음료", "6000093764");
        SSG.put("과일/야채음료", "6000093765");
//        SSG.put("스포츠/이온음료", "6000093957");
//        SSG.put("비타민/식이섬유음료", "6000093958");
//        SSG.put("에너지드링크", "6000093960");
//        SSG.put("전통/차/기타음료", "6000093767");
        SSG.put("커피믹스", "6000094002");
//        SSG.put("커피음료", "6000093780");
        SSG.put("홍차/밀크티", "6000094007");
//        SSG.put("과실/곡물/전통차", "6000094009");
        SSG.put("코코아/핫초코", "6000093786");
        SSG.put("우유", "6000094122");
        SSG.put("요거트/요구르트", "6000093597");
//        SSG.put("두유", "6000093598");
        SSG.put("우유/유제품", "6000095214");
        SSG.put("음료", "6000095274");


        SSG_CODES_MAP.put("간식", Arrays.asList(
                "6000093747", "6000093748", "6000093822",
                "6000093825", "6000093827", "6000093750",
                "6000093751", "6000092902", "6000093753",
                "6000093754", "6000092903", "6000095148",
                "6000093630"
        ));
        SSG_CODES_MAP.put("육가공", Arrays.asList(
                "6000093656", "6000093657", "6000093609",
                "6000093660", "6000093661"
        ));
        SSG_CODES_MAP.put("즉석섭취식품", Arrays.asList(
                "6000093654", "6000093658", "6000093659",
                "6000093662", "6000093608", "6000093606",
                "6000093607", "6000217750", "6000217754",
                "6000093615", "6000093616", "6000093617",
                "6000094877", "6000095144", "6000095259"
        ));
        SSG_CODES_MAP.put("음료", Arrays.asList(
                "6000093764", "6000093765", "6000093957",
                "6000093958", "6000093960", "6000093767",
                "6000094002", "6000093780", "6000094007",
                "6000094009", "6000093786", "6000094122",
                "6000093597", "6000093598", "6000095214",
                "6000095274"
        ));

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

        //육가공
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
