package com.example.geriadur.service;

import com.example.geriadur.domain.Etymon;
import com.example.geriadur.domain.SemanticField;
import com.example.geriadur.repositories.SemanticFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemanticFieldServiceImpl implements SemanticFieldService {
    @Autowired
    private SemanticFieldRepository semanticFieldRepository;

    @Override
    public List<SemanticField> getAllSemanticField() {
        return semanticFieldRepository.findAll();
    }

    @Override
    public void saveSematicField(SemanticField semanticField) {
        this.semanticFieldRepository.save(semanticField);
    }

    @Override
    public SemanticField getSemanticFieldById(Long id) {
        Optional<SemanticField> optional = semanticFieldRepository.findById(id);
        SemanticField semanticField = null;
        if (optional.isPresent()) {
            semanticField = optional.get();
        } else {
            throw new RuntimeException("No semantic field with this ID");
        }
        return semanticField;
    }

    @Override
    public List<Etymon> getListOfEtymonsById(Long id){
        SemanticField semanticField = getSemanticFieldById(id);
        return semanticField.getEtymons();
    }
}
