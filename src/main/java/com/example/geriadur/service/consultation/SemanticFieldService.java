package com.example.geriadur.service.consultation;

import com.example.geriadur.entity.SemanticField;
import com.example.geriadur.entity.consultation.WordStem;
import com.example.geriadur.repositories.SemanticFieldRepository;
import com.example.geriadur.service.consultation.api.ISemanticFieldService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SemanticFieldService implements ISemanticFieldService {
    
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
            throw new RuntimeException("No semantic field with this ID: "+id);
        }
        return semanticField;
    }

    @Override
    public Set<WordStem> getListOfEtymonsBySemField(Long id){
        SemanticField semanticField = getSemanticFieldById(id);
        return semanticField.getWordStems();
    }
}
