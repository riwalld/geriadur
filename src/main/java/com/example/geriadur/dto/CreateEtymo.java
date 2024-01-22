package com.example.geriadur.dto;

import com.example.geriadur.domain.LiteralTranslation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateEtymo {


    private String currentName;

    private String etymoName;

    private List<String> wordStems;

    private String descrFr;

    private String descrEng;

    private Long wordTheme;

    private Long semanticField;

    private LiteralTranslation litTrans;

}
