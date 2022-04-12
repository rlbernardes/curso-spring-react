package com.lima.minhasFinancas.service.validation;

import com.lima.minhasFinancas.api.dto.LancamentoDTO;
import com.lima.minhasFinancas.model.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class LancamentoInsertValidator implements ConstraintValidator<LancamentoInsert, LancamentoDTO> {

    LancamentoRepository repository;

    @Autowired
    public LancamentoInsertValidator(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(LancamentoInsert ann){

    }

    public boolean isValid(LancamentoDTO objDto, ConstraintValidatorContext context){
       return false;
    }
}
